package hu.kits.team.domain;

import static java.util.stream.Collectors.joining;

import java.util.stream.Stream;

public class Member extends Player {

    public final String id;
    
    public final String email;
    
    public final String passwordHash;
    
    public boolean isAdmin;

    public Member(String id, String name, String email, String passwordHash, boolean isAdmin) {
        super(name);
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.isAdmin = isAdmin;
    }
    
    @Override
    public String toString() {
        return nickName();
    }

    public String nickName() {
        return id.substring(0, 1).toUpperCase() + id.substring(1);
    }

    public String getInitials() {
        return Stream.of(name.split(" "))
                .map(part -> part.substring(0,  1).toUpperCase())
                .collect(joining(""));
    }
    
}
