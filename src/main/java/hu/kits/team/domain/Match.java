package hu.kits.team.domain;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;

public class Match {

    public final MatchData matchData;
    
    private final List<MemberStatement> memberStatements;
    
    public Match(MatchData matchData, List<MemberStatement> memberStatements) {
        this.matchData = matchData;
        this.memberStatements = memberStatements;
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
    
    public List<Member> coming() {
        return membersWithMark(Mark.COMING);
    }
    
    public List<Member> notComing() {
        return membersWithMark(Mark.NOT_COMING);
    }
    
    public List<Member> dontKnow() {
        return membersWithMark(Mark.DONT_KNOW_YET);
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
    
}
