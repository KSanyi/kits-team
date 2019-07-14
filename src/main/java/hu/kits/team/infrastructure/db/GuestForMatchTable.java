package hu.kits.team.infrastructure.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.jdbi.v3.core.Jdbi;

import hu.kits.team.domain.Guest;

class GuestForMatchTable {

    private static final String TABLE_GUEST_FOR_MATCH = "GUEST_FOR_MATCH";
    private static final String COLUMN_MATCH_ID = "MATCH_ID";
    private static final String COLUMN_GUEST_NAME = "GUEST_NAME";
    
    private final Jdbi jdbi;
    
    GuestForMatchTable(DataSource dataSource) {
        jdbi = Jdbi.create(dataSource);
    }

    List<GuestForMatch> loadAll() {
        String sql = String.format("SELECT * FROM %s", TABLE_GUEST_FOR_MATCH);
        
        return jdbi.withHandle(handle -> 
            handle.createQuery(sql).map((rs, ctx) -> mapToGuestForMatch(rs)).list());
    }
    
    private static GuestForMatch mapToGuestForMatch(ResultSet rs) throws SQLException {
        
        return new GuestForMatch(
                rs.getLong(COLUMN_MATCH_ID),
                new Guest(rs.getString(COLUMN_GUEST_NAME)));
    }

    void delete(long matchId, String guestName) {
        jdbi.withHandle(handle -> handle.execute(String.format("DELETE FROM %s WHERE %s = ? AND %s = ?", TABLE_GUEST_FOR_MATCH, COLUMN_MATCH_ID, COLUMN_GUEST_NAME),
                matchId, guestName));
    }
    
    void deleteForMatch(long matchId) {
        jdbi.withHandle(handle -> handle.execute(String.format("DELETE FROM %s WHERE %s = ?", TABLE_GUEST_FOR_MATCH, COLUMN_MATCH_ID),
                matchId));
    }

    void saveNew(GuestForMatch guestForMatch) {
        Map<String, Object> values = createValuesMap(guestForMatch);
        
        jdbi.withHandle(handle -> JdbiUtil.createInsert(handle, TABLE_GUEST_FOR_MATCH, values).execute());
    }
    
    private static Map<String, Object> createValuesMap(GuestForMatch guestForMatch) {
        Map<String, Object> valuesMap = new HashMap<>();
        valuesMap.put(COLUMN_MATCH_ID, guestForMatch.matchId);
        valuesMap.put(COLUMN_GUEST_NAME, guestForMatch.guest.name);
        
        return valuesMap;
    }

}
