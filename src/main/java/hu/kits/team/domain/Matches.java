package hu.kits.team.domain;

import static java.util.Comparator.comparing;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class Matches {

    private final List<Match> entries;

    public Matches(List<Match> entries) {
        this.entries = entries;
    }
    
    public Optional<Match> find(long matchId) {
        return entries.stream().filter(e -> e.matchData.id == matchId).findFirst();
    }
    
    public List<Match> entries() {
        return List.copyOf(entries);
    }

    public Match findNext(LocalDateTime time) {
        return entries.stream().filter(e -> e.matchData.time.isAfter(time))
                .sorted(comparing(e -> e.matchData.time))
                .findFirst().orElseGet(() -> findLast(time));
    }
    
    private Match findLast(LocalDateTime time) {
        return entries.stream()
            .filter(e -> e.matchData.time.isBefore(time))
            .sorted(comparing((Match e) -> e.matchData.time).reversed()).findFirst().orElseThrow();
    }
}
