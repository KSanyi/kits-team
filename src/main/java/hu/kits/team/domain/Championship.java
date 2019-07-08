package hu.kits.team.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Championship {

    public final long id;
    
    public final String name;
    
    public final int numberOfPlayers;

    public Championship(long id, String name, int numberOfPlayers) {
        this.id = id;
        this.name = name;
        this.numberOfPlayers = numberOfPlayers;
    }
    
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
}
