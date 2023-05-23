package hu.kits.team.infrastructure.web.ui.view.cominglist;

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
import hu.kits.team.common.Clock;
import hu.kits.team.domain.Championship;
import hu.kits.team.domain.Matches;
import hu.kits.team.domain.Member;
import hu.kits.team.infrastructure.web.Session;
import hu.kits.team.infrastructure.web.ui.MainLayout;
import hu.kits.team.infrastructure.web.ui.ViewFrame;
import hu.kits.team.infrastructure.web.ui.component.navigation.AppBar;
import hu.kits.team.infrastructure.web.ui.view.LoginView;
import hu.kits.team.infrastructure.web.ui.view.cominglist.TopPlayersFilter.ChampionshipFilter;
import hu.kits.team.infrastructure.web.ui.view.match.TabWithData;

@Route(value = "top-players", layout = MainLayout.class)
@PageTitle("Meccs lista")
public class TopPlayersView extends ViewFrame implements BeforeEnterObserver {

    protected static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private final TopPlayersGrid topPlayersGrid = new TopPlayersGrid();
    
    private Matches matches;
    
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        topPlayersGrid.setHeightFull();
        setViewContent(topPlayersGrid);
        initAppBar();
        init();
    }
    
    private void initAppBar() {
        AppBar appBar = MainLayout.get().getAppBar();
        appBar.setTitle("Meccs látogatási");
        appBar.setPreTabComponent(new Div());
        appBar.addActionItem(new Div());
        
        appBar.getSubTitleContainer().removeAll();
        
        appBar.removeAllTabs();
        
        matches = Main.teamService.loadAllMatches();
        
        List<TopPlayersFilter> filters = new ArrayList<>();
        filters.add(new TopPlayersFilter.DateFilter(LocalDate.MIN, "Össz"));
        filters.add(new TopPlayersFilter.DateFilter(Clock.today().withDayOfYear(1), String.valueOf(Clock.now().getYear())));
        for(Championship championShip : Main.teamService.loadRealChampionships()) {
            filters.add(new ChampionshipFilter(championShip));
        }
        
        for(TopPlayersFilter filter : filters) {
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
            TopPlayersFilter filter = (TopPlayersFilter)selectedTab.data;
            topPlayersGrid.setItems(filter.apply(matches).playersScores());
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
