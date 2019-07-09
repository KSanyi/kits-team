package hu.kits.team.infrastructure.web.ui.view.match;

import com.vaadin.flow.component.tabs.Tab;

public class TabWithData extends Tab {

    public final Object data;
    
    public TabWithData(String caption, Object data) {
        super(caption);
        this.data = data;
    }
    
}
