package hu.kits.team.domain;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import hu.kits.team.common.DateInterval;

public class Matches {

    private final List<Match> entries;

    public Matches(List<Match> entries) {
        this.entries = entries;
    }
    
    public Optional<Match> find(long matchId) {
        return entries.stream()
            .filter(e -> e.matchData().id() == matchId)
            .findFirst();
    }
    
    public List<Match> entries() {
        return entries.stream()
            .sorted(comparing(m -> m.matchData().time()))
            .collect(toList());
    }

    public Match findNext(LocalDateTime time) {
        return entries.stream().filter(e -> e.matchData().time().isAfter(time))
            .sorted(comparing(e -> e.matchData().time()))
            .findFirst().orElseGet(() -> findLast(time));
    }
    
    private Match findLast(LocalDateTime time) {
        return entries.stream()
            .filter(e -> e.matchData().time().isBefore(time))
            .sorted(comparing((Match e) -> e.matchData().time()).reversed()).findFirst().orElseThrow();
    }

    public List<Match> in(DateInterval dateInterval) {
        return entries.stream()
                .filter(e -> dateInterval.contains(e.matchData().time()))
                .collect(toList());
    }

    public Optional<Match> findPrev(long matchId) {
        List<Match> entries = entries();
        for(int i=0;i<entries.size();i++) {
            if(entries.get(i).matchData().id() == matchId) {
                if(i > 0) {
                    return Optional.of(entries.get(i-1));
                }
            }
        }
        return Optional.empty();
    }
    
    public Optional<Match> findNext(long matchId) {
        List<Match> entries = entries();
        for(int i=0;i<entries.size();i++) {
            if(entries.get(i).matchData().id() == matchId) {
                if(entries.size() > i+1) {
                    return Optional.of(entries.get(i+1));
                }
            }
        }
        return Optional.empty();
    }
}
