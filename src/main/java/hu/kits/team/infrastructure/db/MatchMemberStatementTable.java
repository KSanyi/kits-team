package hu.kits.team.infrastructure.db;

import java.lang.invoke.MethodHandles;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.kits.team.domain.Mark;
import hu.kits.team.domain.MemberStatement;
import hu.kits.team.domain.Members;

class MatchMemberStatementTable {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private static final String TABLE_MEMBER_STATEMENT = "MEMBER_STATEMENT";
    private static final String COLUMN_MATCH_ID = "MATCH_ID";
    private static final String COLUMN_MEMBER_ID = "MEMBER_ID";
    private static final String COLUMN_TIME = "TIME";
    private static final String COLUMN_MARK = "MARK";
    private static final String COLUMN_COMMENT = "COMMENT";
    
    private final Jdbi jdbi;
    
    private final Members members;
    
    MatchMemberStatementTable(DataSource dataSource, Members members) {
        jdbi = Jdbi.create(dataSource);
        this.members = members;
    }

    List<MatchMemberStatement> loadAll() {
        String sql = String.format("SELECT * FROM %s", TABLE_MEMBER_STATEMENT);
        
        return jdbi.withHandle(handle -> 
            handle.createQuery(sql).map((rs, ctx) -> mapToMatchMemberStatement(rs)).list());
    }
    
    private MatchMemberStatement mapToMatchMemberStatement(ResultSet rs) throws SQLException {
        
        return new MatchMemberStatement(
                rs.getLong(COLUMN_MATCH_ID),
                new MemberStatement(members.forId(rs.getString(COLUMN_MEMBER_ID)),
                        Mark.valueOf(rs.getString(COLUMN_MARK)), 
                        rs.getTimestamp(COLUMN_TIME).toLocalDateTime(),
                        rs.getString(COLUMN_COMMENT)));
    }

    private void delete(long matchId, String memberId) {
        jdbi.withHandle(handle -> handle.execute(String.format("DELETE FROM %s WHERE %s = ? AND %s = ?", TABLE_MEMBER_STATEMENT, COLUMN_MATCH_ID, COLUMN_MEMBER_ID),
                matchId, memberId));
        log.info("MemberStatement deleted for {} for match {}", memberId, matchId);
    }
    
    void deleteForMatch(long matchId) {
        jdbi.withHandle(handle -> handle.execute(String.format("DELETE FROM %s WHERE %s = ?", TABLE_MEMBER_STATEMENT, COLUMN_MATCH_ID),
                matchId));
        log.info("MemberStatements deletedfor match {}", matchId);
    }

    void saveNew(MatchMemberStatement matchMemberStatement) {
        Map<String, Object> values = createValuesMap(matchMemberStatement);
        
        jdbi.withHandle(handle -> JdbiUtil.createInsert(handle, TABLE_MEMBER_STATEMENT, values).execute());
        
        log.info("MatchMemberStatement saved: {}", matchMemberStatement);
    }
    
    private static Map<String, Object> createValuesMap(MatchMemberStatement matchMemberStatement) {
        Map<String, Object> valuesMap = new HashMap<>();
        valuesMap.put(COLUMN_MATCH_ID, matchMemberStatement.matchId);
        valuesMap.put(COLUMN_MEMBER_ID, matchMemberStatement.memberStatement.member.id);
        valuesMap.put(COLUMN_TIME, matchMemberStatement.memberStatement.time);
        valuesMap.put(COLUMN_COMMENT, matchMemberStatement.memberStatement.comment);
        valuesMap.put(COLUMN_MARK, matchMemberStatement.memberStatement.mark);
        
        return valuesMap;
    }

    void update(MatchMemberStatement matchMemberStatement) {
        delete(matchMemberStatement.matchId, matchMemberStatement.memberStatement.member.id);
        saveNew(matchMemberStatement);
    }
    
}
