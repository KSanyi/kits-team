package hu.kits.team.infrastructure.web.ui.view.match;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Anchor;
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

import hu.kits.team.Main;
import hu.kits.team.common.Clock;
import hu.kits.team.common.Formatters;
import hu.kits.team.domain.Guest;
import hu.kits.team.domain.Mark;
import hu.kits.team.domain.Match;
import hu.kits.team.domain.Matches;
import hu.kits.team.domain.Member;
import hu.kits.team.domain.MemberStatement;
import hu.kits.team.infrastructure.web.ui.MainLayout;
import hu.kits.team.infrastructure.web.ui.ViewFrame;
import hu.kits.team.infrastructure.web.ui.component.navigation.AppBar;
import hu.kits.team.infrastructure.web.ui.component.util.LumoStyles;
import hu.kits.team.infrastructure.web.ui.component.util.UIUtils;
import hu.kits.team.infrastructure.web.ui.vaadin.Session;
import hu.kits.team.infrastructure.web.ui.view.LoginView;

@Route(value = "match", layout = MainLayout.class)
@PageTitle("Jonny Meccs")
public class MatchView extends ViewFrame implements HasUrlParameter<Long>, BeforeEnterObserver {

    protected static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private Member currentMember;
    private Long matchId;
    private Match match;
    
    private final Button comingButton = UIUtils.createSuccessPrimaryButton("Jövök", VaadinIcon.CHECK);
    private final Button notComingButton = UIUtils.createErrorButton("Nem jövök", VaadinIcon.CLOSE);
    
    private Optional<MemberStatement> myStatement;
    
    private final MembersStatementGrid membersStatementGrid = new MembersStatementGrid(this);
    
    private boolean isAttached = false;
    
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        setViewContent(createView());
        setViewFooter(createButtonBar());
        init();
        isAttached = true;
        
