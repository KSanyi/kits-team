package hu.kits.team.infrastructure.db;

import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.sql.DataSource;

import hu.kits.team.domain.ChampionshipRepository;
import hu.kits.team.domain.Match;
import hu.kits.team.domain.MatchData;
import hu.kits.team.domain.MatchRepository;
import hu.kits.team.domain.Matches;
import hu.kits.team.domain.MemberStatement;
import hu.kits.team.domain.Members;

public class MatchJdbiRepository implements MatchRepository {

    private final MatchDataTable matchDataTable;
    
    private final MatchMemberStatementTable memberStatementTable;

    public MatchJdbiRepository(DataSource dataSource, ChampionshipRepository championshipRepository, Members members) {
        matchDataTable = new MatchDataTable(dataSource, championshipRepository);
        memberStatementTable = new MatchMemberStatementTable(dataSource, members);
    }

    @Override
    public void saveStatementForMatch(MatchData matchData, MemberStatement memberStatement) {
        memberStatementTable.saveNew(new MatchMemberStatement(matchData.id, memberStatement));
    }
    
    @Override
    public void updateStatementForMatch(MatchData matchData, MemberStatement memberStatement) {
        memberStatementTable.update(new MatchMemberStatement(matchData.id, memberStatement));
    }

    @Override
    public MatchData saveMatchData(MatchData matchData) {
        return matchDataTable.saveNew(matchData);
    }

    @Override
    public void deleteMatchData(long id) {
        matchDataTable.delete(id);
        memberStatementTable.deleteForMatch(id);
    }

    @Override
    public void updateMatchData(MatchData matchData) {
        matchDataTable.update(matchData);
    }

    @Override
    public Matches loadAllMatches() {
        List<MatchData> matchDatas = matchDataTable.loadAll();
        List<MatchMemberStatement> memberStatements = memberStatementTable.loadAll();
        
        return new Matches(matchDatas.stream().map(matchData -> new Match(matchData, findStatementsForMatch(matchData.id, memberStatements))).collect(toList()));
    }

    private static List<MemberStatement> findStatementsForMatch(long matchId, List<MatchMemberStatement> memberStatements) {
        return memberStatements.stream()
                .filter(m -> m.matchId == matchId)
                .map(m -> m.memberStatement)
                .collect(toList());
    }
    
}
