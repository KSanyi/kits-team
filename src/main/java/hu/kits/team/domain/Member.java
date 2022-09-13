package hu.kits.team.domain;

import static java.util.stream.Collectors.joining;

import java.util.stream.Stream;

import hu.kits.team.common.Util;

public class Member extends Player {

    public final String id;
    
    public final String email;
    
    public final String passwordHash;
    
    public Role role;
    
    public Member(String id, String name, String email, String passwordHash, Role role) {
        super(name);
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }
    
    @Override
    public String toString() {
        return nickName();
    }

    public String nickName() {
        String[] nameParts = id.split("_");
        return Stream.of(nameParts).map(namePart -> Util.withCapital(namePart)).collect(joining(" "));
    }
    
    public String firstName() {
        String[] nameParts = nickName().split(" ");
        return nameParts[nameParts.length-1];
    }
    
    public String getInitials() {
        return Stream.of(name.split(" "))
                .map(part -> part.substring(0,  1).toUpperCase())
                .collect(joining(""));
    }
    
    public static enum Role {
        ADMIN, MEMBER, TEMP_MEMBER, 
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    public boolean isTempMember() {
        return role == Role.TEMP_MEMBER;
    }

}
