package hu.kits.team.domain;

import java.time.LocalDateTime;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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
    
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
}
