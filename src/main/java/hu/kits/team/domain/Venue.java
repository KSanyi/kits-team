package hu.kits.team.domain;

public record Venue(String id, String name, String address, String gps) {

    @Override
    public String toString() {
        return name + " " + address;
    }
}
