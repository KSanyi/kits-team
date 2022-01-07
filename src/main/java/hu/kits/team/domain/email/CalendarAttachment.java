package hu.kits.team.domain.email;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import hu.kits.team.Main;
import hu.kits.team.common.Clock;
import hu.kits.team.domain.MatchData;

public class CalendarAttachment {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
    
    private final MatchData matchData;

    public CalendarAttachment(MatchData matchData) {
        this.matchData = matchData;
    }
    
    public String formatToIcal() {
        
        String timestamp = DATE_TIME_FORMAT.format(Clock.now());
        String start = DATE_TIME_FORMAT.format(matchStart());
        String end = DATE_TIME_FORMAT.format(matchEnd());
        
        return 
            "BEGIN:VCALENDAR\n" + 
            "ORGANIZER:luzerfc\n" +
            "PRODID:luzerfc\n" + 
            "VERSION:2.0\n" + 
            "METHOD:PUBLISH\n" + 
            "BEGIN:VEVENT\n" + 
            "DTSTART;TZID=Europe/Budapest:" + start + "\n" +
            "DTEND;TZID=Europe/Budapest:" + end + "\n" + 
            "LOCATION:" + location() + "\n" +
            "SUMMARY:" + summary() + "\n" +
            "DTSTAMP:" + timestamp + "\n" +
            "UID:" + UUID.randomUUID() + "\n" +
            "CREATED:" + timestamp + "\n" + 
            "DESCRIPTION:" + Main.URL + "/match/" + matchData.id() + "\n" + 
            "LAST-MODIFIED:" + timestamp + "\n" + 
            "SEQUENCE:0\n" + 
            "STATUS:CONFIRMED\n" + 
            "TRANSP:OPAQUE\n" + 
            "END:VEVENT\n" + 
            "END:VCALENDAR";
    }
    
    private LocalDateTime matchStart() {
        return matchData.time();
    }
    
    private LocalDateTime matchEnd() {
        return matchData.time().plusMinutes(50);
    }
    
    private String summary() {
        return matchData.championship().name() + " vs " + matchData.opponent();
    }
    
    private String location() {
        return matchData.venue().address();
    }
    
}
