package hu.kits.team.domain;

import java.time.LocalDateTime;

public class MemberStatement {

    public final Member member;
    
    public final Mark mark;
    
    public final LocalDateTime time;
    
    public final String comment;

    public MemberStatement(Member member, Mark mark, LocalDateTime time, String comment) {
        this.member = member;
        this.mark = mark;
        this.time = time;
        this.comment = comment;
    }
    
}
