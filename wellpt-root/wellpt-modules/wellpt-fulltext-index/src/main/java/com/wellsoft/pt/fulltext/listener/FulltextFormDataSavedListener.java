/*
 * @(#)6/17/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.listener;

import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.definition.event.FormDataSavedEvent;
import com.wellsoft.pt.fulltext.facade.service.FulltextSettingFacadeService;
import com.wellsoft.pt.fulltext.service.FulltextFormDataDocumentIndexService;
import com.wellsoft.pt.fulltext.support.FulltextSetting;
import com.wellsoft.pt.jpa.event.EventListenerPair;
import com.wellsoft.pt.jpa.event.WellptTransactionalEventListener;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 6/17/25.1	    zhulh		6/17/25		    Create
 * </pre>
 * @date 6/17/25
 */
@Component
public class FulltextFormDataSavedListener extends WellptTransactionalEventListener<FormDataSavedEvent> {

    @Autowired
    private FulltextSettingFacadeService fulltextSettingFacadeService;

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired(required = false)
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    @Autowired
    private FulltextFormDataDocumentIndexService fulltextFormDataDocumentIndexService;

    @Override
    public boolean onAddEvent(List<EventListenerPair> eventListenerPairs, ApplicationEvent event) {
        FormDataSavedEvent event1 = (FormDataSavedEvent) event;
        for (EventListenerPair eventListenerPair : eventListenerPairs) {
            if (eventListenerPair.getEvent() instanceof FormDataSavedEvent) {
                FormDataSavedEvent event2 = (FormDataSavedEvent) eventListenerPair.getEvent();
                if (StringUtils.equals(event1.getDyFormData().getDataUuid(), event2.getDyFormData().getDataUuid())
                        && this.equals(eventListenerPair.getListener())) {
                    eventListenerPair.markIgnoreExecute();// 忽略重复的事件
                }
            }
        }
        return true;
    }

    @Override
    public void onApplicationEvent(FormDataSavedEvent event) {
        if (restHighLevelClient == null) {
            return;
        }

        DyFormData dyFormData = event.getDyFormData();
        String formUuid = dyFormData.getFormUuid();
        String system = event.getSystem();
        if (StringUtils.isBlank(system)) {
            DyFormFormDefinition dyFormFormDefinition = dyFormFacade.getFormDefinition(formUuid);
            system = dyFormFormDefinition.getSystem();
        }
        if (StringUtils.isBlank(system)) {
            return;
        }
        FulltextSetting fulltextSetting = fulltextSettingFacadeService.getSettingBySystem(system);
        if (BooleanUtils.isNotTrue(fulltextSetting.getEnabled()) || StringUtils.equals(fulltextSetting.getUpdateMode(), FulltextSetting.UPDATE_MODE_REGULAR)) {
            return;
        }

        String indexSystem = system;
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        scheduledExecutorService.execute(() -> {
            try {
                RequestSystemContextPathResolver.setSystem(indexSystem);
                IgnoreLoginUtils.login(userDetails);
                fulltextFormDataDocumentIndexService.index(event.getFormUuid(), event.getDataUuid(), dyFormData, fulltextSetting);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                IgnoreLoginUtils.logout();
                RequestSystemContextPathResolver.clear();
            }
        });
    }
}
