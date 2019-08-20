package hu.kits.team.domain;

public class Venue {

    public final String id;
    
    public final String name;
    
    public final String address;
    
    public final String gps;

    public Venue(String id, String name, String address, String gps) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.gps = gps;
    }
    
    public String toString() {
        return name + " " + address;
    }
}
