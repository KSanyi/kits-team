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

public class ChampionshipJdbiRepository implements ChampionshipRepository {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private static final String TABLE_CHAMPIONSHIP = "CHAMPIONSHIP";
    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_NAME = "NAME";
    
    private final Jdbi jdbi;

    public ChampionshipJdbiRepository(DataSource dataSource) {
        jdbi = Jdbi.create(dataSource);
    }

    public List<Championship> loadAll() {
        String sql = String.format("SELECT * FROM %s", TABLE_CHAMPIONSHIP);
        
        return jdbi.withHandle(handle -> 
            handle.createQuery(sql).map((rs, ctx) -> mapToChampionship(rs)).list());
    }
    
    Championship find(long id) {
        String sql = String.format("SELECT * FROM %s WHERE %s = :id", TABLE_CHAMPIONSHIP, COLUMN_ID);
        
        return jdbi.withHandle(handle -> 
            handle.createQuery(sql).bind("id", id).map((rs, ctx) -> mapToChampionship(rs)).findOnly());
    }
    
    private static Championship mapToChampionship(ResultSet rs) throws SQLException {
        
        return new Championship(rs.getLong(COLUMN_ID), rs.getString(COLUMN_NAME));
    }

    public void delete(long id) {
        jdbi.withHandle(handle -> handle.execute(String.format("DELETE FROM %s WHERE %s = ?", TABLE_CHAMPIONSHIP, COLUMN_ID), id));
        log.info("Championship with id {} deleted", id);
    }

    public Championship save(String name) {
        Map<String, Object> values = Map.of(COLUMN_NAME, name);
        
        long id = jdbi.withHandle(handle -> JdbiUtil.createInsert(handle, TABLE_CHAMPIONSHIP, values)
                .executeAndReturnGeneratedKeys(COLUMN_ID).mapTo(Long.class).findOnly());
        
        Championship championship = new Championship(id, name);
        
        log.info("Championship saved: {}", championship);
        
        return championship;
    }
    
    private static Map<String, Object> createValuesMap(Championship championship) {
        Map<String, Object> valuesMap = new HashMap<>();
        valuesMap.put(COLUMN_NAME, championship.name);
        
        return valuesMap;
    }

    public void update(Championship championship) {
        Map<String, Object> values = createValuesMap(championship);
        Map<String, Object> originalValues = createValuesMap(find(championship.id));
        jdbi.withHandle(handle -> JdbiUtil.createUpdate(handle, TABLE_CHAMPIONSHIP, originalValues, values, COLUMN_ID, String.valueOf(championship.id)).execute());
    }
    
}
