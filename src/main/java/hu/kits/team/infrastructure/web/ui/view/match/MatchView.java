package hu.kits.team.infrastructure.web.ui.view.match;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import hu.kits.team.Main;
import hu.kits.team.common.Clock;
import hu.kits.team.common.Formatters;
import hu.kits.team.domain.Mark;
import hu.kits.team.domain.Match;
import hu.kits.team.domain.Matches;
import hu.kits.team.domain.Member;
import hu.kits.team.domain.MemberStatement;
import hu.kits.team.infrastructure.web.ui.MainLayout;
import hu.kits.team.infrastructure.web.ui.ViewFrame;
import hu.kits.team.infrastructure.web.ui.component.FlexBoxLayout;
import hu.kits.team.infrastructure.web.ui.component.navigation.AppBar;
import hu.kits.team.infrastructure.web.ui.component.util.LumoStyles;
import hu.kits.team.infrastructure.web.ui.component.util.UIUtils;
import hu.kits.team.infrastructure.web.ui.view.LoginView;

@Route(value = "match", layout = MainLayout.class)
@PageTitle("Meccs")
public class MatchView extends ViewFrame implements HasUrlParameter<Long>, BeforeEnterObserver {

    protected static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private Member currentMember;
    private Long matchId;
    private Match match;
    
    private final Button comingButton = UIUtils.createSuccessPrimaryButton("Jövök", VaadinIcon.CHECK);
    private final Button notComingButton = UIUtils.createErrorButton("Nem jövök", VaadinIcon.CLOSE);
    
    private Optional<MemberStatement> myStatement;
    
    private final MembersStatementGrid membersStatementGrid = new MembersStatementGrid();
    
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        setViewContent(createView());
        setViewFooter(createButtonBar());
        init();
        initAppBar();
    }
    
    private void initAppBar() {
        AppBar appBar = MainLayout.get().getAppBar();
        appBar.setTitle(Formatters.formatDateTime(match.matchData.time) + " vs " + match.matchData.opponent);

        appBar.setPreTabComponent(createStatusBadge());
        
        List<Optional<Mark>> statements = new ArrayList<>();
        match.noStatements(Main.teamService.members()).stream().forEach(s -> statements.add(Optional.empty()));
        match.memberStatements().stream().forEach(s -> statements.add(Optional.of(s.mark)));
        
        appBar.removeAllTabs();
        for(StatementFilter filter : StatementFilter.values()) {
            int count = (int)statements.stream().filter(filter.filter::test).count();
            appBar.addTab(filter, count);
        }
        
        appBar.addTabSelectionListener(e -> {
            filter();
            //detailsDrawer.hide();
        });
        appBar.centerTabs();
    }
    
    private Component createStatusBadge() {
        String statusString = match.statusString();
        FlexBoxLayout badge = UIUtils.createRoundBadge(statusString);
        
        if(statusString.startsWith("+")) {
            badge.setBackgroundColor(LumoStyles.Color.Success._100);
        } else if(statusString.startsWith("-")) {
            badge.setBackgroundColor(LumoStyles.Color.Error._100);
        } else {
            badge.setBackgroundColor(LumoStyles.Color.Contrast._50);
        }
        
        return badge;
    }
    
    private Component createView() {
        membersStatementGrid.setHeight("100%");
        return membersStatementGrid;
    }
    
    private Component createButtonBar() {
        comingButton.setSizeFull();
        notComingButton.setSizeFull();
        HorizontalLayout buttonBar = new HorizontalLayout(comingButton, notComingButton);
        buttonBar.setSpacing(false);
        buttonBar.setSizeFull();
        buttonBar.setHeight("60px");
        comingButton.addClickListener(click -> coming());
        notComingButton.addClickListener(click -> notComing());
        
        return buttonBar;
    }
    
    private void filter() {
        FilterTab selectedTab = (FilterTab)MainLayout.get().getAppBar().getSelectedTab();
        if (selectedTab != null) {
            membersStatementGrid.filter(selectedTab.filter);
        }
    }
    
    private void initButtons() {
        if(Clock.now().isBefore(match.matchData.time)) {
            if(myStatement.isPresent()) {
                if(myStatement.get().mark == Mark.COMING) {
                    comingButton.setVisible(false);
                    notComingButton.setVisible(true);
                    notComingButton.setText("Mégsem jövök");
                } else if(myStatement.get().mark == Mark.NOT_COMING) {
                    notComingButton.setVisible(false);
                    comingButton.setVisible(true);
                    comingButton.setText("Mégis jövök");
                }
            } 
        } else {
            setViewFooter(new Div());
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        currentMember = (Member)VaadinSession.getCurrent().getAttribute("current-user");
        if(currentMember == null) {
            event.forwardTo(LoginView.class);
        } else {
            log.info(VaadinSession.getCurrent().getAttribute("current-user") + " navigated to MatchView");
        }
    }
    
    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Long parameter) {
        matchId = parameter;
    }
    
    private void init() {
        
        match = loadMatch();
        myStatement = match.statementFor(currentMember);
        
        membersStatementGrid.setRows(MemberStatementRow.createForMatch(Main.teamService.members(), match), currentMember);
        filter();
        initButtons();
        
        try {
            // TODO
            initAppBar();
        }catch(Exception e) {}
    }
    
    private Match loadMatch() {
        Matches matches = Main.teamService.loadAllMatches();
        if(matchId != null) {
            return matches.find(matchId).orElseThrow(() -> new IllegalArgumentException("Unknown match id: " + matchId));
        } else {
            return matches.findNext(Clock.now());
        }
    }
    
    private void coming() {
        saveStatement(Mark.COMING);
    }
    
    private void notComing() {
        saveStatement(Mark.NOT_COMING);
    }
    
    private void saveStatement(Mark mark) {
        if(myStatement.isEmpty()) {
            Main.teamService.saveStatementForMatch(match.matchData, new MemberStatement(currentMember, mark, Clock.now(), ""));
        } else {
            Main.teamService.updateStatementForMatch(match.matchData, new MemberStatement(currentMember, mark, Clock.now(), ""));
        }
        init();
    }

}
