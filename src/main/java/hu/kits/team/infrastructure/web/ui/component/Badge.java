package hu.kits.team.infrastructure.web.ui.component;

import com.vaadin.flow.component.html.Span;

import hu.kits.team.infrastructure.web.ui.component.util.UIUtils;
import hu.kits.team.infrastructure.web.ui.component.util.css.lumo.BadgeColor;
import hu.kits.team.infrastructure.web.ui.component.util.css.lumo.BadgeShape;
import hu.kits.team.infrastructure.web.ui.component.util.css.lumo.BadgeSize;

import java.util.StringJoiner;

public class Badge extends Span {

	public Badge(String text) {
		this(text, BadgeColor.NORMAL);
	}

	public Badge(String text, BadgeColor color) {
		super(text);
		UIUtils.setTheme(color.getThemeName(), this);
	}

	public Badge(String text, BadgeColor color, BadgeSize size, BadgeShape shape) {
		super(text);
		StringJoiner joiner = new StringJoiner(" ");
		joiner.add(color.getThemeName());
		if (shape.equals(BadgeShape.PILL)) {
			joiner.add(shape.getThemeName());
		}
		if (size.equals(BadgeSize.S)) {
			joiner.add(size.getThemeName());
		}
		UIUtils.setTheme(joiner.toString(), this);
	}

}
