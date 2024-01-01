package hu.kits.team.infrastructure.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.jdbi.v3.core.Jdbi;

import hu.kits.team.domain.Venue;
import hu.kits.team.domain.VenueRepository;

public class VenueJdbiRepository implements VenueRepository {

    private static final String TABLE_VENUE = "TEAM_VENUE";
    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_NAME = "NAME";
    private static final String COLUMN_ADDRESS = "ADDRESS";
    private static final String COLUMN_GPS = "GPS";
    
    private final Jdbi jdbi;

    public VenueJdbiRepository(DataSource dataSource) {
        jdbi = Jdbi.create(dataSource);
    }

    public List<Venue> loadAll() {
        String sql = String.format("SELECT * FROM %s", TABLE_VENUE);
        
        return jdbi.withHandle(handle -> 
            handle.createQuery(sql).map((rs, ctx) -> mapToVenue(rs)).list());
    }
    
    
    private static Venue mapToVenue(ResultSet rs) throws SQLException {
        
        return new Venue(
                rs.getString(COLUMN_ID),
                rs.getString(COLUMN_NAME),
                rs.getString(COLUMN_ADDRESS),
                rs.getString(COLUMN_GPS));
    }
    
}
