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

import hu.kits.team.domain.Championship;
import hu.kits.team.domain.ChampionshipRepository;
import hu.kits.team.domain.MatchData;

class MatchDataTable {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private static final String TABLE_MATCH_DATA = "MATCH_DATA";
    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_CHAMP_ID = "CHAMP_ID";
    private static final String COLUMN_TIME = "TIME";
    private static final String COLUMN_VENUE = "VENUE";
    private static final String COLUMN_OPPONENT = "OPPONENT";
    
    private final Jdbi jdbi;
    
    private final ChampionshipRepository championshipRepository;

    MatchDataTable(DataSource dataSource, ChampionshipRepository championshipRepository) {
        jdbi = Jdbi.create(dataSource);
        this.championshipRepository = championshipRepository;
    }

    List<MatchData> loadAll() {
        String sql = String.format("SELECT * FROM %s", TABLE_MATCH_DATA);
        
        List<Championship> championships = championshipRepository.loadAll();
        
        return jdbi.withHandle(handle -> 
            handle.createQuery(sql).map((rs, ctx) -> mapToMatchData(rs, championships)).list());
    }
    
    MatchData find(long id) {
        String sql = String.format("SELECT * FROM %s WHERE %s = :id", TABLE_MATCH_DATA, COLUMN_ID);
        
        List<Championship> championships = championshipRepository.loadAll();
        
        return jdbi.withHandle(handle -> 
            handle.createQuery(sql).bind("id", id).map((rs, ctx) -> mapToMatchData(rs, championships)).findOnly());
    }
    
    private static MatchData mapToMatchData(ResultSet rs, List<Championship> championships) throws SQLException {
        
        long champId = rs.getLong(COLUMN_CHAMP_ID);
        
        Championship championship = championships.stream().filter(c -> c.id == champId).findAny().get();
        
        return new MatchData(
                rs.getLong(COLUMN_ID),
                championship,
                rs.getTimestamp(COLUMN_TIME).toLocalDateTime(),
                rs.getString(COLUMN_VENUE),
                rs.getString(COLUMN_OPPONENT));
    }

    void delete(long id) {
        jdbi.withHandle(handle -> handle.execute(String.format("DELETE FROM %s WHERE %s = ?", TABLE_MATCH_DATA, COLUMN_ID), id));
        log.info("Purchase with id {} deleted", id);
    }

    MatchData saveNew(MatchData matchData) {
        Map<String, Object> values = createValuesMap(matchData);
        
        long id = jdbi.withHandle(handle -> JdbiUtil.createInsert(handle, TABLE_MATCH_DATA, values).executeAndReturnGeneratedKeys(COLUMN_ID).mapTo(Long.class).findOnly());
        
        log.info("MatchData saved: {}", matchData);
        
        return matchData.withId(id);
    }
    
    private static Map<String, Object> createValuesMap(MatchData matchData) {
        Map<String, Object> valuesMap = new HashMap<>();
        valuesMap.put(COLUMN_CHAMP_ID, matchData.championship.id);
        valuesMap.put(COLUMN_TIME, matchData.time);
        valuesMap.put(COLUMN_VENUE, matchData.venue);
        valuesMap.put(COLUMN_OPPONENT, matchData.opponent);
        
        return valuesMap;
    }

    void update(MatchData matchData) {
        Map<String, Object> values = createValuesMap(matchData);
        Map<String, Object> originalValues = createValuesMap(find(matchData.id));
        jdbi.withHandle(handle -> JdbiUtil.createUpdate(handle, TABLE_MATCH_DATA, originalValues, values, COLUMN_ID, String.valueOf(matchData.id)).execute());
    }
    
}
