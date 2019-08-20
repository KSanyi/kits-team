package hu.kits.team.infrastructure.email;

import java.lang.invoke.MethodHandles;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.kits.team.domain.email.Email;
import hu.kits.team.domain.email.EmailSender;

public class SendGridEmailSender implements EmailSender {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private final String sendGridUser;
    
    private final String sendGridPassword;
    
    public SendGridEmailSender(String sendGridUser, String sendGridPassword) {
        this.sendGridUser = sendGridUser;
        this.sendGridPassword = sendGridPassword;
        
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap(); 
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html"); 
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed"); 
        mc.addMailcap("text/calendar;; x-java-content-handler=com.sun.mail.handlers.text_plain");
    }

    @Override
    public void sendEmail(Email email) {
        
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", "smtp.sendgrid.net");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.auth", "true");

        Authenticator auth = new SMTPAuthenticator(sendGridUser, sendGridPassword);
        Session mailSession = Session.getDefaultInstance(props, auth);
        
        try {
            Transport transport = mailSession.getTransport();

            MimeMessage message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(email.sender));
            message.setSubject(email.subject);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email.recipient));
            message.addRecipient(Message.RecipientType.CC, new InternetAddress("kocso.sandor.gabor@gmail.com"));
            message.setContent(createContent(email));

            transport.connect();
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            log.info("Email sent: {}", email);
        } catch(Exception ex) {
            log.error("Error during email sending", ex);
            throw new RuntimeException(ex);
        }
    }
    
    private static Multipart createContent(Email email) throws MessagingException {
        
        Multipart multipart = new MimeMultipart("alternative");

        BodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(email.content, "text/html; charset=utf-8");
        multipart.addBodyPart(bodyPart);
        
        if(email.calendarAttachment.isPresent()) {
            BodyPart calendarPart = new MimeBodyPart();
            calendarPart.addHeader("Content-Class", "urn:content-classes:calendarmessage");
            calendarPart.setContent(email.calendarAttachment.get().formatToIcal(), "text/calendar; charset=utf-8");
            
            multipart.addBodyPart(calendarPart);
        }
        
        return multipart;
    }

    private static class SMTPAuthenticator extends javax.mail.Authenticator {
        
        private final String sendGridUser;
        
        private final String sendGridPassword;
        
        public SMTPAuthenticator(String sendGridUser, String sendGridPassword) {
            this.sendGridUser = sendGridUser;
            this.sendGridPassword = sendGridPassword;
        }

        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(sendGridUser, sendGridPassword);
        }
    }
    
}
