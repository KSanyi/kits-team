package hu.kits.team.infrastructure.web.ui.component.navigation;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;

import hu.kits.team.infrastructure.web.ui.component.util.UIUtils;
import hu.kits.team.infrastructure.web.ui.component.util.css.Overflow;
import hu.kits.team.infrastructure.web.ui.view.match.FilterTab;
import hu.kits.team.infrastructure.web.ui.view.match.MatchView;
import hu.kits.team.infrastructure.web.ui.view.match.StatementFilter;

/**
 * NaviTabs supports tabs that can be closed, and that can navigate to a
 * specific target when clicked.
 */
public class NaviTabs extends Tabs {

    private ComponentEventListener<SelectedChangeEvent> listener = (ComponentEventListener<SelectedChangeEvent>) selectedChangeEvent -> navigateToSelectedTab();

    public NaviTabs() {
        getElement().setAttribute("overflow", "end");
        UIUtils.setOverflow(Overflow.HIDDEN, this);
        addSelectedChangeListener(listener);
    }

    /**
     * When adding the first tab, the selection change event is triggered. This
     * will cause the app to navigate to that tab's navigation target (if any).
     * This constructor allows you to add the tabs before the event listener is
     * set.
     */
    public NaviTabs(NaviTab... naviTabs) {
        this();
        add(naviTabs);
    }

    /**
     * Creates a regular tab without any click listeners.
     */
    public FilterTab addTab(StatementFilter caption, int count) {
        FilterTab tab = new FilterTab(caption, count);
        add(tab);
        return tab;
    }

    /**
     * Creates a tab that when clicked navigates to the specified target.
     */
    public Tab addTab(String text,
            Class<? extends Component> navigationTarget) {
        Tab tab = new NaviTab(text, navigationTarget);
        add(tab);
        return tab;
    }

    /**
     * Navigates to the selected tab's navigation target if available.
     */
    public void navigateToSelectedTab() {
        if (getSelectedTab() instanceof NaviTab) {
            try {
                UI.getCurrent().navigate(
                        ((NaviTab) getSelectedTab()).getNavigationTarget());
            } catch (Exception e) {
                // @todo this is an code flow by exception anti-pattern. Either
                // handle the case without the exception, or
                // @todo at least document meticulously why this can't be done
                // any other way and what kind of exceptions are we catching
                // @todo and when they can occur.
                // @todo this block consumes all exceptions, even
                // backend-originated, and may result in exceptions disappearing
                // mysteriously.

                // If the right-most tab is closed, the Tabs component does not
                // auto-select tabs on the left.
                if (getTabCount() > 0) {
                    setSelectedIndex(getTabCount() - 1);
                } else {
                    UI.getCurrent().navigate(MatchView.class);
                }
            }
        }
    }

    /**
     * Updates the current tab's name and navigation target.
     */
    public void updateSelectedTab(String text,
            Class<? extends Component> navigationTarget) {
        Tab tab = getSelectedTab();
        tab.setLabel(text);

        if (tab instanceof NaviTab) {
            ((NaviTab) tab).setNavigationTarget(navigationTarget);
        }

        navigateToSelectedTab();
    }

    /**
     * Returns the number of tabs.
     */
    public int getTabCount() {
        return Math.toIntExact(getChildren()
                .filter(component -> component instanceof Tab).count());
    }

}
