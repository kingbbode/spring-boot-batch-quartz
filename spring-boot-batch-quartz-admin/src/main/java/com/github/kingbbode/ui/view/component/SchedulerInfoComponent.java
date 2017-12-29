package com.github.kingbbode.ui.view.component;

import com.github.kingbbode.scheduler.dto.SchedulerResponse;
import com.github.kingbbode.scheduler.service.SchedulerService;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringView(name = "")
public class SchedulerInfoComponent extends CustomComponent implements View {

    private final SchedulerService schedulerService;

    @Autowired
    public SchedulerInfoComponent(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
        init();
    }

    private void init() {
        VerticalLayout content = new VerticalLayout();
        content.addComponent(createTitleBar());
        content.addComponent(createSchedulerGrid());
        setCompositionRoot(content);
    }

    private HorizontalLayout createTitleBar() {
        HorizontalLayout titleBar = new HorizontalLayout();
        Label title = new Label("EHCACHE LIST");
        titleBar.addComponent(title);
        titleBar.setExpandRatio(title, 1.0f);
        title.addStyleNames(ValoTheme.LABEL_H1, ValoTheme.LABEL_BOLD, ValoTheme.LABEL_COLORED);

        return titleBar;
    }

    private Grid<GroupScheduler> createSchedulerGrid() {
        Grid<GroupScheduler> grid = new Grid<>();
        grid.addColumn(GroupScheduler::getName).setCaption("Name");
        grid.addColumn(GroupScheduler::getVersion).setCaption("Latest Version");
        grid.addColumn(GroupScheduler::getSize).setCaption("Job Count");

        List<GroupScheduler> groupSchedulers = this.schedulerService.getSchedulerList()
                .stream()
                .collect(Collectors.groupingBy(SchedulerResponse::getName))
                .values()
                .stream()
                .map(schedulerResponses -> {
                            String latestVersion = schedulerResponses.stream().map(SchedulerResponse::getVersion).max(String::compareTo)
                                    .orElse(null);
                            if(latestVersion == null) {
                                return new ArrayList<SchedulerResponse>();
                            }
                            return schedulerResponses.stream().filter(schedulerResponse -> latestVersion.equals(schedulerResponse.getVersion()))
                                    .collect(Collectors.toList());
                        }
                )
                .filter(lists -> !lists.isEmpty())
                .map(GroupScheduler::new)
                .collect(Collectors.toList());
        grid.setItems();
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.NONE);

        int schedulerCount = groupSchedulers.size();
        if (schedulerCount != 0) {
            grid.setHeightByRows(schedulerCount > 10 ? 10 : schedulerCount);
        }
        return grid;
    }

    private class GroupScheduler {
        private String name;
        private String version;
        private int size;

        GroupScheduler(List<SchedulerResponse> list) {
            this.name = list.get(0).getName();
            this.version = list.get(0).getVersion();
            this.size = list.size();
        }

        String getName() {
            return name;
        }

        String getVersion() {
            return version;
        }

        int getSize() {
            return size;
        }
    }
}
