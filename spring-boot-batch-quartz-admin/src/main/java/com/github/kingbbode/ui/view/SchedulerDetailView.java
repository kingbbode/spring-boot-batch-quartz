package com.github.kingbbode.ui.view;

import com.github.kingbbode.ui.view.component.SchedulerDetailComponent;
import com.vaadin.spring.annotation.SpringView;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;

/**
 * Created by YG-MAC on 2017. 12. 18..
 */
@SpringView(name = "detail")
public class SchedulerDetailView extends SchedulerDetailComponent {
    public SchedulerDetailView(CacheManager cacheManager) {
        super(((EhCacheCacheManager) cacheManager).getCacheManager());
    }
}
