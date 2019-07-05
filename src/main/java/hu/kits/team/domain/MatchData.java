package hu.kits.team.domain;

import java.time.LocalDateTime;

public class MatchData {

    public final long id;
    
    public final Championship championship;
    
    public final LocalDateTime time;
    
    public final String venue;
    
    public final String opponent;

    public MatchData(long id, Championship championship, LocalDateTime time, String venue, String opponent) {
        this.id = id;
        this.championship = championship;
        this.time = time;
        this.venue = venue;
        this.opponent = opponent;
    }

    public MatchData withId(long id) {
        return new MatchData(id, championship, time, venue, opponent);
    }
    
}
