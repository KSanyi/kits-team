package hu.kits.team.infrastructure.web;

import com.vaadin.flow.server.VaadinSession;

import hu.kits.team.domain.Member;

public class Session {

    private static final String USER_KEY = "current-user";
    
    public static Member currentMember() {
        return (Member)VaadinSession.getCurrent().getAttribute(USER_KEY);
    }
    
    public static void setMember(Member member) {
        VaadinSession.getCurrent().setAttribute(USER_KEY, member);
    }
    
}
