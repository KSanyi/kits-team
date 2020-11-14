package hu.kits.team.infrastructure.web.ui.component.navigation.drawer;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;

import hu.kits.team.infrastructure.web.ui.component.util.UIUtils;

@CssImport("./styles/components/brand-expression.css")
public class BrandExpression extends Composite<Div> {

    private String CLASS_NAME = "brand-expression";

    private Image logo;
    private Label title;

    public BrandExpression(String text) {
        getContent().setClassName(CLASS_NAME);

        logo = new Image(UIUtils.IMG_PATH + "logo.png", "");
        logo.addClassName(CLASS_NAME + "__logo");
        logo.setAlt(text + " logo");

        title = UIUtils.createH3Label(text);
        title.addClassName(CLASS_NAME + "__title");

        getContent().add(logo, title);
    }

}
