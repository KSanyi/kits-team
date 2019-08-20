package hu.kits.team;

import hu.kits.team.domain.email.Email;
import hu.kits.team.domain.email.EmailSender;

public class SpyEmailSender implements EmailSender {

    private Email lastEmailSent;
    
    @Override
    public void sendEmail(Email email) {
        lastEmailSent = email;
    }

    public Email getLastEmailSent() {
        return lastEmailSent;
    }

}
