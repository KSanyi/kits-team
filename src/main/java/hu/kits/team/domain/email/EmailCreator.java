package hu.kits.team.domain.email;

import hu.kits.team.common.Formatters;
import hu.kits.team.domain.MatchData;
import hu.kits.team.domain.Member;

public class EmailCreator {

    public static Email createCalendarEntryEmail(Member member, MatchData matchData) {
        
        String subject = "Meccs: " + Formatters.formatDateTime(matchData.time) + " vs " + matchData.opponent;
        return new Email("info@luzerfc.hu", member.email, subject, "Mentsd el a naptárodba!", new CalendarAttachment(matchData));
    }
    
    public static Email createReminderEmail(Member member, MatchData matchData) {
        
        String subject = "Meccs emlékeztető: " + Formatters.formatTimeWithWeekday(matchData.time) + " vs " + matchData.opponent;
        String content = EmailContentCreator.createReminderEmailContent(member, matchData);
        return new Email("reminder@luzerfc.hu", member.email, subject, content);
    }
    
}
