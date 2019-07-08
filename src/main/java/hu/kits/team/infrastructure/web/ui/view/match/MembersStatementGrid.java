package hu.kits.team.infrastructure.web.ui.view.match;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import hu.kits.team.domain.Member;
import hu.kits.team.infrastructure.web.ui.component.ListItem;
import hu.kits.team.infrastructure.web.ui.component.util.UIUtils;

public class MembersStatementGrid extends Grid<MemberStatementRow> {

    private List<MemberStatementRow> items = new ArrayList<>();
    
    private Member currentUser;
    
    public MembersStatementGrid() {

        addColumn(new ComponentRenderer<>(this::createMemberInfo))
            .setWidth(UIUtils.COLUMN_WIDTH_XL);

        addColumn(new ComponentRenderer<>(MembersStatementGrid::createComing))
            .setTextAlign(ColumnTextAlign.CENTER)
            .setWidth(UIUtils.COLUMN_WIDTH_XS);
        
    }

    private Component createMemberInfo(MemberStatementRow row) {
        Member member = row.member;
        
        ListItem item = new ListItem(UIUtils.createRoundBadge(member.getInitials()), member.nickName(), member.email);
        
        if(currentUser.id.equals(member.id)) {
            //item.setSuffix(UIUtils.createInitials("X"));
        }
        item.setHorizontalPadding(false);
        return item;
    }
    
    @SuppressWarnings("incomplete-switch")
    private static Component createComing(MemberStatementRow row) {
        if(!row.mark().isEmpty()) {
            switch(row.mark().get()) {
                case COMING: return UIUtils.createSuccessIcon(VaadinIcon.CHECK);
                case NOT_COMING: return UIUtils.createErrorIcon(VaadinIcon.CLOSE);
            }
        }
        return UIUtils.createDisabledIcon(VaadinIcon.QUESTION);
    }

    public void filter(StatementFilter filter) {
        setItems(items.stream()
                .filter(row -> filter.filter.test(row.mark()))
                .collect(toList()));
    }

    public void setRows(List<MemberStatementRow> items, Member currentUser) {
        this.items = items;
        this.currentUser = currentUser;
        setItems(items);
    }

}
