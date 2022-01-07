package hu.kits.team.domain.email;

import java.util.Optional;

public record Email(String sender, String recipient, String subject, String content, Optional<CalendarAttachment> calendarAttachment) {

    public Email(String sender, String recipient, String subject, String content) {
        this(sender, recipient, subject, content, Optional.empty());
    }
    
    public Email(String sender, String recipient, String subject, String content, CalendarAttachment calendarAttachment) {
        this(sender, recipient, subject, content, Optional.of(calendarAttachment));
    }
    
    @Override
    public String toString() {
        return String.format("To: %s subject: %s", recipient, subject);
    }
    
}
