package hu.kits.team.infrastructure.web.ui.view.match;

import java.util.Optional;

import hu.kits.team.domain.Mark;
import hu.kits.team.domain.Member;
import hu.kits.team.domain.MemberStatement;

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

}
