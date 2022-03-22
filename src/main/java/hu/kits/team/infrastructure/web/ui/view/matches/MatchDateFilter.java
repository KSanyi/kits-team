package hu.kits.team.infrastructure.web.ui.view.matches;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.function.Predicate;

import hu.kits.team.common.Clock;
import hu.kits.team.domain.Match;

public enum MatchDateFilter {

    UPCOMING("Köv", d -> d.isAfter(Clock.now()), Comparator.comparing(m -> m.matchData().time())),
    PAST("Elmúlt", d -> d.isBefore(Clock.now()), Comparator.comparing((Match m) -> m.matchData().time()).reversed()),
    ALL("Össz", m -> true, Comparator.comparing(m -> m.matchData().time()));
    
    public final String label;
    
    public final Predicate<LocalDateTime> filter;
    public final Comparator<Match> comparator;

    private MatchDateFilter(String label, Predicate<LocalDateTime> filter, Comparator<Match> comparator) {
        this.label = label;
        this.filter = filter;
        this.comparator = comparator;
    }
    
}
