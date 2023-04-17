package hu.kits.team.domain;

public record MatchResult(int goalsScored, int goalsConceded) {

    public String format() {
        return goalsScored + " : " + goalsConceded;
    }

    public boolean isWin() {
        return goalsScored > goalsConceded;
    }

    public boolean isLoss() {
        return goalsScored < goalsConceded;
    }
    
}