        /*
        UI ui = attachEvent.getUI();
        ui.getPage().executeJavaScript("window.addSwipeAway($0,$1,$2,$3)", this.getElement(), this, "onSwipeAway", this.getElement());
        */
        
    }
    
    /*
    @ClientCallable
    public void onSwipeAway(JsonObject data) {
        System.out.println(data);
    }
    */
    
    private void initAppBar() {
        AppBar appBar = MainLayout.get().getAppBar();
        appBar.setTitle(Formatters.formatDateTime2(match.matchData.time) + " vs " + match.matchData.opponent + " - " + match.matchData.venue.name);

        Component statusBadge = createStatusBadge();
        
        ContextMenu contextMenu = new ContextMenu(statusBadge);
        contextMenu.addItem("Vendég hozzádása", click -> openGuestWindow());
        contextMenu.addItem("Emlékeztető küldése", click -> sendReminders());
        
        appBar.setPreTabComponent(statusBadge);
        
        Anchor anchor = new Anchor("https://maps.google.com/?q=" + match.matchData.venue.address, "");
        anchor.setTarget("blank");
        anchor.setClassName("fa fa-map-marker-alt");
        anchor.addClassName("googleAnchor");
        appBar.removeAllActionItems();
        appBar.addActionItem(anchor);
        
        List<Optional<Mark>> statements = new ArrayList<>();
        match.noStatements(Main.teamService.members()).stream().forEach(s -> statements.add(Optional.empty()));
        match.memberStatements().stream().forEach(s -> statements.add(Optional.of(s.mark)));
        
        appBar.removeAllTabs();
        for(StatementFilter filter : StatementFilter.values()) {
            int count = (int)statements.stream().filter(filter.filter::test).count();
            String caption;
            if(filter == StatementFilter.COMING && !match.guests().isEmpty()) {
                caption = filter.label + "(" + count + "+" + match.guests().size() + "v)";
            } else {
                caption = filter.label + "(" + count + ")";
            }
            appBar.addTab(caption, filter);
        }
        
        appBar.addTabSelectionListener(e -> filter());
        appBar.centerTabs();
        appBar.unHideButtonsContainer();
        setupButtons(appBar.getButtonsContainer());
    }
    
    private void setupButtons(HorizontalLayout buttonsContainer) {
        Matches matches = Main.teamService.loadAllMatches();
        
        Optional<Match> previousMatch = matches.findPrev(match.matchData.id);
        Button prevButton = new Button(VaadinIcon.ARROW_LEFT.create());
        prevButton.setWidthFull();
        if(previousMatch.isPresent()) {
            prevButton.addClickListener(click -> gotoMatch(previousMatch.get()));
        } else {
            prevButton.setEnabled(false);
        }
        
        Optional<Match> nextMatch = matches.findNext(match.matchData.id);
        Button nextButton = new Button(VaadinIcon.ARROW_RIGHT.create());
        nextButton.setWidthFull();
        if(nextMatch.isPresent()) {
            nextButton.addClickListener(click -> gotoMatch(nextMatch.get()));
        } else {
            nextButton.setEnabled(false);
        }
        
        buttonsContainer.removeAll();
        buttonsContainer.add(prevButton, nextButton);
    }
    
    private void gotoMatch(Match match) {
        getUI().ifPresent(ui -> ui.navigate(MatchView.class, match.matchData.id));
    }
    
    private void openGuestWindow() {
        
        Consumer<String> callback = guestName -> {
            Main.teamService.addGuestForMatch(match.matchData, new Guest(guestName));
            UIUtils.showNotification(guestName + " hozzáadva");
            init();
        };
        
        new GuestWindow(callback).open();
    }
    
    private void sendReminders() {
        int remindersSent = Main.teamService.sendReminders(match);
        UIUtils.showNotification(remindersSent + " emlékeztető email küldve");
    }
    
    private Component createStatusBadge() {
        String statusString = match.statusString();
        
        if(statusString.startsWith("+")) {
            return UIUtils.createRoundBadge(statusString, LumoStyles.Color.Success._100);
        } else if(statusString.startsWith("-")) {
            return UIUtils.createRoundBadge(statusString, LumoStyles.Color.Error._100);
        } else {
            return UIUtils.createRoundBadge(statusString, LumoStyles.Color.Contrast._50);
        }
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
        comingButton.addClickListener(click -> coming(currentMember));
        notComingButton.addClickListener(click -> notComing(currentMember));
        
        return buttonBar;
    }
    
    private void filter() {
        TabWithData selectedTab = (TabWithData)MainLayout.get().getAppBar().getSelectedTab();
        if (selectedTab != null) {
            membersStatementGrid.filter((StatementFilter)selectedTab.data);
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
        currentMember = Session.currentMember();
        if(currentMember == null) {
            event.forwardTo(LoginView.class);
        } else {
            if(isAttached) {
                init();
            }
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
        initAppBar();
        log.info(Session.currentMember() + " navigated to match view: {}", match.matchData);
    }
    
    private Match loadMatch() {
        Matches matches = Main.teamService.loadAllMatches();
        if(matchId != null) {
            return matches.find(matchId).orElseThrow(() -> new IllegalArgumentException("Unknown match id: " + matchId));
        } else {
            return matches.findNext(Clock.now());
        }
    }
    
    void coming(Member member) {
        saveStatement(member, Mark.COMING);
    }
    
    void notComing(Member member) {
        saveStatement(member, Mark.NOT_COMING);
    }
    
    void notComing(Guest guest) {
        Main.teamService.removeGuestForMatch(match.matchData, guest);
        UIUtils.showNotification(guest.name + " eltávolítva");
        init();
    }
    
    private void saveStatement(Member member, Mark mark) {
        if(myStatement.isEmpty()) {
            Main.teamService.saveStatementForMatch(match.matchData, new MemberStatement(member, mark, Clock.now(), ""));
        } else {
            Main.teamService.updateStatementForMatch(match.matchData, new MemberStatement(member, mark, Clock.now(), ""));
        }
        init();
    }

}
