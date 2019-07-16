package hu.kits.team.domain;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import hu.kits.team.common.CollectionsUtil;

public class Match {

    public final MatchData matchData;
    
    private final List<MemberStatement> memberStatements;
    
    private final List<Guest> guests;
    
    public Match(MatchData matchData, List<MemberStatement> memberStatements, List<Guest> guests) {
        this.matchData = matchData;
        this.memberStatements = memberStatements;
        this.guests = guests;
    }
    
    public List<MemberStatement> memberStatements() {
        return memberStatements.stream()
                .sorted(comparing(m -> m.time))
                .collect(toList());
    }

    public Optional<MemberStatement> statementFor(Member member) {
        return memberStatements().stream()
                .filter(m -> m.member.id == member.id)
                .findFirst();
    }
    
    public List<Player> coming() {
        return CollectionsUtil.concat(membersWithMark(Mark.COMING), guests);
    }
    
    public List<Member> notComing() {
        return membersWithMark(Mark.NOT_COMING);
    }
    
    private List<Member> membersWithMark(Mark mark) {
        return memberStatements().stream()
                .filter(m -> m.mark == mark)
                .map(m -> m.member)
                .collect(toList());
    }
    
    private List<Member> withStatement() {
        return memberStatements.stream().map(m -> m.member).collect(toList());
    }
    
    public List<Member> noStatements(Members members) {
        return members.entries().stream().filter(m -> !withStatement().contains(m)).collect(toList());
    }
    
    public List<Guest> guests() {
        return List.copyOf(guests);
    }
    
    public int status() {
        int coming = coming().size();
        return coming - matchData.championship.numberOfPlayers;
    }
    
    public String statusString() {
        int status = status();
        return (status > 0 ? "+" : "") + status + "/" + withStatement().size();
    }
    
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
