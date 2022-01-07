package hu.kits.team.infrastructure.email;

import java.lang.invoke.MethodHandles;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Personalization;

import hu.kits.team.domain.email.Email;
import hu.kits.team.domain.email.EmailSender;

public class SendGridEmailSender implements EmailSender {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private final SendGrid sendGrid;
    
    public SendGridEmailSender(String sendGridPassword) {
        sendGrid = new SendGrid(sendGridPassword);
    }

    @Override
    public boolean sendEmail(Email email) {
        
        try {
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            
            Mail mail = new Mail();
            mail.setFrom(new com.sendgrid.helpers.mail.objects.Email(email.sender()));
            mail.setSubject(email.subject());
            mail.addContent(new Content("text/html", email.content()));
            
            Personalization personalization = new Personalization();
            personalization.addTo(new com.sendgrid.helpers.mail.objects.Email(email.recipient()));
            mail.addPersonalization(personalization);
            
            if(email.calendarAttachment().isPresent()) {
                Attachments attachments = new Attachments();
                String base64EncodedAttachment = Base64.getEncoder().encodeToString(email.calendarAttachment().get().formatToIcal().getBytes());
                attachments.setContent(base64EncodedAttachment);
                attachments.setType("text/calendar");
                attachments.setFilename("calendar");
                mail.addAttachments(attachments);
            }
            
            request.setBody(mail.build());
            Response response = sendGrid.api(request);
            
            if(response.getStatusCode() == 202) {
                log.info("Email sent: {}", email);
                return true;
            } else {
                log.error("Error sending email: {}, status code: {}, body: {}", email, response.getStatusCode(), response.getBody());
                return false;
            }
        } catch(Exception ex) {
            log.error("Error during email sending", ex);
            return false;
        }
    }
    
}
