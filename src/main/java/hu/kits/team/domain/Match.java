package hu.kits.team.domain;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import hu.kits.team.common.CollectionsUtil;
import hu.kits.team.domain.email.AllGoals.GoalData;

public record Match(MatchData matchData,
        List<MemberStatement> memberStatements, 
        List<Guest> guests,
        Map<Player, Integer> goals) {

    public List<MemberStatement> memberStatements() {
        return memberStatements.stream()
                .sorted(comparing(m -> m.time()))
                .collect(toList());
    }

    public Optional<MemberStatement> statementFor(Member member) {
        return memberStatements().stream()
                .filter(m -> m.member().id == member.id)
                .findFirst();
    }
    
    public int goalsBy(Player player) {
        return goals.getOrDefault(player, 0);
    }
    
    public List<Player> coming() {
        return CollectionsUtil.concat(membersWithMark(Mark.COMING), guests);
    }
    
    public List<Member> notComing() {
        return membersWithMark(Mark.NOT_COMING);
    }
    
    private List<Member> membersWithMark(Mark mark) {
        return memberStatements().stream()
                .filter(m -> m.mark() == mark)
                .map(m -> m.member())
                .collect(toList());
    }
    
    private List<Member> withStatement() {
        return memberStatements.stream().map(MemberStatement::member).collect(toList());
    }
    
    public List<Member> noStatements(Members members) {
        return members.entries().stream().filter(m -> !withStatement().contains(m)).collect(toList());
    }
    
    public List<Guest> guests() {
        return List.copyOf(guests);
    }
    
    public int status() {
        int coming = coming().size();
        return coming - matchData.championship().numberOfPlayers();
    }
    
    public String statusString() {
        int status = status();
        return (status > 0 ? "+" : "") + status + "/" + withStatement().size();
    }
    
    public Stream<GoalData> goalDatas() {
        return goals.entrySet().stream()
                .map(e -> new GoalData(e.getKey(), matchData.time().toLocalDate(), matchData.championship(), e.getValue()));
    }
    
    public LocalDateTime markCutoffTime() {
        return matchData.time().minusDays(3);
    }
    
}
