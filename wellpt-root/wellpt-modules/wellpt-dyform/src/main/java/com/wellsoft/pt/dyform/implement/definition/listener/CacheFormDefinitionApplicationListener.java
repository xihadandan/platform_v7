/*
 * @(#)2021年12月15日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.definition.listener;

import com.wellsoft.context.profile.OnNotDevProfileCondition;
import com.wellsoft.pt.dyform.implement.definition.cache.DyformCacheUtils;
import com.wellsoft.pt.dyform.implement.definition.service.FormDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description: 应用启动后缓存最新更改的表单定义
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年12月15日.1	zhulh		2021年12月15日		Create
 * </pre>
 * @date 2021年12月15日
 */
@Component
@Conditional(OnNotDevProfileCondition.class)
public class CacheFormDefinitionApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private FormDefinitionService formDefinitionService;

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 应用启动后缓存最新更改的100个表单定义
        List<String> formUuids = formDefinitionService.getLatestUpdatedUuids(100);
        for (String formUuid : formUuids) {
            DyformCacheUtils.getDyformDefinitionByUuid(formUuid);
        }
    }

}
