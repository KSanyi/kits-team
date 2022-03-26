package hu.kits.team.domain;

import java.time.LocalDateTime;

public record MemberStatement(Member member, Mark mark, LocalDateTime time, String comment) {

    public String toString() {
        return member + ": " + mark.label;
    }
    
}
