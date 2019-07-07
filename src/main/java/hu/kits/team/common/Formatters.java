package hu.kits.team.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Formatters {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    private static final DateTimeFormatter DATE_FORMAT_WITHOUT_YEAR = DateTimeFormatter.ofPattern("MMMM d.");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy. MMMM dd. HH:mm");
    private static final DateTimeFormatter DATE_TIME_FORMAT_WITHOUT_YEAR = DateTimeFormatter.ofPattern("MMMM d. HH:mm");
    
    public static final Locale HU_LOCALE = new Locale("HU");
    
    public static String formatDate(LocalDate date) {
        return (Clock.today().getYear() == date.getYear() ? DATE_FORMAT_WITHOUT_YEAR : DATE_FORMAT).format(date);
    }
    
    public static String formatDateTime(LocalDateTime date) {
        return (Clock.today().getYear() == date.getYear() ? DATE_TIME_FORMAT_WITHOUT_YEAR : DATE_TIME_FORMAT).format(date);
    }
    
}
