package hu.kits.team;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.kits.team.domain.Championship;
import hu.kits.team.domain.Mark;
import hu.kits.team.domain.Match;
import hu.kits.team.domain.MatchData;
import hu.kits.team.domain.Matches;
import hu.kits.team.domain.Member;
import hu.kits.team.domain.MemberStatement;
import hu.kits.team.domain.Members;
import hu.kits.team.domain.TeamService;

public class TeamServiceTest {

    private TeamService teamService;
    
    @BeforeEach
    private void init() throws Exception {
        DataSource dataSource = InMemoryDataSourceFactory.createDataSource(
                "INSERT INTO MEMBER VALUES('sanyi', 'Kócsó Sándor', 'kocso.sandor.gabor@gmail.com')",
                "INSERT INTO MEMBER VALUES('zolika', 'Wéber Zoltán', 'weberzoli@gmail.com')",
                "INSERT INTO CHAMPIONSHIP VALUES (null, 'Üzleti Liga ÁBL 2019')");
        
        teamService = TeamServiceFactory.create(dataSource);
    }
    
    @Test
    public void members() {
        Members members = teamService.members();
        
        Member sanyi = members.entries().get(0);
        
        assertEquals("sanyi", sanyi.id);
        assertEquals("Kócsó Sándor", sanyi.name);
        assertEquals("kocso.sandor.gabor@gmail.com", sanyi.email);
        
        assertEquals(2, members.entries().size());
    }
    
    @Test
    public void championships() {
        teamService.createChampionship("BEAC nyár");
        
        List<Championship> championships = teamService.loadChampionships();
        
        assertEquals(2, championships.size());
        assertEquals("BEAC nyár", championships.get(1).name);
        
        teamService.updateChampionship(new Championship(championships.get(1).id, "BEAC nyár 2019"));
        
        championships = teamService.loadChampionships();
        
        assertEquals(2, championships.size());
        assertEquals("BEAC nyár 2019", championships.get(1).name);
    }
    
    @Test
    public void matchData() {
        
        Championship championship = teamService.loadChampionships().get(0);
        
        MatchData matchData = new MatchData(0, championship, LocalDateTime.of(2019,8,8, 20,0), "BEAC", "Jubi Titáns");
        
        teamService.saveNewMatchData(matchData);
        
        matchData = teamService.loadAllMatches().entries().get(0).matchData;
        
        assertEquals("Üzleti Liga ÁBL 2019", matchData.championship.name);
        assertEquals("Jubi Titáns", matchData.opponent);
        assertEquals("BEAC", matchData.venue);
        assertEquals(LocalDateTime.of(2019,8,8, 20,0), matchData.time);
        
        // UPDATE
        
        MatchData updatedMatchData = new MatchData(matchData.id, championship, LocalDateTime.of(2019,8,8, 21,0), "Sportmax2", "LogMeIn");
        
        teamService.updateMatchData(updatedMatchData);
        
        Matches matches = teamService.loadAllMatches();
        
        assertEquals(1, matches.entries().size());
        
        matchData = matches.entries().get(0).matchData;
        
        assertEquals("Üzleti Liga ÁBL 2019", matchData.championship.name);
        assertEquals("LogMeIn", matchData.opponent);
        assertEquals("Sportmax2", matchData.venue);
        assertEquals(LocalDateTime.of(2019,8,8, 21,0), matchData.time);
    }
    
    @Test
    public void statements() {
        
        Member sanyi = teamService.members().entries().get(0);
        Member zoli = teamService.members().entries().get(1);
        
        Championship championship = teamService.loadChampionships().get(0);
        
        MatchData matchData = teamService.saveNewMatchData(new MatchData(0, championship, LocalDateTime.of(2019,8,8, 20,0), "BEAC", "Jubi Titáns"));
        
        teamService.saveStatementForMatch(matchData, new MemberStatement(sanyi, Mark.COMING, LocalDateTime.of(2019,8,5, 8,0), ""));
        teamService.saveStatementForMatch(matchData, new MemberStatement(zoli, Mark.NOT_COMING, LocalDateTime.of(2019,8,5, 9,0), ""));
        
        Match match = teamService.loadAllMatches().entries().get(0);
        
        List<MemberStatement> statements = match.memberStatements();
        
        assertEquals("sanyi", statements.get(0).member.id);
        assertEquals(Mark.COMING, statements.get(0).mark);
        assertEquals(LocalDateTime.of(2019,8,5, 8,0), statements.get(0).time);
        
        assertEquals("zolika", statements.get(1).member.id);
        assertEquals(Mark.NOT_COMING, statements.get(1).mark);
        assertEquals(LocalDateTime.of(2019,8,5, 9,0), statements.get(1).time);
    }
    
    @Test
    public void updateStatement() {
        
        Member sanyi = teamService.members().entries().get(0);
        
        Championship championship = teamService.loadChampionships().get(0);
        
        MatchData matchData = teamService.saveNewMatchData(new MatchData(0, championship, LocalDateTime.of(2019,8,8, 20,0), "BEAC", "Jubi Titáns"));
        
        teamService.saveStatementForMatch(matchData, new MemberStatement(sanyi, Mark.COMING, LocalDateTime.of(2019,8,5, 8,0), ""));
        
        Match match = teamService.loadAllMatches().entries().get(0);
        
        assertEquals(Mark.COMING, match.statementFor(sanyi).get().mark);
        
        teamService.updateStatementForMatch(matchData, new MemberStatement(sanyi, Mark.DONT_KNOW_YET, LocalDateTime.of(2019,8,5, 9,0), ""));
        
        match = teamService.loadAllMatches().entries().get(0);
        
        assertEquals(Mark.DONT_KNOW_YET, match.statementFor(sanyi).get().mark);
    }
    
}
