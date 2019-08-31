package hu.kits.team.infrastructure.web.ui.view.matches;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import hu.kits.team.Main;
import hu.kits.team.domain.Member;
import hu.kits.team.infrastructure.web.ui.MainLayout;
import hu.kits.team.infrastructure.web.ui.SplitViewFrame;
import hu.kits.team.infrastructure.web.ui.component.navigation.AppBar;
import hu.kits.team.infrastructure.web.ui.vaadin.Session;
import hu.kits.team.infrastructure.web.ui.view.LoginView;
import hu.kits.team.infrastructure.web.ui.view.match.TabWithData;

@Route(value = "matches", layout = MainLayout.class)
@PageTitle("Meccsek")
public class MatchesView extends SplitViewFrame implements BeforeEnterObserver {

    private final MatchGrid matchGrid = new MatchGrid();
    
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        setViewContent(createView());
        initAppBar();
        init();
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
        appBar.setPreTabComponent(new Div());
        appBar.addActionItem(new Div());
        
        appBar.removeAllTabs();
        for(MatchDateFilter filter : MatchDateFilter.values()) {
            appBar.addTab(filter.label, filter);
        }
        
        appBar.addTabSelectionListener(e -> {
            filter();
            //detailsDrawer.hide();
        });
        appBar.centerTabs();
    }
    
    private void filter() {
        TabWithData selectedTab = (TabWithData)MainLayout.get().getAppBar().getSelectedTab();
        if (selectedTab != null) {
            matchGrid.filter((MatchDateFilter)selectedTab.data);
        }
    }
    
    private void init() {
        matchGrid.setRows(Main.teamService.loadAllMatches().entries());
        filter();
    }
    
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Member currentUser = Session.currentMember();
        if(currentUser == null) {
            event.forwardTo(LoginView.class);
        } else {
            log.info(Session.currentMember() + " navigated to matches");
        }
    }

}
