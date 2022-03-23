package hu.kits.team.domain;

import java.time.LocalDateTime;
import java.util.Optional;

import hu.kits.team.common.Formatters;

public record MatchData(long id, Championship championship, LocalDateTime time, Venue venue, String opponent, Optional<MatchResult> matchResult) {

    public MatchData withId(long id) {
        return new MatchData(id, championship, time, venue, opponent, Optional.empty());
    }
    
    public MatchData withResult(Optional<MatchResult> matchResult) {
        return new MatchData(id, championship, time, venue, opponent, matchResult);
    }
    
    public String toString() {
        return Formatters.formatDateTime(time) + " (" + opponent + ")";
    }
    
    public String formatResult() {
        return matchResult.map(MatchResult::format).orElse("? : ?");
    }
    
}
