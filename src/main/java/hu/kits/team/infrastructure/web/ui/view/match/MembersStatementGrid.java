package hu.kits.team.infrastructure.web.ui.view.match;

import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import hu.kits.team.common.Clock;
import hu.kits.team.domain.Guest;
import hu.kits.team.domain.Mark;
import hu.kits.team.domain.Member;
import hu.kits.team.domain.Player;
import hu.kits.team.infrastructure.web.Session;
import hu.kits.team.infrastructure.web.ui.component.Initials;
import hu.kits.team.infrastructure.web.ui.component.ListItem;
import hu.kits.team.infrastructure.web.ui.component.util.LumoStyles.Color;
import hu.kits.team.infrastructure.web.ui.component.util.UIUtils;

class MembersStatementGrid extends Grid<MemberStatementRow> {

    private final MatchView matchView;
    
    private List<MemberStatementRow> items = new ArrayList<>();
    
    MembersStatementGrid(MatchView matchView) {
        
        this.matchView = matchView;

        addColumn(new ComponentRenderer<>(this::createMemberInfo))
            .setWidth(UIUtils.COLUMN_WIDTH_XL);

        addColumn(new ComponentRenderer<>(this::createComingIcon))
            .setTextAlign(ColumnTextAlign.CENTER)
            .setWidth(UIUtils.COLUMN_WIDTH_XS);
        
        setSelectionMode(SelectionMode.NONE);
    }

    private Component createMemberInfo(MemberStatementRow row) {
        Player player = row.player();
        
        if(player instanceof Member member) {
            Image avatar = new Image(UIUtils.IMG_PATH + member.id + ".png", member.getInitials());
            avatar.setWidth("50px");
            avatar.setHeight("50px");
            
            if(Session.currentMember().isAdmin()) {
                avatar.addClickListener(e -> addGoal(e, member));    
            }
            
            return new ListItem(avatar, member.nickName(), createGoalsComponent(row.goals(), member));
        } else {
            return new ListItem(new Initials("V", Color.Primary._50), player.name, createGoalsComponent(row.goals(), null));
        }
    }
    
    private Component createGoalsComponent(int goals, Member member) {
        HorizontalLayout layout = new HorizontalLayout();
        
        for(int i=0;i<goals;i++) {
            Image goalImage = new Image(UIUtils.IMG_PATH + "small-football-icon.jpg", "O");
            goalImage.setWidth("20px");
            
            if(Session.currentMember().isAdmin() && member != null) {
                goalImage.addClickListener(e -> removeGoal(e, member));
            }
            
            layout.add(goalImage);
        }
        return layout;
    }
    
    private void addGoal(ClickEvent<Image> event, Member member) {
        if(isGoalsEditable()) {
            matchView.addGoal(member);
        }
    }
    
    private void removeGoal(ClickEvent<Image> event, Member member) {
        if(isGoalsEditable()) {
            matchView.removeGoal(member);
        }
    }
    
    private Icon createComingIcon(MemberStatementRow row) {
        Icon icon = createIcon(row);
        
        if(Session.currentMember().isAdmin()) {
            ContextMenu contextMenu = new ContextMenu(icon);
            if(row.player() instanceof Member) {
                contextMenu.addItem(Mark.COMING.label, c -> matchView.coming((Member)row.player()));
                contextMenu.addItem(Mark.NOT_COMING.label, c -> matchView.notComing((Member)row.player()));
            } else if(row.player() instanceof Guest) {
                contextMenu.addItem(Mark.NOT_COMING.label, c -> matchView.notComing((Guest)row.player()));
            }
        }
        
        return icon;
    }
    
    private static Icon createIcon(MemberStatementRow row) {
        if(!row.mark().isEmpty()) {
            switch(row.mark().get()) {
                case COMING: return UIUtils.createSuccessIcon(VaadinIcon.CHECK);
                case NOT_COMING: return UIUtils.createErrorIcon(VaadinIcon.CLOSE);
            }
        }
        return UIUtils.createDisabledIcon(VaadinIcon.QUESTION);
    }

    void filter(StatementFilter filter) {
        setItems(items.stream()
                .filter(row -> filter.filter.test(row.mark()))
                .collect(toList()));
    }

    void setRows(List<MemberStatementRow> items) {
        this.items = items;
        setItems(items);
    }
    
    private boolean isGoalsEditable() {
        LocalDateTime matchStartTime = matchView.match.matchData().time();
        return Clock.now().isAfter(matchStartTime) && Clock.now().isBefore(matchStartTime.plusHours(24));
    }

}
