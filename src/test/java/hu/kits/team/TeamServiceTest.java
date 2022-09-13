package hu.kits.team;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.kits.team.common.Clock;
import hu.kits.team.domain.Championship;
import hu.kits.team.domain.Mark;
import hu.kits.team.domain.Match;
import hu.kits.team.domain.MatchData;
import hu.kits.team.domain.MatchResult;
import hu.kits.team.domain.Matches;
import hu.kits.team.domain.Member;
import hu.kits.team.domain.MemberStatement;
import hu.kits.team.domain.Members;
import hu.kits.team.domain.TeamService;
import hu.kits.team.domain.Venue;

public class TeamServiceTest {

    private final SpyEmailSender spyEmailSender = new SpyEmailSender();
    
    private TeamService teamService;
    
    @BeforeEach
    private void init() throws Exception {
        DataSource dataSource = InMemoryDataSourceFactory.createDataSource(
                "INSERT INTO MEMBER VALUES('sanyi', 'Kócsó Sándor', 'kocso.sandor.gabor@gmail.com', 'ADMIN', 'abcd')",
                "INSERT INTO MEMBER VALUES('zolika', 'Wéber Zoltán', 'weberzoli@gmail.com', 'ADMIN', 'abcd')",
                "INSERT INTO CHAMPIONSHIP VALUES (null, 'Üzleti Liga ÁBL 2019', 6)",
                "INSERT INTO VENUE VALUES ('sportmax2', 'SportMax2', 'Budapest, Mom Park 1', null)");
        
        teamService = TeamServiceFactory.create(dataSource, spyEmailSender);
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
        teamService.createChampionship("BEAC nyár", 6);
        
        List<Championship> championships = teamService.loadChampionships();
        
        assertEquals(2, championships.size());
        assertEquals("BEAC nyár", championships.get(1).name());
        assertEquals(6, championships.get(1).numberOfPlayers());
        
        teamService.updateChampionship(new Championship(championships.get(1).id(), "BEAC nyár 2019", 5));
        
        championships = teamService.loadChampionships();
        
        assertEquals(2, championships.size());
        assertEquals("BEAC nyár 2019", championships.get(1).name());
        assertEquals(5, championships.get(1).numberOfPlayers());
    }
    
    @Test
    public void matchData() {
        
        Championship championship = teamService.loadChampionships().get(0);
        Venue venue = teamService.loadVenues().get(0);
        
        MatchData matchData = new MatchData(0, championship, LocalDateTime.of(2019,8,8, 20,0), venue, "Jubi Titáns", Optional.empty());
        
        teamService.saveNewMatchData(matchData);
        
        matchData = teamService.loadAllMatches().entries().get(0).matchData();
        
        assertEquals("Üzleti Liga ÁBL 2019", matchData.championship().name());
        assertEquals("Jubi Titáns", matchData.opponent());
        assertEquals("SportMax2", matchData.venue().name());
        assertEquals(LocalDateTime.of(2019,8,8, 20,0), matchData.time());
        
        // UPDATE
        MatchData updatedMatchData = new MatchData(matchData.id(), championship, LocalDateTime.of(2019,8,8, 21,0), venue, "LogMeIn", Optional.empty());
        
        teamService.updateMatchData(updatedMatchData);
        
        Matches matches = teamService.loadAllMatches();
        
        assertEquals(1, matches.entries().size());
        
        matchData = matches.entries().get(0).matchData();
        
        assertEquals("Üzleti Liga ÁBL 2019", matchData.championship().name());
        assertEquals("LogMeIn", matchData.opponent());
        assertEquals("SportMax2", matchData.venue().name());
        assertEquals(LocalDateTime.of(2019,8,8, 21,0), matchData.time());
        
        // UPDATE result
        teamService.updateResult(matchData, Optional.of(new MatchResult(3, 2)));
        
        matches = teamService.loadAllMatches();
        
        assertEquals(1, matches.entries().size());
        
        matchData = matches.entries().get(0).matchData();
        
        assertEquals("Üzleti Liga ÁBL 2019", matchData.championship().name());
        assertEquals("LogMeIn", matchData.opponent());
        assertEquals("SportMax2", matchData.venue().name());
        assertEquals(LocalDateTime.of(2019,8,8, 21,0), matchData.time());
        assertEquals(new MatchResult(3, 2), matchData.matchResult().get());
    }
    
