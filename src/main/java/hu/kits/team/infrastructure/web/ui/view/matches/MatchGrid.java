package hu.kits.team.infrastructure.web.ui.view.matches;

import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.selection.SelectionEvent;

import hu.kits.team.common.Formatters;
import hu.kits.team.domain.Match;
import hu.kits.team.domain.MatchData;
import hu.kits.team.infrastructure.web.ui.component.ListItem;
import hu.kits.team.infrastructure.web.ui.component.util.UIUtils;
import hu.kits.team.infrastructure.web.ui.view.match.MatchView;

public class MatchGrid extends Grid<Match> {

    private List<Match> items = new ArrayList<>();
    
    public MatchGrid() {

        addColumn(new ComponentRenderer<>(this::createTime))
            .setWidth(UIUtils.COLUMN_WIDTH_S);
        
        addColumn(new ComponentRenderer<>(this::createRow))
            .setWidth(UIUtils.COLUMN_WIDTH_XL);
        
        addSelectionListener(this::matchSelected);
        
    }
    
    private Component createRow(Match match) {
        MatchData matchData = match.matchData;
        
        ListItem item = new ListItem(matchData.opponent, matchData.championship.name, UIUtils.createRoundBadge(match.statusString()));
        return item;
    }
    
    private Component createTime(Match match) {
        
        MatchData matchData = match.matchData;
        
        LocalDate matchDate = matchData.time.toLocalDate();
        LocalTime matchTime = matchData.time.toLocalTime();
        
        ListItem item = new ListItem(Formatters.formatDate(matchDate), matchTime.toString());
        item.setHorizontalPadding(false);
        
        return item;
    }


    private void matchSelected(SelectionEvent<Grid<Match>, Match> selectionEvent) {
        
        selectionEvent.getFirstSelectedItem().ifPresent(match -> getUI().ifPresent(ui -> ui.navigate(MatchView.class, match.matchData.id)));
    }
    
    public void filter(MatchDateFilter filter) {
        setItems(items.stream()
                .filter(match -> filter.filter.test(match.matchData.time))
                .collect(toList()));
    }

    public void setRows(List<Match> items) {
        this.items = items;
        setItems(items);
    }

}
