package com.wellsoft.pt.app.listener;

import com.wellsoft.pt.app.service.AppCodeI18nService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class AppI18nCodeCacheInitListener implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    AppCodeI18nService appCodeI18nService;


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        appCodeI18nService.updateCache();
    }
}
