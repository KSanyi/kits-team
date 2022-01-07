package hu.kits.team.domain;

import java.time.LocalDateTime;

import hu.kits.team.common.Formatters;

public record MatchData(long id, Championship championship, LocalDateTime time, Venue venue, String opponent) {

    public MatchData withId(long id) {
        return new MatchData(id, championship, time, venue, opponent);
    }
    public String toString() {
        return Formatters.formatDateTime(time) + " (" + opponent + ")";
    }
    
}
