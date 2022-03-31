package hu.kits.team.domain;

import java.util.Objects;

public class Guest extends Player {

    public Guest(String name) {
        super(name);
    }
    

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Player other = (Player) obj;
        return Objects.equals(name, other.name);
    }
    
}
