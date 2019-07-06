package hu.kits.team.infrastructure.web.ui.view.match;

import com.vaadin.flow.component.tabs.Tab;

public class FilterTab extends Tab {

    public final StatementFilter filter;
    
    public FilterTab(StatementFilter filter, int count) {
        super(filter.label + "(" + count + ")");
        this.filter = filter;
    }
    
}
