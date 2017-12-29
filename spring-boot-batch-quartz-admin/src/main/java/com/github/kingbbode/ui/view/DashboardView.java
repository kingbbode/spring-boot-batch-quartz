package com.github.kingbbode.ui.view;

import com.github.kingbbode.scheduler.service.SchedulerService;
import com.github.kingbbode.ui.view.component.SchedulerInfoComponent;
import com.vaadin.spring.annotation.SpringView;
import org.springframework.beans.factory.annotation.Autowired;

@SpringView(name = "")
public class DashboardView extends SchedulerInfoComponent {
    @Autowired
    public DashboardView(SchedulerService schedulerService) {
        super(schedulerService);
    }
}
