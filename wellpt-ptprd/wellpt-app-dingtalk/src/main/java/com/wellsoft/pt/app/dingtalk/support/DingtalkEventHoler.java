/*
 * @(#)4/23/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.support;

import com.dingtalk.open.app.api.message.GenericOpenDingTalkEvent;
import com.google.common.collect.Maps;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.dingtalk.entity.DingtalkConfigEntity;
import com.wellsoft.pt.app.dingtalk.entity.DingtalkEventEntity;
import com.wellsoft.pt.app.dingtalk.service.DingtalkConfigService;
import com.wellsoft.pt.app.dingtalk.service.DingtalkEventService;
import com.wellsoft.pt.app.dingtalk.utils.DingtalkEventUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;

import java.util.Map;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 4/23/25.1	    zhulh		4/23/25		    Create
 * </pre>
 * @date 4/23/25
 */
public class DingtalkEventHoler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DingtalkEventHoler.class);

    private static final ThreadLocal<DingtalkEventEntity> dingtalkEventHoler = new NamedThreadLocal<>("DingtalkEventHoler");

    public static void create(GenericOpenDingTalkEvent event) {
        DingtalkEventEntity dingtalkEventEntity = new DingtalkEventEntity();
        dingtalkEventEntity.setAppId(event.getEventUnifiedAppId());
        dingtalkEventEntity.setEventId(event.getEventId());
        dingtalkEventEntity.setEventType(event.getEventType());
        dingtalkEventEntity.setEventTypeName(DingtalkEventUtils.getEventTypeName(event.getEventType()));
        if (StringUtils.isBlank(dingtalkEventEntity.getEventTypeName())) {
            dingtalkEventEntity.setEventTypeName(event.getEventType());
        }
        dingtalkEventEntity.setHandleStatus(DingtalkEventEntity.HandleStatus.WAIT);
        dingtalkEventEntity.setEventData(event.getData().toString());
        dingtalkEventHoler.set(dingtalkEventEntity);
    }

    public static void success(String successMsg) {
        DingtalkEventEntity dingtalkEventEntity = dingtalkEventHoler.get();
        dingtalkEventEntity.setHandleStatus(DingtalkEventEntity.HandleStatus.SUCCESS);
        dingtalkEventEntity.setHandleResult(successMsg);
    }

    public static void success(String key, Object value) {
        Map<String, Object> data = Maps.newHashMap();
        data.put(key, value);
        DingtalkEventEntity dingtalkEventEntity = dingtalkEventHoler.get();
        dingtalkEventEntity.setHandleStatus(DingtalkEventEntity.HandleStatus.SUCCESS);
        dingtalkEventEntity.setHandleResult(JsonUtils.object2Json(data));
    }

    public static void error(String errorMsg) {
        DingtalkEventEntity dingtalkEventEntity = dingtalkEventHoler.get();
        dingtalkEventEntity.setHandleStatus(DingtalkEventEntity.HandleStatus.FAILED);
        dingtalkEventEntity.setHandleResult(errorMsg);
    }

    public static void commit() {
        try {
            DingtalkEventEntity dingtalkEventEntity = dingtalkEventHoler.get();
            if (!DingtalkEventEntity.HandleStatus.FAILED.equals(dingtalkEventEntity.getHandleStatus())) {
                dingtalkEventEntity.setHandleStatus(DingtalkEventEntity.HandleStatus.SUCCESS);
            }
            DingtalkEventService dingtalkEventService = ApplicationContextHolder.getBean(DingtalkEventService.class);
            DingtalkConfigService dingtalkConfigService = ApplicationContextHolder.getBean(DingtalkConfigService.class);
            DingtalkConfigEntity dingtalkConfigEntity = dingtalkConfigService.getByAppId(dingtalkEventEntity.getAppId());
            if (dingtalkConfigEntity != null) {
                dingtalkEventEntity.setSystem(dingtalkConfigEntity.getSystem());
                dingtalkEventEntity.setTenant(dingtalkConfigEntity.getTenant());
            }
            dingtalkEventService.save(dingtalkEventEntity);
        } catch (Exception e) {
            LOGGER.error("DingtalkEventHoler commit error", e);
        }
        dingtalkEventHoler.remove();
    }

}
