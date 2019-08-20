package hu.kits.team.domain;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.kits.team.common.Clock;
import hu.kits.team.domain.email.EmailCreator;
import hu.kits.team.domain.email.EmailSender;

public class TeamService {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private final Members members;
    
    private final ChampionshipRepository championshipRepository;
    private final VenueRepository venueRepository;
    private final MatchRepository matchRepository;
    private final EmailSender emailSender;
   
    public TeamService(Members members, ChampionshipRepository championshipRepository, VenueRepository venueRepository, MatchRepository matchRepository, EmailSender emailSender) {
        this.members = members;
        this.championshipRepository = championshipRepository;
        this.venueRepository = venueRepository;
        this.matchRepository = matchRepository;
        this.emailSender = emailSender;
    }
    
    public Members members() {
        return members;
    }
    
    public List<Championship> loadChampionships() {
        return championshipRepository.loadAll().stream().sorted(comparing(c -> c.id)).collect(toList());
    }

    public Championship createChampionship(String name, int numberOfPlayers) {
        return championshipRepository.save(name, numberOfPlayers);
    }
    
    public void updateChampionship(Championship championship) {
        championshipRepository.update(championship);
    }
    
    public List<Venue> loadVenues() {
        return venueRepository.loadAll().stream().sorted(comparing(v -> v.name)).collect(toList());
    }

    public MatchData saveNewMatchData(MatchData matchData) {
        return matchRepository.saveMatchData(matchData);
    }

    public Matches loadAllMatches() {
        return matchRepository.loadAllMatches();
    }

    public void updateMatchData(MatchData updatedMatchData) {
        matchRepository.updateMatchData(updatedMatchData);
    }

    public void saveStatementForMatch(MatchData matchData, MemberStatement memberStatement) {
        matchRepository.saveStatementForMatch(matchData, memberStatement);
        if(memberStatement.mark == Mark.COMING) {
            emailSender.sendEmail(EmailCreator.createCalendarEntryEmail(memberStatement.member, matchData));
        }
        log.info("Match statement saved: {} {}", matchData, memberStatement);
    }

    public void updateStatementForMatch(MatchData matchData, MemberStatement updatedMemberStatement) {
        matchRepository.updateStatementForMatch(matchData, updatedMemberStatement);
        if(updatedMemberStatement.mark == Mark.COMING) {
            emailSender.sendEmail(EmailCreator.createCalendarEntryEmail(updatedMemberStatement.member, matchData));
        }
        log.info("Match statement updated: {} {}", matchData, updatedMemberStatement);
    }
    
    public void addGuestForMatch(MatchData matchData, Guest guest) {
        matchRepository.addGuestForMatch(matchData.id, guest);
        log.info("Guest added for match {}: {}", matchData, guest);
    }

    public void removeGuestForMatch(MatchData matchData, Guest guest) {
        matchRepository.removeGuestForMatch(matchData.id, guest);
        log.info("Guest removed for match {}: {}", matchData, guest);
    }
    
    public int sendReminders() {
        
        Matches matches = loadAllMatches();
        LocalDate cutoffDate = Clock.today().minusDays(3);
        List<Match> upcomingMatches = matches.findAfter(cutoffDate);
        
        int count = 0;
        for(Match match : upcomingMatches) {
            for(Member member : match.noStatements(members)) {
                emailSender.sendEmail(EmailCreator.createReminderEmail(member, match.matchData));
                count++;
            }
        }
        
        return count;
    }

}
