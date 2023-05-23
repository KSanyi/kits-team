package hu.kits.team.infrastructure.web.ui.component.navigation;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;

import hu.kits.team.domain.Member;
import hu.kits.team.infrastructure.web.CookieUtil;
import hu.kits.team.infrastructure.web.Session;
import hu.kits.team.infrastructure.web.ui.MainLayout;
import hu.kits.team.infrastructure.web.ui.component.FlexBoxLayout;
import hu.kits.team.infrastructure.web.ui.component.util.LumoStyles;
import hu.kits.team.infrastructure.web.ui.component.util.UIUtils;
import hu.kits.team.infrastructure.web.ui.view.match.TabWithData;

@CssImport("./styles/components/app-bar.css")
public class AppBar extends Composite<FlexLayout> {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private String CLASS_NAME = "app-bar";

    private FlexBoxLayout container;

    private Button menuIcon;
    private Button contextIcon;

    private H5 title;
    private HorizontalLayout subTitleContainer;
    private FlexBoxLayout actionItems;
    private Image avatar;

    private HorizontalLayout buttonsContainer;
    private FlexBoxLayout tabContainer;
    private NaviTabs tabs;
    private Button addTab;
    private Div preTabContainer = new Div();
    private List<Registration> tabEventRegistrations = new ArrayList<>();

    public enum NaviMode {
        MENU, CONTEXTUAL
    }

    public AppBar(String title, NaviTab... tabs) {
        getContent().setClassName(CLASS_NAME);
        getElement().setAttribute(LumoStyles.THEME, LumoStyles.DARK);

        initMenuIcon();
        initContextIcon();
        initTitle(title);
        initAvatar();
        initActionItems();
        initContainer();
        initSubtitleContainer();
        initButtons();
        initTabs(tabs);
    }

    public void setNaviMode(NaviMode mode) {
        if (mode.equals(NaviMode.MENU)) {
            menuIcon.setVisible(true);
            contextIcon.setVisible(false);
        } else {
            menuIcon.setVisible(false);
            contextIcon.setVisible(true);
        }
    }

    private void initMenuIcon() {
        menuIcon = UIUtils.createTertiaryInlineButton(VaadinIcon.MENU);
        menuIcon.removeThemeVariants(ButtonVariant.LUMO_ICON);
        menuIcon.addClassName(CLASS_NAME + "__navi-icon");
        menuIcon.addClickListener(e -> MainLayout.get().getNaviDrawer().toggle());
        UIUtils.setAriaLabel("Menu", menuIcon);
    }

    private void initContextIcon() {
        contextIcon = UIUtils.createTertiaryInlineButton(VaadinIcon.ARROW_LEFT);
        contextIcon.removeThemeVariants(ButtonVariant.LUMO_ICON);
        contextIcon.addClassNames(CLASS_NAME + "__context-icon");
        contextIcon.setVisible(false);
        UIUtils.setAriaLabel("Back", contextIcon);
    }

    private void initTitle(String title) {
        this.title = new H5(title);
        this.title.setClassName(CLASS_NAME + "__title");
    }

    private void initAvatar() {
        avatar = new Image();
        avatar.setClassName(CLASS_NAME + "__avatar");
        
        Member currentUser = Session.currentMember();
        if(currentUser != null) {
            ContextMenu contextMenu = new ContextMenu(avatar);
            contextMenu.setOpenOnClick(true);
            avatar.setAlt(currentUser.nickName());
            avatar.setSrc(UIUtils.IMG_PATH + currentUser.id + ".png");
            
            contextMenu.addItem(currentUser.name);
            contextMenu.addItem("Beállítások", e -> Notification.show("Not implemented yet.", 3000, Notification.Position.BOTTOM_CENTER));
            contextMenu.addItem("Kijelentkezés", e -> {
                VaadinSession.getCurrent().getSession().invalidate();
                CookieUtil.deleteUserCookie();
                UI.getCurrent().getPage().reload();
                log.info(Session.currentMember() + " logged out");
            });
        }
        
    }

    private void initActionItems() {
        actionItems = new FlexBoxLayout();
        actionItems.addClassName(CLASS_NAME + "__action-items");
        actionItems.setVisible(false);
    }

    private void initContainer() {
        Div spacer = new Div();
        container = new FlexBoxLayout(menuIcon, contextIcon, title, actionItems, spacer, avatar);
        container.expand(spacer);
        container.addClassName(CLASS_NAME + "__container");
        container.setAlignItems(FlexComponent.Alignment.CENTER);
        getContent().add(container);
    }
    
    private void initButtons() {
        buttonsContainer = new HorizontalLayout();
        getContent().add(buttonsContainer);
    }
    
