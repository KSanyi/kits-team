package hu.kits.team.infrastructure.web.ui;

import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;

public class MembersStatementGrid extends Grid<MemberStatementRow> {
    
    public MembersStatementGrid() {
        
        addColumn(MemberStatementRow::name)
            .setHeader("Név")
            .setSortable(true);
        
        addColumn(MemberStatementRow::mark)
            .setHeader("Jön?")
            .setTextAlign(ColumnTextAlign.CENTER)
            .setSortable(true);
        
        setHeightByRows(true);
    }

}
