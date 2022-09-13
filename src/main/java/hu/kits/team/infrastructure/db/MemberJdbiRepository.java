package hu.kits.team.infrastructure.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.jdbi.v3.core.Jdbi;

import hu.kits.team.domain.Member;
import hu.kits.team.domain.Member.Role;
import hu.kits.team.domain.MemberRepository;
import hu.kits.team.domain.Members;

public class MemberJdbiRepository implements MemberRepository {

    private static final String TABLE_MEMBER = "MEMBER";
    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_NAME = "NAME";
    private static final String COLUMN_EMAIL = "EMAIL";
    private static final String COLUMN_PASSWORD_HASH = "PASSWORD_HASH";
    private static final String COLUMN_ROLE = "ROLE";
    
    private final Jdbi jdbi;
    
    public MemberJdbiRepository(DataSource dataSource) {
        jdbi = Jdbi.create(dataSource);
    }

    public Members loadAll() {
        String sql = String.format("SELECT * FROM %s", TABLE_MEMBER);
        
        return new Members(jdbi.withHandle(handle -> 
            handle.createQuery(sql).map((rs, ctx) -> mapToMember(rs)).list()));
    }
    
    private static Member mapToMember(ResultSet rs) throws SQLException {
        return new Member(
                rs.getString(COLUMN_ID),
                rs.getString(COLUMN_NAME),
                rs.getString(COLUMN_EMAIL),
                rs.getString(COLUMN_PASSWORD_HASH),
                Role.valueOf(rs.getString(COLUMN_ROLE)));
    }
    
}
