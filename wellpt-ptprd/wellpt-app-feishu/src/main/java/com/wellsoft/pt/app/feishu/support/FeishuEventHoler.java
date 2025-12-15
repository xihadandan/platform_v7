/*
 * @(#)3/28/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.feishu.support;

import com.google.common.collect.Maps;
import com.lark.oapi.event.model.Header;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.feishu.entity.FeishuConfigEntity;
import com.wellsoft.pt.app.feishu.entity.FeishuEventEntity;
import com.wellsoft.pt.app.feishu.service.FeishuConfigService;
import com.wellsoft.pt.app.feishu.service.FeishuEventService;
import com.wellsoft.pt.app.feishu.utils.FeishuEventUtils;
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
 * 3/28/25.1	    zhulh		3/28/25		    Create
 * </pre>
 * @date 3/28/25
 */
public class FeishuEventHoler {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeishuEventHoler.class);

    private static final ThreadLocal<FeishuEventEntity> feishuEventHoler = new NamedThreadLocal<>("FeishuEventHoler");

    public static void create(Header header, String eventData) {
        FeishuEventEntity feishuEventEntity = new FeishuEventEntity();
        feishuEventEntity.setAppId(header.getAppId());
        feishuEventEntity.setEventId(header.getEventId());
        feishuEventEntity.setEventType(header.getEventType());
        feishuEventEntity.setEventTypeName(FeishuEventUtils.getEventTypeName(header.getEventType()));
        if (StringUtils.isBlank(feishuEventEntity.getEventTypeName())) {
            feishuEventEntity.setEventTypeName(header.getEventType());
        }
        feishuEventEntity.setHandleStatus(FeishuEventEntity.HandleStatus.WAIT);
        feishuEventEntity.setEventData(eventData);
        feishuEventHoler.set(feishuEventEntity);
    }

    public static void success(String successMsg) {
        FeishuEventEntity feishuEventEntity = feishuEventHoler.get();
        feishuEventEntity.setHandleStatus(FeishuEventEntity.HandleStatus.SUCCESS);
        feishuEventEntity.setHandleResult(successMsg);
    }

    public static void success(String key, Object value) {
        Map<String, Object> data = Maps.newHashMap();
        data.put(key, value);
        FeishuEventEntity feishuEventEntity = feishuEventHoler.get();
        feishuEventEntity.setHandleStatus(FeishuEventEntity.HandleStatus.SUCCESS);
        feishuEventEntity.setHandleResult(JsonUtils.object2Json(data));
    }

    public static void error(String errorMsg) {
        FeishuEventEntity feishuEventEntity = feishuEventHoler.get();
        feishuEventEntity.setHandleStatus(FeishuEventEntity.HandleStatus.FAILED);
        feishuEventEntity.setHandleResult(errorMsg);
    }

    public static void commit() {
        try {
            FeishuEventEntity feishuEventEntity = feishuEventHoler.get();
            if (!FeishuEventEntity.HandleStatus.FAILED.equals(feishuEventEntity.getHandleStatus())) {
                feishuEventEntity.setHandleStatus(FeishuEventEntity.HandleStatus.SUCCESS);
            }
            FeishuEventService feishuEventService = ApplicationContextHolder.getBean(FeishuEventService.class);
            FeishuConfigService feishuConfigService = ApplicationContextHolder.getBean(FeishuConfigService.class);
            FeishuConfigEntity feishuConfigEntity = feishuConfigService.getByAppId(feishuEventEntity.getAppId());
            if (feishuConfigEntity != null) {
                feishuEventEntity.setSystem(feishuConfigEntity.getSystem());
                feishuEventEntity.setTenant(feishuConfigEntity.getTenant());
            }
            feishuEventService.save(feishuEventEntity);
        } catch (Exception e) {
            LOGGER.error("FeishuEventHoler commit error", e);
        }
        feishuEventHoler.remove();
    }

}
