package hu.kits.team.infrastructure.web.ui;

import java.util.Optional;

import hu.kits.team.domain.Member;
import hu.kits.team.domain.MemberStatement;

public class MemberStatementRow {

    private final Member member;
    
    private final Optional<MemberStatement> memberstatement;

    public MemberStatementRow(Member member, Optional<MemberStatement> memberstatement) {
        this.member = member;
        this.memberstatement = memberstatement;
    }
    
    public String name() {
        return member.name;
    }
    
    public String mark() {
        return memberstatement.map(s -> s.mark.label).orElse("?");
    }
}
