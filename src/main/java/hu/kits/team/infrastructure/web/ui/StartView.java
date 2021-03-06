package hu.kits.team.infrastructure.web.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

import hu.kits.team.infrastructure.web.ui.view.LoginView;

@Route(value = "")
public class StartView extends Div implements BeforeEnterObserver {

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        event.forwardTo(LoginView.class);
    }

}
