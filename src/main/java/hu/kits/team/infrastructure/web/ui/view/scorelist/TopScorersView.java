package hu.kits.team.infrastructure.web.ui.view.scorelist;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import hu.kits.team.Main;
import hu.kits.team.domain.Championship;
import hu.kits.team.domain.Member;
import hu.kits.team.domain.email.AllGoals;
import hu.kits.team.infrastructure.web.ui.MainLayout;
import hu.kits.team.infrastructure.web.ui.ViewFrame;
import hu.kits.team.infrastructure.web.ui.component.navigation.AppBar;
import hu.kits.team.infrastructure.web.ui.vaadin.Session;
import hu.kits.team.infrastructure.web.ui.view.LoginView;
import hu.kits.team.infrastructure.web.ui.view.match.TabWithData;
import hu.kits.team.infrastructure.web.ui.view.scorelist.TopScorersFilter.ChampionshipFilter;

@Route(value = "top-scorers", layout = MainLayout.class)
@PageTitle("Góllövőlisták")
public class TopScorersView extends ViewFrame implements BeforeEnterObserver {

    protected static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private final TopScorersGrid topScorersGrid = new TopScorersGrid();
    
    private AllGoals allGoals;
    
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        topScorersGrid.setHeightFull();
        setViewContent(topScorersGrid);
        initAppBar();
        init();
    }
    
    private void initAppBar() {
        AppBar appBar = MainLayout.get().getAppBar();
        appBar.setTitle("Góllövőlisták");
        appBar.setPreTabComponent(new Div());
        appBar.addActionItem(new Div());
        
        appBar.getSubTitleContainer().removeAll();
        
        appBar.removeAllTabs();
        
        allGoals = Main.teamService.loadAllGoals();
        
        List<TopScorersFilter> filters = new ArrayList<>();
        filters.add(new TopScorersFilter.DateFilter(LocalDate.MIN, "Össz"));
        //filters.add(new TopScorersFilter.DateFilter(Clock.today().withDayOfYear(1), String.valueOf(Clock.now().getYear())));
        for(Championship championShip : Main.teamService.loadRealChampionships()) {
            filters.add(new ChampionshipFilter(championShip));
        }
        
        for(TopScorersFilter filter : filters) {
            appBar.addTab(filter.label(), filter);
        }
        
        appBar.addTabSelectionListener(e -> {
            filter();
        });
        appBar.centerTabs();
        appBar.hideButtonsContainer();
    }
    
    private void filter() {
        TabWithData selectedTab = (TabWithData)MainLayout.get().getAppBar().getSelectedTab();
        if (selectedTab != null) {
            TopScorersFilter filter = (TopScorersFilter)selectedTab.data;
            topScorersGrid.setItems(filter.apply(allGoals).playersScores());
        }
    }
    
    private void init() {
        filter();
    }
    
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Member currentUser = Session.currentMember();
        if(currentUser == null) {
            event.forwardTo(LoginView.class);
        } else {
            log.info(Session.currentMember() + " navigated to top-scorers");
        }
    }

}
