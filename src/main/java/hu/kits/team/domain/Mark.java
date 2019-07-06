package hu.kits.team.domain;

public enum Mark {

    COMING("Jön"),
    NOT_COMING("Nem jön"),
    DONT_KNOW_YET("Nem tudja");
    
    public final String label;

    private Mark(String label) {
        this.label = label;
    }
    
}
