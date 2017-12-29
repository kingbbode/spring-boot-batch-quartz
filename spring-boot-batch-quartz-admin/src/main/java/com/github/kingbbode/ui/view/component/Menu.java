package com.github.kingbbode.ui.view.component;

import com.github.kingbbode.scheduler.dto.SchedulerResponse;
import com.github.kingbbode.scheduler.service.SchedulerService;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.Resource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by YG-MAC on 2017. 12. 16..
 */
public class Menu extends CssLayout {
    static final String VERSION_PARAMETER_KEY = "version";
    static final String SCHEDULE_PARAMETER_KEY = "scheduler";
    private static final String VALO_MENUITEMS = "valo-menuitems";
    private static final String VALO_MENU_TOGGLE = "valo-menu-toggle";
    private static final String VALO_MENU_VISIBLE = "valo-menu-visible";
    private final SchedulerService schedulerService;
    private final Navigator navigator;
    private Map<String, Button> viewButtons = new HashMap<>();

    private CssLayout menuItemsLayout;
    private CssLayout menuPart;

    public Menu(Navigator navigator, SchedulerService schedulerService) {
        this.navigator = navigator;
        this.schedulerService = schedulerService;
        initMenu();
        initCacheInfo();
        setPrimaryStyleName(ValoTheme.MENU_ROOT);
        addComponent(this.menuPart);
    }

    private void initMenu() {
        this.menuPart = new CssLayout();
        this.menuPart.addStyleName(ValoTheme.MENU_PART);
        // header of the menu
        final HorizontalLayout top = new HorizontalLayout();
        top.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        top.addStyleName(ValoTheme.MENU_TITLE);
        Label title = new Label("EHCACHE MONITOR");
        title.addStyleNames(ValoTheme.LABEL_H3, ValoTheme.LABEL_BOLD);
        title.setSizeUndefined();
        /*Image image = new Image(null, new
        ThemeResource("img/table-logo.png"));
        image.setStyleName("logo");
        top.addComponent(image);*/
        top.addComponent(title);
        this.menuPart.addComponent(top);
        final Button showMenu = new Button("Menu", (Button.ClickListener) event -> {
            if (this.menuPart.getStyleName().contains(VALO_MENU_VISIBLE)) {
                this.menuPart.removeStyleName(VALO_MENU_VISIBLE);
            } else {
                this.menuPart.addStyleName(VALO_MENU_VISIBLE);
            }
        });
        showMenu.addStyleName(ValoTheme.BUTTON_PRIMARY);
        showMenu.addStyleName(ValoTheme.BUTTON_SMALL);
        showMenu.addStyleName(VALO_MENU_TOGGLE);
        showMenu.setIcon(VaadinIcons.MENU);
        menuPart.addComponent(showMenu);
        menuItemsLayout = new CssLayout();
        menuItemsLayout.setPrimaryStyleName(VALO_MENUITEMS);
        menuPart.addComponent(menuItemsLayout);
    }


    private void initCacheInfo() {
        addViewButton("", "전체", VaadinIcons.LIST);
        this.schedulerService.getSchedulerList().stream()
                .collect(Collectors.groupingBy(SchedulerResponse::getName))
                .forEach((name, schedulerResponses) -> {
                    addViewLabel(name);
                    schedulerResponses.stream().map(SchedulerResponse::getVersion).distinct().forEach(version -> {
                        addViewButton(name + version, version, VaadinIcons.TIMER);
                    });
                });
    }

    public void addViewLabel(String caption) {
        Label label = new Label();
        label.setCaption(caption);
        menuItemsLayout.addComponent(label);
    }

    public void addViewButton(final String scheduler, String version,Resource icon) {
        String path = "detail/" + SCHEDULE_PARAMETER_KEY + "=" + scheduler + "&" + VERSION_PARAMETER_KEY + "=" + version;
        Button button = new Button(version, (Button.ClickListener) event -> navigator.navigateTo(path));
        button.setPrimaryStyleName(ValoTheme.MENU_ITEM);
        button.setIcon(icon);
        menuItemsLayout.addComponent(button);
        viewButtons.put(path, button);
    }

    public void setActiveView(String viewName) {
        for (Button button : viewButtons.values()) {
            button.removeStyleName("selected");
        }
        Button selected = viewButtons.get(viewName);
        if (selected != null) {
            selected.addStyleName("selected");
        }
        menuPart.removeStyleName(VALO_MENU_VISIBLE);
    }
}
