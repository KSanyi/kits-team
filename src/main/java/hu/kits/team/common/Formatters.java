package hu.kits.team.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Formatters {

    public static final Locale HU_LOCALE = new Locale("HU");
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    private static final DateTimeFormatter DATE_FORMAT_WITHOUT_YEAR = DateTimeFormatter.ofPattern("MMMM d.");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy. MMMM dd. HH:mm");
    private static final DateTimeFormatter DATE_TIME_FORMAT_WITHOUT_YEAR = DateTimeFormatter.ofPattern("MMMM d. HH:mm");
    private static final DateTimeFormatter DATE_TIME_FORMAT_WITH_WEEKDAY = DateTimeFormatter.ofPattern("EEEE HH:mm", HU_LOCALE);
    private static final DateTimeFormatter DATE_WEEKDAY = DateTimeFormatter.ofPattern("EEEE", HU_LOCALE);
    
    public static String formatDate(LocalDate date) {
        return (Clock.today().getYear() == date.getYear() ? DATE_FORMAT_WITHOUT_YEAR : DATE_FORMAT).format(date);
    }
    
    public static String formatDateTime(LocalDateTime time) {
        return (Clock.today().getYear() == time.getYear() ? DATE_TIME_FORMAT_WITHOUT_YEAR : DATE_TIME_FORMAT).format(time);
    }
    
    public static String formatTimeWithWeekday(LocalDateTime time) {
        return DATE_TIME_FORMAT_WITH_WEEKDAY.format(time);
    }
    
    public static String formatWeekday(LocalDateTime time) {
        return DATE_WEEKDAY.format(time);
    }

}
