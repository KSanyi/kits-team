package hu.kits.team.infrastructure.web.ui.view.match;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H6;
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
import hu.kits.team.domain.Member;
import hu.kits.team.domain.MemberStatement;
import hu.kits.team.infrastructure.web.ui.MainLayout;
import hu.kits.team.infrastructure.web.ui.SplitViewFrame;
import hu.kits.team.infrastructure.web.ui.component.navigation.AppBar;
import hu.kits.team.infrastructure.web.ui.component.util.UIUtils;
import hu.kits.team.infrastructure.web.ui.view.LoginView;

@Route(value = "match", layout = MainLayout.class)
@PageTitle("Meccs")
public class MatchView extends SplitViewFrame implements HasUrlParameter<Long>, BeforeEnterObserver {

    private Match match;
    
    private final H2 timeLabel = new H2();
    private final H6 opponentLabel = new H6();
    
    private final Button comingButton = UIUtils.createSuccessPrimaryButton("Jövök", VaadinIcon.CHECK);
    private final Button notComingButton = UIUtils.createErrorButton("Nem jövök", VaadinIcon.CLOSE);
    
    private Member currentUser;
    
    private Optional<MemberStatement> myStatement;
    
    private final MembersStatementGrid membersStatementGrid = new MembersStatementGrid();
    
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        initAppBar();
        setViewContent(createView());
        setViewFooter(createButtonBar());
        filter();
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

    private Component createView() {
        
        membersStatementGrid.setHeight("100%");
        
        Div content = new Div(membersStatementGrid);
        content.addClassName("grid-view");
        
        return content;
    }
    
    private void initAppBar() {
        AppBar appBar = MainLayout.get().getAppBar();
        appBar.setTitle(Formatters.formatDateTime(match.matchData.time) + " vs " + match.matchData.opponent);
        
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
    
    private void filter() {
        FilterTab selectedTab = (FilterTab)MainLayout.get().getAppBar().getSelectedTab();
        if (selectedTab != null) {
            membersStatementGrid.filter(selectedTab.filter);
        }
    }
    
    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Long parameter) {
        if(parameter != null) {
            long matchId = parameter;
            match = Main.teamService.loadAllMatches().find(matchId).orElseThrow(() -> new IllegalArgumentException("Unknown match id: " + matchId));
        } else {
            match = Main.teamService.loadAllMatches().findNext(Clock.now());
        }
    }
    
    private void init() {
        List<MemberStatementRow> items = Main.teamService.members().entries().stream()
                .map(m1 -> new MemberStatementRow(m1, match.memberStatements().stream().filter(m2 -> m2.member.id.equals(m1.id)).findFirst())).collect(toList());
        
        myStatement = match.statementFor(currentUser);
        membersStatementGrid.setRows(items, currentUser);
        timeLabel.setText(Formatters.formatDateTime(match.matchData.time));
        opponentLabel.setText("vs " + match.matchData.opponent);
        setupButtons();
        try {
            initAppBar();
        }catch(Exception e) {}
    }
    
    private void setupButtons() {
        if(myStatement.isPresent()) {
            if(myStatement.get().mark == Mark.COMING) {
                comingButton.setVisible(false);
                notComingButton.setText("Mégsem jövök");
            } else if(myStatement.get().mark == Mark.NOT_COMING) {
                notComingButton.setVisible(false);
                comingButton.setText("Mégis jövök");
            }
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        currentUser = (Member)VaadinSession.getCurrent().getAttribute("current-user");
        if(currentUser == null) {
            event.forwardTo(LoginView.class);
        } else {
            init();
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
            Main.teamService.saveStatementForMatch(match.matchData, new MemberStatement(currentUser, mark, Clock.now(), ""));
        } else {
            Main.teamService.updateStatementForMatch(match.matchData, new MemberStatement(currentUser, mark, Clock.now(), ""));
        }
        UI.getCurrent().getPage().reload();
    }

}
