package hu.kits.team.infrastructure.web.ui.view;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import hu.kits.team.Main;
import hu.kits.team.domain.Member;
import hu.kits.team.infrastructure.web.ui.component.util.UIUtils;
import hu.kits.team.infrastructure.web.ui.vaadin.CookieUtil;
import hu.kits.team.infrastructure.web.ui.vaadin.Session;
import hu.kits.team.infrastructure.web.ui.view.match.MatchView;

@Route(value = "login")
@PageTitle("Login")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private final ComboBox<Member> memberCombo = new ComboBox<>("Felhasználó");
    private final PasswordField passwordField = new PasswordField("Jelszó");
    private final Button loginButton = new Button("Log in", click -> login());
    
    public LoginView() {
        
        initView();
        memberCombo.setItems(Main.teamService.members().entries());
        memberCombo.setValue(Main.teamService.members().entries().get(0));
    }
    
    private void initView() {
        setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        setSpacing(false);
        setMargin(false);
        
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButton.setHeight("60px");
        
        add(memberCombo, passwordField, loginButton);
        
        setMaxWidth("600px");
    }
    
    private void login() {
        
        Member member = memberCombo.getValue();
        String password = passwordField.getValue();
        if(member.passwordHash.equals(password)) {
            Session.setMember(member);
            getUI().ifPresent(ui -> ui.navigate(MatchView.class));
            log.info(member + " logged in");
            CookieUtil.createUserCookie(member.id);
        } else {
            UIUtils.showErrorNotification("Hibás jelszó");
            log.warn("Failed authentication for member {} with password {}", member, password);
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Member member = Session.currentMember();
        if(member == null) {
            Optional<String> userFromCookie = CookieUtil.findUserFromCookie();
            if(userFromCookie.isPresent()) {
                Optional<Member> memberFromCookie = Main.teamService.members().findById(userFromCookie.get());
                if(memberFromCookie.isPresent()) {
                    member = memberFromCookie.get();
                    log.info(member + " logged in from cookie");
                } else {
                    // unknown user id in cookie
                    log.info("Unknown user found in cookie: {}", userFromCookie.get());
                    return;
                }
            } else {
                // no user in cookie
                return;
            }
        }
        
        Session.setMember(member);
        VaadinSession.getCurrent().getSession().setMaxInactiveInterval(60 * 60 * 24);
        
        event.forwardTo(MatchView.class);
    }

}
