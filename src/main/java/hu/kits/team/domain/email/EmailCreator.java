package hu.kits.team.domain.email;

import hu.kits.team.common.Formatters;
import hu.kits.team.domain.MatchData;
import hu.kits.team.domain.Member;

public class EmailCreator {

    private static final String JONNY = "jonny@luzerfc.hu";
    
    public static Email createCalendarEntryEmail(Member member, MatchData matchData) {
        
        String subject = "Meccs: " + Formatters.formatDateTime(matchData.time) + " vs " + matchData.opponent;
        return new Email(JONNY, member.email, subject, "Mentsd el a naptárodba!", new CalendarAttachment(matchData));
    }
    
    public static Email createReminderEmail(Member member, MatchData matchData) {
        
        String subject = "Meccs emlékeztető: " + Formatters.formatDateTime2(matchData.time) + " vs " + matchData.opponent;
        String content = EmailContentCreator.createReminderEmailContent(member, matchData);
        return new Email(JONNY, member.email, subject, content);
    }
    
}
