package hu.kits.team.infrastructure.web.ui;

import static java.util.stream.Collectors.toList;

import java.util.List;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import hu.kits.team.Main;
import hu.kits.team.common.Formatters;
import hu.kits.team.domain.Match;

@Route(value = "match")
@PageTitle("Meccs")
public class MatchView extends VerticalLayout implements HasUrlParameter<Long> {

    private Match match;
    
    private final H2 timeLabel = new H2();
    private final H2 opponentLabel = new H2();
    
    private final Button comingButton = new Button("Jövök");
    private final Button notComingButton = new Button("Nem jövök");
    
    private final MembersStatementGrid membersStatementGrid = new MembersStatementGrid();
    
    public MatchView() {
        
        initView();
    }
    
    private void initView() {
        setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        setSpacing(false);
        setMargin(false);
        
        comingButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        notComingButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        
        comingButton.setSizeFull();
        notComingButton.setSizeFull();
        HorizontalLayout buttonBar = new HorizontalLayout(comingButton, notComingButton);
        buttonBar.setSizeFull();
        buttonBar.setHeight("100px");
        
        add(new HorizontalLayout(timeLabel, opponentLabel), membersStatementGrid, buttonBar);
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Long parameter) {
        if(parameter != null) {
            long matchId = parameter;
            match = Main.teamService.loadAllMatches().find(matchId).orElseThrow(() -> new IllegalArgumentException("Unknown match id: " + matchId));
            
            List<MemberStatementRow> items = Main.teamService.members().entries().stream()
                    .map(m1 -> new MemberStatementRow(m1, match.memberStatements().stream().filter(m2 -> m2.member.id.equals(m1.id)).findFirst())).collect(toList());
            
            membersStatementGrid.setItems(items);
            timeLabel.setText(Formatters.formatDateTime(match.matchData.time));
            opponentLabel.setText("vs " + match.matchData.opponent);
        }
    }

}
