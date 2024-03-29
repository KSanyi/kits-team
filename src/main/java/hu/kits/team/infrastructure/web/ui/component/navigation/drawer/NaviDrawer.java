package hu.kits.team.infrastructure.web.ui.component.navigation.drawer;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;

import elemental.json.JsonObject;
import hu.kits.team.infrastructure.web.ui.component.util.UIUtils;

@CssImport("./styles/components/navi-drawer.css")
@JsModule("./swipe-away.js")
public class NaviDrawer extends Composite<Div> implements AfterNavigationObserver {

    private static final String CLASS_NAME = "navi-drawer";
    private static final String RAIL = "rail";
    private static final String OPEN = "open";

    private Div scrim;

    private Div mainContent;
    private Div scrollableArea;

    private Button railButton;
    private NaviMenu menu;

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        UI ui = attachEvent.getUI();
        ui.getPage().executeJs("window.addSwipeAway($0,$1,$2,$3)", mainContent.getElement(), this, "onSwipeAway", scrim.getElement());
    }

    @ClientCallable
    public void onSwipeAway(@SuppressWarnings("unused") JsonObject data) {
        close();
    }

    public NaviDrawer() {
        getContent().setClassName(CLASS_NAME);

        initScrim();
        initMainContent();

        initHeader();

        initScrollableArea();
        initMenu();

        initFooter();
    }

    private void initScrim() {
        // Backdrop on small viewports
        scrim = new Div();
        scrim.addClassName(CLASS_NAME + "__scrim");
        scrim.addClickListener(event -> close());
        getContent().add(scrim);
    }

    private void initMainContent() {
        mainContent = new Div();
        mainContent.addClassName(CLASS_NAME + "__content");
        getContent().add(mainContent);
    }

    private void initHeader() {
        mainContent.add(new BrandExpression("BDO - Lúzer FC"));
    }

    private void initScrollableArea() {
        scrollableArea = new Div();
        scrollableArea.addClassName(CLASS_NAME + "__scroll-area");
        mainContent.add(scrollableArea);
    }

    private void initMenu() {
        menu = new NaviMenu();
        scrollableArea.add(menu);
    }

    private void initFooter() {
        railButton = UIUtils.createSmallButton("Elrejt", VaadinIcon.CHEVRON_LEFT_SMALL);
        railButton.addClassName(CLASS_NAME + "__footer");
        railButton.addClickListener(event -> toggleRailMode());
        railButton.getElement().setAttribute("aria-label", "Collapse menu");
        mainContent.add(railButton);
    }

    private void toggleRailMode() {
        if (getElement().hasAttribute(RAIL)) {
            getElement().setAttribute(RAIL, false);
            railButton.setIcon(new Icon(VaadinIcon.CHEVRON_LEFT_SMALL));
            railButton.setText("Elrejt");
            railButton.getElement().setAttribute("aria-label", "Collapse menu");
        } else {
            getElement().setAttribute(RAIL, true);
            railButton.setIcon(new Icon(VaadinIcon.CHEVRON_RIGHT_SMALL));
            railButton.setText("Kinyit");
            railButton.getElement().setAttribute("aria-label", "Expand menu");
            getUI().get().getPage().executeJs(
                    "var originalStyle = getComputedStyle($0).pointerEvents;" //
                            + "$0.style.pointerEvents='none';" //
                            + "setTimeout(function() {$0.style.pointerEvents=originalStyle;}, 170);",
                    getElement());
        }
    }

    public void toggle() {
        if (getElement().hasAttribute(OPEN)) {
            close();
        } else {
            open();
        }
    }

    private void open() {
        getElement().setAttribute(OPEN, true);
    }

    private void close() {
        getElement().setAttribute(OPEN, false);
        applyIOS122Workaround();
    }

    private void applyIOS122Workaround() {
        // iOS 12.2 sometimes fails to animate the menu away.
        // It should be gone after 240ms
        // This will make sure it disappears even when the browser fails.
        getUI().get().getPage().executeJs(
                "var originalStyle = getComputedStyle($0).transitionProperty;" //
                        + "setTimeout(function() {$0.style.transitionProperty='padding'; requestAnimationFrame(function() {$0.style.transitionProperty=originalStyle})}, 250);",
                mainContent.getElement());
    }

    public NaviMenu getMenu() {
        return menu;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        close();
    }

}
