package hu.kits.team;

import hu.kits.team.domain.email.Email;
import hu.kits.team.domain.email.EmailSender;

public class SpyEmailSender implements EmailSender {

    private Email lastEmailSent;
    
    @Override
    public boolean sendEmail(Email email) {
        lastEmailSent = email;
        return true;
    }

    public Email getLastEmailSent() {
        return lastEmailSent;
    }

}
