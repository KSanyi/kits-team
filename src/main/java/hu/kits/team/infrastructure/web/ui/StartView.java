package hu.kits.team.infrastructure.web.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import hu.kits.team.infrastructure.web.ui.view.LoginView;

@Route(value = "")
public class StartView extends Div implements BeforeEnterObserver {

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        VaadinSession.getCurrent().getSession().setMaxInactiveInterval(60 * 60 * 12);
        event.forwardTo(LoginView.class);
    }

}
