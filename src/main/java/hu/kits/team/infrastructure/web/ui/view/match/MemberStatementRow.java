package hu.kits.team.infrastructure.web.ui.view.match;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;

import hu.kits.team.domain.Mark;
import hu.kits.team.domain.Match;
import hu.kits.team.domain.Member;
import hu.kits.team.domain.MemberStatement;
import hu.kits.team.domain.Members;

public class MemberStatementRow {

    public final Member member;
    
    private final Optional<MemberStatement> memberstatement;

    public MemberStatementRow(Member member, Optional<MemberStatement> memberstatement) {
        this.member = member;
        this.memberstatement = memberstatement;
    }
    
    public Optional<Mark> mark() {
        return memberstatement.map(s -> s.mark);
    }

    static List<MemberStatementRow> createForMatch(Members members, Match match) {
        return members.entries().stream()
                .map(member -> new MemberStatementRow(member, match.statementFor(member)))
                .collect(toList());
    }
    
}
