package hu.kits.team.domain;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import hu.kits.team.common.Clock;
import hu.kits.team.common.DateInterval;
import hu.kits.team.domain.TopPlayers.PlayerGames;

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

    public TopPlayers topPlayersOfChampionship(Championship championship) {
        
        Stream<Match> matchStream = entries.stream()
                .filter(match -> match.matchData().championship().equals(championship));
        
        return createTopPlayers(matchStream);
    }

    public TopPlayers topPlayersInYear(int year) {
        
        LocalDate start = LocalDate.of(year, 1, 1).minusDays(1);
        LocalDate end = LocalDate.of(year+1, 1, 1);
        Stream<Match> matchStream = entries.stream()
                .filter(m -> m.matchData().time().toLocalDate().isAfter(start) && m.matchData().time().toLocalDate().isBefore(end));
            
        return createTopPlayers(matchStream);
    }
    
    private static TopPlayers createTopPlayers(Stream<Match> matchStream) {
        Map<Player, Long> playerMatchCount =  matchStream
            .filter(match -> match.matchData().time().isBefore(Clock.now()))
            .flatMap(match -> match.coming().stream())
            .collect(groupingBy(Function.identity(), counting()));
        
        List<PlayerGames> playerGamesList = playerMatchCount.entrySet().stream()
            .map(e -> new PlayerGames(e.getKey(), e.getValue().intValue()))
            .sorted(comparing(PlayerGames::games).reversed())
            .collect(toList());
        
        return new TopPlayers(playerGamesList);
    }

    public TopPlayers topPlayers() {
        return createTopPlayers(entries.stream());
    }
}
