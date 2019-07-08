package hu.kits.team.infrastructure.web.ui.view.matches;

import java.time.LocalDateTime;
import java.util.function.Predicate;

import hu.kits.team.common.Clock;

public enum MatchDateFiler {

    ALL("Össz", m -> true),
    UPCOMING("Köv", d -> d.isAfter(Clock.now())),
    PAST("Elmúlt", d -> d.isBefore(Clock.now()));
    
    public final String label;
    
    public final Predicate<LocalDateTime> filter;

    private MatchDateFiler(String label, Predicate<LocalDateTime> filter) {
        this.label = label;
        this.filter = filter;
    }
    
}
