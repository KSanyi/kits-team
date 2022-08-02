package hu.kits.team.infrastructure.db;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import hu.kits.team.domain.ChampionshipRepository;
import hu.kits.team.domain.Guest;
import hu.kits.team.domain.Match;
import hu.kits.team.domain.MatchData;
import hu.kits.team.domain.MatchRepository;
import hu.kits.team.domain.Matches;
import hu.kits.team.domain.Member;
import hu.kits.team.domain.MemberStatement;
import hu.kits.team.domain.Members;
import hu.kits.team.domain.Player;
import hu.kits.team.domain.VenueRepository;

public class MatchJdbiRepository implements MatchRepository {

    private final MatchDataTable matchDataTable;
    
    private final MatchMemberStatementTable memberStatementTable;
    
    private final GuestForMatchTable guestForMatchTable;
    
    private final GoalsTable goalsTable;

    public MatchJdbiRepository(DataSource dataSource, ChampionshipRepository championshipRepository, VenueRepository venueRepository, Members members) {
        matchDataTable = new MatchDataTable(dataSource, championshipRepository, venueRepository);
        memberStatementTable = new MatchMemberStatementTable(dataSource, members);
        guestForMatchTable = new GuestForMatchTable(dataSource);
        goalsTable = new GoalsTable(dataSource, members);
    }

    @Override
    public void saveStatementForMatch(MatchData matchData, MemberStatement memberStatement) {
        memberStatementTable.saveNew(new MatchMemberStatement(matchData.id(), memberStatement));
    }
    
    @Override
    public void updateStatementForMatch(MatchData matchData, MemberStatement memberStatement) {
        memberStatementTable.update(new MatchMemberStatement(matchData.id(), memberStatement));
    }

    @Override
    public MatchData saveMatchData(MatchData matchData) {
        return matchDataTable.saveNew(matchData);
    }

    @Override
    public void deleteMatchData(long id) {
        matchDataTable.delete(id);
        memberStatementTable.deleteForMatch(id);
        goalsTable.deleteForMatch(id);
    }

    @Override
    public void updateMatchData(MatchData matchData) {
        matchDataTable.update(matchData);
    }

    @Override
    public Matches loadAllMatches() {
        List<MatchData> matchDatas = matchDataTable.loadAll();
        List<MatchMemberStatement> memberStatements = memberStatementTable.loadAll();
        List<GuestForMatch> guestForMatchEntries = guestForMatchTable.loadAll();
        Map<Long, Map<Player, Integer>> goalsPerMatchPerPlayer = goalsTable.loadAll();
        
        return new Matches(matchDatas.stream().map(matchData -> new Match(
                    matchData, 
                    findStatementsForMatch(matchData.id(), memberStatements),
                    findGuestsForMatch(matchData.id(), guestForMatchEntries),
                    goalsPerMatchPerPlayer.getOrDefault(matchData.id(), Map.of())))
                .collect(toList()));
    }

    private static List<MemberStatement> findStatementsForMatch(long matchId, List<MatchMemberStatement> memberStatements) {
        return memberStatements.stream()
                .filter(m -> m.matchId == matchId)
                .map(m -> m.memberStatement)
                .collect(toList());
    }
    
    private static List<Guest> findGuestsForMatch(long matchId, List<GuestForMatch> guestForMatchEntries) {
        return guestForMatchEntries.stream()
                .filter(m -> m.matchId == matchId)
                .map(m -> m.guest)
                .collect(toList());
    }
    
    public void addGuestForMatch(long matchId, Guest guest) {
        guestForMatchTable.saveNew(new GuestForMatch(matchId, guest));
    }

    @Override
    public void removeGuestForMatch(long id, Guest guest) {
        guestForMatchTable.delete(id, guest.name);
    }

    @Override
    public void updateGoalsForMatch(MatchData matchData, Member member, int goals) {
        goalsTable.update(matchData, member, goals);
    }
    
}
