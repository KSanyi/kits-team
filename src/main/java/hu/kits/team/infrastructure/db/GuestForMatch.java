package hu.kits.team.infrastructure.db;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import hu.kits.team.domain.Guest;

class GuestForMatch {

    final long matchId;
    
    final Guest guest;

    public GuestForMatch(long matchId, Guest guest) {
        this.matchId = matchId;
        this.guest = guest;
    }
    
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
}
