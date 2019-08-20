package hu.kits.team.domain.email;

import java.util.Optional;

public class Email {

    public final String sender;
    
    public final String recipient;
    
    public final String subject;
    
    public final String content;

    public final Optional<CalendarAttachment> calendarAttachment;
    
    public Email(String sender, String recipient, String subject, String content) {
        this(sender, recipient, subject, content, Optional.empty());
    }
    
    public Email(String sender, String recipient, String subject, String content, CalendarAttachment calendarAttachment) {
        this(sender, recipient, subject, content, Optional.of(calendarAttachment));
    }
    
    private Email(String sender, String recipient, String subject, String content, Optional<CalendarAttachment> calendarAttachment) {
        this.sender = sender;
        this.recipient = recipient;
        this.subject = subject;
        this.content = content;
        this.calendarAttachment = calendarAttachment;
    }
    
    @Override
    public String toString() {
        return String.format("To: %s subject: %s", recipient, subject);
    }
    
}
