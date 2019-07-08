package hu.kits.team.infrastructure.web.ui.view.matches;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import hu.kits.team.Main;
import hu.kits.team.domain.Member;
import hu.kits.team.infrastructure.web.ui.MainLayout;
import hu.kits.team.infrastructure.web.ui.SplitViewFrame;
import hu.kits.team.infrastructure.web.ui.component.navigation.AppBar;
import hu.kits.team.infrastructure.web.ui.view.LoginView;

@Route(value = "matches", layout = MainLayout.class)
@PageTitle("Meccsek")
public class MatchesView extends SplitViewFrame implements BeforeEnterObserver {

    private final MatchGrid matchGrid = new MatchGrid();
    
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        setViewContent(createView());
        initAppBar();
    }
    
    private Component createView() {
        
        matchGrid.setHeight("100%");
        
        Div content = new Div(matchGrid);
        content.addClassName("grid-view");
        
        return content;
    }
    
    private void initAppBar() {
        AppBar appBar = MainLayout.get().getAppBar();
        appBar.setTitle("Meccsek");
    }
    
    private void init() {
        matchGrid.setRows(Main.teamService.loadAllMatches().entries());
    }
    
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Member currentUser = (Member)VaadinSession.getCurrent().getAttribute("current-user");
        if(currentUser == null) {
            event.forwardTo(LoginView.class);
        } else {
            init();
            log.info(VaadinSession.getCurrent().getAttribute("current-user") + " navigated to matches");
        }
    }

}
