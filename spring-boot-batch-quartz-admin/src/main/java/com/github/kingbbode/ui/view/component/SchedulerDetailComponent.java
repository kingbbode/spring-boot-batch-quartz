package com.github.kingbbode.ui.view.component;

import com.github.kingbbode.scheduler.dto.SchedulerResponse;
import com.github.kingbbode.scheduler.service.SchedulerService;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by YG-MAC on 2017. 12. 16..
 */
public class SchedulerDetailComponent extends CustomComponent implements View {

    private final SchedulerService schedulerService;

    @Autowired
    public SchedulerDetailComponent(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    private void init(String scheduleName, String version) {
        VerticalLayout content = new VerticalLayout();
        content.addComponent(createTitleBar(scheduleName, version));
        content.addComponent(createSchedulerGrid(scheduleName, version));
        setCompositionRoot(content);
    }

    private HorizontalLayout createTitleBar(String scheduleName, String version) {
        HorizontalLayout titleBar = new HorizontalLayout();
        Label title = new Label(scheduleName + " " + version);
        titleBar.addComponent(title);
        titleBar.setExpandRatio(title, 1.0f);
        title.addStyleNames(ValoTheme.LABEL_H1, ValoTheme.LABEL_BOLD, ValoTheme.LABEL_COLORED);

        return titleBar;
    }

    private Grid<SchedulerResponse> createSchedulerGrid(String scheduleName, String version) {
        Grid<SchedulerResponse> grid = new Grid<>();
        grid.addColumn(SchedulerResponse::getJobName).setCaption("Job Name");
        grid.addColumn(job -> {
            Button button = new Button(VaadinIcons.PLUS_CIRCLE);
            button.addClickListener(event -> repaint(scheduleName, version, job.getJobName()));
            return button;
        }, new ComponentRenderer()).setCaption("");
        List<SchedulerResponse> list = this.schedulerService.getSchedulerListByScheduleNameAndVersion(scheduleName, version);
        grid.setItems();
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.NONE);

        int jobCount = list.size();
        if (jobCount != 0) {
            grid.setHeightByRows(jobCount > 10 ? 10 : jobCount);
        }
        return grid;
    }

    private void repaint(String scheduleName, String version, String jobName) {

    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        String scheduleName = event.getParameterMap().getOrDefault(Menu.SCHEDULE_PARAMETER_KEY, "");
        String version = event.getParameterMap().getOrDefault(Menu.VERSION_PARAMETER_KEY, "");
        if ("".equals(scheduleName) || "".equals(version)) {
            event.getNavigator().navigateTo("/");
            return;
        }
        init(scheduleName, version);
    }
}
