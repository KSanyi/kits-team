package hu.kits.team.domain;

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
}
