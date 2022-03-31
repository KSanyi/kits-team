package hu.kits.team.domain;

public abstract class Player {

    public final String name;

    public Player(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
    }

}
