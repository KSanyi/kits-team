package hu.kits.team.infrastructure.db;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.jdbi.v3.core.Jdbi;

import hu.kits.team.domain.Members;
import hu.kits.team.domain.Player;

class GoalsTable {

    private static final String TABLE_GOALS = "GOALS";
    private static final String COLUMN_MATCH_ID = "MATCH_ID";
    private static final String COLUMN_MEMBER_ID = "MEMBER_ID";
    private static final String COLUMN_SCORED = "SCORED";
    
    private final Jdbi jdbi;
    
    private final Members members;
    
    GoalsTable(DataSource dataSource, Members members) {
        jdbi = Jdbi.create(dataSource);
        this.members = members;
    }

    Map<Long, Map<Player, Integer>> loadAll() {
        String sql = String.format("SELECT * FROM %s", TABLE_GOALS);
        
        List<MatchGoal> matchGoals = jdbi.withHandle(handle -> 
            handle.createQuery(sql).map((rs, ctx) -> mapToMatchGoal(rs)).list());
        
        return matchGoals.stream().collect(groupingBy(MatchGoal::matchId, 
                toMap(MatchGoal::player, MatchGoal::scored)));
    }
    
    private MatchGoal mapToMatchGoal(ResultSet rs) throws SQLException {
        
        return new MatchGoal(
                rs.getLong(COLUMN_MATCH_ID),
                members.forId(rs.getString(COLUMN_MEMBER_ID)),
                rs.getInt(COLUMN_SCORED));
    }
    
    private record MatchGoal(long matchId, Player player, int scored) {}


    private void delete(long matchId, String memberId) {
        jdbi.withHandle(handle -> handle.execute(String.format("DELETE FROM %s WHERE %s = ? AND %s = ?", TABLE_GOALS, COLUMN_MATCH_ID, COLUMN_MEMBER_ID),
                matchId, memberId));
    }
    
    void deleteForMatch(long matchId) {
        jdbi.withHandle(handle -> handle.execute(String.format("DELETE FROM %s WHERE %s = ?", TABLE_GOALS, COLUMN_MATCH_ID),
                matchId));
    }

    void saveNew(MatchMemberStatement matchMemberStatement) {
        Map<String, Object> values = createValuesMap(matchMemberStatement);
        
        jdbi.withHandle(handle -> JdbiUtil.createInsert(handle, TABLE_GOALS, values).execute());
    }
    
    private static Map<String, Object> createValuesMap(MatchMemberStatement matchMemberStatement) {
        Map<String, Object> valuesMap = new HashMap<>();
        valuesMap.put(COLUMN_MATCH_ID, matchMemberStatement.matchId);
        valuesMap.put(COLUMN_MEMBER_ID, matchMemberStatement.memberStatement.member().id);
        valuesMap.put(COLUMN_SCORED, matchMemberStatement.memberStatement.time());
        
        return valuesMap;
    }

    void update(MatchMemberStatement matchMemberStatement) {
        delete(matchMemberStatement.matchId, matchMemberStatement.memberStatement.member().id);
        saveNew(matchMemberStatement);
    }
    
}
