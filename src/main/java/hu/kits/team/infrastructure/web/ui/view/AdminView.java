package hu.kits.team.infrastructure.web.ui.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import hu.kits.team.domain.Member;
import hu.kits.team.infrastructure.web.ui.MainLayout;
import hu.kits.team.infrastructure.web.ui.SplitViewFrame;
import hu.kits.team.infrastructure.web.ui.component.navigation.AppBar;
import hu.kits.team.infrastructure.web.ui.vaadin.Session;

@Route(value = "admin", layout = MainLayout.class)
@PageTitle("Admin")
public class AdminView extends SplitViewFrame implements BeforeEnterObserver {

    private Member currentUser;
    
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        setViewContent(createView());
        initAppBar();
    }
    
    private Component createView() {
        
        Div content = new Div();
        content.addClassName("grid-view");
        
        return content;
    }
    
    private void initAppBar() {
        AppBar appBar = MainLayout.get().getAppBar();
        appBar.setTitle("Admin");
    }
    
    private void init() {
        
    }
    
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        currentUser = Session.currentMember();
        if(currentUser == null) {
            event.forwardTo(LoginView.class);
        } else {
            init();
        }
    }

}
