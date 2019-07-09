package hu.kits.team.infrastructure.web.ui;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.Div;

public class ViewFrame extends Composite<Div> implements HasStyle {

    private final String CLASS_NAME = "view-frame";

    private final Div header = new Div();
    private final Div content = new Div();
    private final Div footer = new Div();

    public ViewFrame() {
        setClassName(CLASS_NAME);

        header.setClassName(CLASS_NAME + "__header");
        content.setClassName(CLASS_NAME + "__content");
        footer.setClassName(CLASS_NAME + "__footer");

        getContent().add(header, content, footer);
    }

    public void setViewHeader(Component... components) {
        header.removeAll();
        header.add(components);
    }

    public void setViewContent(Component... components) {
        content.removeAll();
        content.add(components);
    }

    public void setViewFooter(Component... components) {
        footer.removeAll();
        footer.add(components);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        MainLayout.get().getAppBar().reset();
    }

}