    @Test
    public void statements() {
        
        Member sanyi = teamService.members().entries().get(0);
        Member zoli = teamService.members().entries().get(1);
        
        Championship championship = teamService.loadChampionships().get(0);
        Venue venue = teamService.loadVenues().get(0);
        
        MatchData matchData = teamService.saveNewMatchData(new MatchData(0, championship, LocalDateTime.of(2019,8,8, 20,0), venue, "Jubi Titáns", Optional.empty()));
        
        teamService.saveStatementForMatch(matchData, new MemberStatement(sanyi, Mark.COMING, LocalDateTime.of(2019,8,5, 8,0), ""));
        teamService.saveStatementForMatch(matchData, new MemberStatement(zoli, Mark.NOT_COMING, LocalDateTime.of(2019,8,5, 9,0), ""));
        
        Match match = teamService.loadAllMatches().entries().get(0);
        
        List<MemberStatement> statements = match.memberStatements();
        
        assertEquals("sanyi", statements.get(0).member().id);
        assertEquals(Mark.COMING, statements.get(0).mark());
        assertEquals(LocalDateTime.of(2019,8,5, 8,0), statements.get(0).time());
        
        assertEquals("zolika", statements.get(1).member().id);
        assertEquals(Mark.NOT_COMING, statements.get(1).mark());
        assertEquals(LocalDateTime.of(2019,8,5, 9,0), statements.get(1).time());
    }
    
    @Test
    public void updateStatement() {
        
        Member sanyi = teamService.members().entries().get(0);
        
        Championship championship = teamService.loadChampionships().get(0);
        Venue venue = teamService.loadVenues().get(0);
        
        MatchData matchData = teamService.saveNewMatchData(new MatchData(0, championship, LocalDateTime.of(2019,8,8, 20,0), venue, "Jubi Titáns", Optional.empty()));
        
        teamService.saveStatementForMatch(matchData, new MemberStatement(sanyi, Mark.COMING, LocalDateTime.of(2019,8,5, 8,0), ""));
        
        Match match = teamService.loadAllMatches().entries().get(0);
        
        assertEquals(Mark.COMING, match.statementFor(sanyi).get().mark());
        
        teamService.updateStatementForMatch(matchData, new MemberStatement(sanyi, Mark.NOT_COMING, LocalDateTime.of(2019,8,5, 9,0), ""));
        
        match = teamService.loadAllMatches().entries().get(0);
        
        assertEquals(Mark.NOT_COMING, match.statementFor(sanyi).get().mark());
    }
    
    @Test
    public void sendReminders() {
        
        Championship championship = teamService.loadChampionships().get(0);
        Venue venue = teamService.loadVenues().get(0);
        
        MatchData matchData = teamService.saveNewMatchData(new MatchData(0, championship, LocalDateTime.of(2019,8,8, 20,0), venue, "Jubi Titáns", Optional.empty()));
        
        Clock.setStaticDate(LocalDateTime.of(2019,8,4, 8,0));
        
        int remindersSent = teamService.sendReminders();
        assertEquals(0, remindersSent);
        
        Clock.setStaticDate(LocalDateTime.of(2019,8,7, 8,0));
        
        remindersSent = teamService.sendReminders();
        assertEquals(2, remindersSent);
        
        Member sanyi = teamService.members().entries().get(0);
        teamService.saveStatementForMatch(matchData, new MemberStatement(sanyi, Mark.COMING, LocalDateTime.of(2019,8,5, 8,0), ""));
        
        remindersSent = teamService.sendReminders();
        assertEquals(1, remindersSent);
    }
    
}
