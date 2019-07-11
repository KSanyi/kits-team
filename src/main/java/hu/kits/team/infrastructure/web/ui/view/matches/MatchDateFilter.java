package hu.kits.team.infrastructure.web.ui.view.matches;

import java.time.LocalDateTime;
import java.util.function.Predicate;

import hu.kits.team.common.Clock;

public enum MatchDateFilter {

    UPCOMING("Köv", d -> d.isAfter(Clock.now())),
    PAST("Elmúlt", d -> d.isBefore(Clock.now())),
    ALL("Össz", m -> true);
    
    public final String label;
    
    public final Predicate<LocalDateTime> filter;

    private MatchDateFilter(String label, Predicate<LocalDateTime> filter) {
        this.label = label;
        this.filter = filter;
    }
    
}