    private void initSubtitleContainer() {
        subTitleContainer = new HorizontalLayout();
        VerticalLayout layout = new VerticalLayout(subTitleContainer);
        layout.setAlignSelf(Alignment.CENTER, subTitleContainer);
        layout.setMargin(false);
        layout.setPadding(false);
        getContent().add(layout);
    }

    private void initTabs(NaviTab... tabs) {
        addTab = UIUtils.createSmallButton(VaadinIcon.PLUS);
        addTab.setVisible(false);

        this.tabs = tabs.length > 0 ? new NaviTabs(tabs) : new NaviTabs();
        this.tabs.setClassName(CLASS_NAME + "__tabs");
        this.tabs.setVisible(false);
        for (NaviTab tab : tabs) {
            configureTab(tab);
        }

        tabContainer = new FlexBoxLayout(preTabContainer, this.tabs, addTab);
        tabContainer.addClassName(CLASS_NAME + "__tab-container");
        tabContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        getContent().add(tabContainer);
    }

    /* === MENU ICON === */

    public Button getMenuIcon() {
        return menuIcon;
    }

    /* === CONTEXT ICON === */

    public Button getContextIcon() {
        return contextIcon;
    }

    public void setContextIcon(Icon icon) {
        contextIcon.setIcon(icon);
        contextIcon.removeThemeVariants(ButtonVariant.LUMO_ICON);
    }

    /* === TITLE === */

    public String getTitle() {
        return this.title.getText();
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }
    
    /* === ACTION ITEMS === */

    public Component addActionItem(Component component) {
        actionItems.add(component);
        updateActionItemsVisibility();
        return component;
    }

    public Button addActionItem(VaadinIcon icon) {
        Button button = UIUtils.createButton(icon, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
        addActionItem(button);
        return button;
    }

    public void removeAllActionItems() {
        actionItems.removeAll();
        updateActionItemsVisibility();
    }

    /* === TABS === */

    public void centerTabs() {
        tabs.addClassName(LumoStyles.Margin.Horizontal.AUTO);
    }

    private void configureTab(Tab tab) {
        tab.addClassName(CLASS_NAME + "__tab");
        updateTabsVisibility();
    }

    public TabWithData addTab(String caption, Object data) {
        TabWithData tab = tabs.addTab(caption, data);
        configureTab(tab);
        return tab;
    }

    public Tab addTab(String text, Class<? extends Component> navigationTarget) {
        Tab tab = tabs.addTab(text, navigationTarget);
        configureTab(tab);
        return tab;
    }

    public Tab getSelectedTab() {
        return tabs.getSelectedTab();
    }

    public void setSelectedTab(Tab selectedTab) {
        tabs.setSelectedTab(selectedTab);
    }

    public void updateSelectedTab(String text, Class<? extends Component> navigationTarget) {
        tabs.updateSelectedTab(text, navigationTarget);
    }

    public void navigateToSelectedTab() {
        tabs.navigateToSelectedTab();
    }

    public void addTabSelectionListener(ComponentEventListener<Tabs.SelectedChangeEvent> listener) {
        tabEventRegistrations.add(tabs.addSelectedChangeListener(listener));
    }

    public int getTabCount() {
        return tabs.getTabCount();
    }

    public void removeAllTabs() {
        tabs.removeAll();
        tabEventRegistrations.forEach(Registration::remove);
        tabEventRegistrations.clear();
        updateTabsVisibility();
    }

    /* === ADD TAB BUTTON === */

    public void setAddTabVisible(boolean visible) {
        addTab.setVisible(visible);
    }

    /* === RESET === */

    public void reset() {
        title.setText("");
        setNaviMode(AppBar.NaviMode.MENU);
        removeAllActionItems();
        removeAllTabs();
    }

    /* === UPDATE VISIBILITY === */

    private void updateActionItemsVisibility() {
        actionItems.setVisible(actionItems.getComponentCount() > 0);
    }

    private void updateTabsVisibility() {
        tabs.setVisible(tabs.getComponentCount() > 0);
    }

    public Image getAvatar() {
        return avatar;
    }

    public void setPreTabComponent(Component createRoundBadge) {
        preTabContainer.removeAll();
        preTabContainer.add(createRoundBadge);
    }
    
    public void hideButtonsContainer() {
        buttonsContainer.setVisible(false);
    }
    
    public void unHideButtonsContainer() {
        buttonsContainer.setVisible(true);
    }
    
    public HorizontalLayout getButtonsContainer() {
        return buttonsContainer;
    }
    
    public void hideSubTitleContainer() {
        subTitleContainer.setVisible(false);
    }
    
    public void unHideSubTitleContainer() {
        subTitleContainer.setVisible(true);
    }
    
    public HorizontalLayout getSubTitleContainer() {
        return subTitleContainer;
    }
    
}
