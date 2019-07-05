package hu.kits.team.domain;

public interface MatchRepository {

    void saveStatementForMatch(MatchData matchData, MemberStatement memberStatement);
    
    void updateStatementForMatch(MatchData matchData, MemberStatement updatedMemberStatement);
    
    MatchData saveMatchData(MatchData matchData);
    
    void deleteMatchData(long id);
    
    void updateMatchData(MatchData matchData);
    
    Matches loadAllMatches();
    
}
