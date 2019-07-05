package hu.kits.team.infrastructure.db;

import hu.kits.team.domain.MemberStatement;

class MatchMemberStatement {

    final long matchId;
    
    final MemberStatement memberStatement;

    public MatchMemberStatement(long matchId, MemberStatement memberStatement) {
        this.matchId = matchId;
        this.memberStatement = memberStatement;
    }
    
}
