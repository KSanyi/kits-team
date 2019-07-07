package hu.kits.team.infrastructure.web.ui.view.match;

import java.util.Optional;
import java.util.function.Predicate;

import hu.kits.team.domain.Mark;

public enum StatementFilter {

    ALL("Össz", m -> true),
    COMING(Mark.COMING.label, m -> m.map(Mark.COMING::equals).orElse(false)),
    NOT_COMING(Mark.NOT_COMING.label, m -> m.map(Mark.NOT_COMING::equals).orElse(false)),
    NA("Nem jelzett", Optional::isEmpty);
    
    public final String label;
    
    public final Predicate<Optional<Mark>> filter;

    private StatementFilter(String label, Predicate<Optional<Mark>> filter) {
        this.label = label;
        this.filter = filter;
    }
    
}
