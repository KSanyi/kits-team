package hu.kits.team.domain;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TeamService {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private final Members members;
    
    private final ChampionshipRepository championshipRepository;
    private final MatchRepository matchRepository;
   
    public TeamService(Members members, ChampionshipRepository championshipRepository, MatchRepository matchRepository) {
        this.members = members;
        this.championshipRepository = championshipRepository;
        this.matchRepository = matchRepository;
    }
    
    public Members members() {
        return members;
    }
    
    public List<Championship> loadChampionships() {
        return championshipRepository.loadAll().stream().sorted(comparing(c -> c.id)).collect(toList());
    }

    public Championship createChampionship(String name) {
        return championshipRepository.save(name);
    }
    
    public void updateChampionship(Championship championship) {
        championshipRepository.update(championship);
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
    }

    public void updateStatementForMatch(MatchData matchData, MemberStatement updatedMemberStatement) {
        matchRepository.updateStatementForMatch(matchData, updatedMemberStatement);
    }

}
