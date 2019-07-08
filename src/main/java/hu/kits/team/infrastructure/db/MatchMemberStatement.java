package hu.kits.team.infrastructure.db;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import hu.kits.team.domain.MemberStatement;

class MatchMemberStatement {

    final long matchId;
    
    final MemberStatement memberStatement;

    public MatchMemberStatement(long matchId, MemberStatement memberStatement) {
        this.matchId = matchId;
        this.memberStatement = memberStatement;
    }
    
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
}
