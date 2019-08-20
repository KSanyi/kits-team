package hu.kits.team;

import javax.sql.DataSource;

import hu.kits.team.domain.ChampionshipRepository;
import hu.kits.team.domain.MatchRepository;
import hu.kits.team.domain.MemberRepository;
import hu.kits.team.domain.Members;
import hu.kits.team.domain.TeamService;
import hu.kits.team.domain.email.EmailSender;
import hu.kits.team.infrastructure.db.ChampionshipJdbiRepository;
import hu.kits.team.infrastructure.db.MatchJdbiRepository;
import hu.kits.team.infrastructure.db.MemberJdbiRepository;

public class TeamServiceFactory {

    public static TeamService create(DataSource dataSource, EmailSender emailSender) {
        
        ChampionshipRepository championshipRepository = new ChampionshipJdbiRepository(dataSource);
        MemberRepository memberRepository = new MemberJdbiRepository(dataSource);
        Members members = memberRepository.loadAll();
        MatchRepository matchRepository = new MatchJdbiRepository(dataSource, championshipRepository, members);
        
        return new TeamService(members, championshipRepository, matchRepository, emailSender);
    }
    
}
