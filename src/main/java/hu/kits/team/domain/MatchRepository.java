package hu.kits.team.domain;

public interface MatchRepository {

    void saveStatementForMatch(MatchData matchData, MemberStatement memberStatement);
    
    void updateStatementForMatch(MatchData matchData, MemberStatement updatedMemberStatement);
    
    void addGuestForMatch(long matchId, Guest guest);
    
    void removeGuestForMatch(long id, Guest guest);
    
    MatchData saveMatchData(MatchData matchData);
    
    void deleteMatchData(long id);
    
    void updateMatchData(MatchData matchData);
    
    Matches loadAllMatches();

    void updateGoalsForMatch(MatchData matchData, Member member, int goals);

}
