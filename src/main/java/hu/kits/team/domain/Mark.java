package hu.kits.team.domain;

public enum Mark {

    COMING("Jövök"),
    NOT_COMING("Nem jövök"),
    DONT_KNOW_YET("Nem tudom még");
    
    public final String label;

    private Mark(String label) {
        this.label = label;
    }
    
}
