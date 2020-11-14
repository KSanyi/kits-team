package hu.kits.team.infrastructure.web.ui.component;

import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.theme.lumo.Lumo;

import hu.kits.team.infrastructure.web.ui.component.util.FontSize;
import hu.kits.team.infrastructure.web.ui.component.util.FontWeight;
import hu.kits.team.infrastructure.web.ui.component.util.LumoStyles;
import hu.kits.team.infrastructure.web.ui.component.util.UIUtils;
import hu.kits.team.infrastructure.web.ui.component.util.css.BorderRadius;

public class Initials extends FlexBoxLayout {

	private String CLASS_NAME = "initials";

	public Initials(String initials) {
	    this(initials, null);
	}
	
	public Initials(String initials, String color) {
		setAlignItems(FlexComponent.Alignment.CENTER);
		setBackgroundColor(color);
		setBorderRadius(BorderRadius._50);
		setClassName(CLASS_NAME);
		UIUtils.setFontSize(FontSize.S, this);
		UIUtils.setFontWeight(FontWeight._600, this);
		setHeight(LumoStyles.Size.M);
		setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
		setWidth(LumoStyles.Size.M);
		setTheme(Lumo.DARK);
		add(initials);
	}

}
