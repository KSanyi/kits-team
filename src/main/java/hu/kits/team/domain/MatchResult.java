package hu.kits.team.domain;

public record MatchResult(int goalsScored, int goalsConceded) {

    public String format() {
        return goalsScored + ":" + goalsConceded;
    }
    
}
