package hu.kits.team.infrastructure.web.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import hu.kits.team.Main;
import hu.kits.team.domain.Member;
import hu.kits.team.infrastructure.web.ui.view.match.MatchView;

@Route(value = "login")
@PageTitle("Login")
public class LoginView extends VerticalLayout {

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
        VaadinSession.getCurrent().setAttribute("current-user", memberCombo.getValue());
        getUI().ifPresent(ui -> ui.navigate(MatchView.class)); 
    }

}
