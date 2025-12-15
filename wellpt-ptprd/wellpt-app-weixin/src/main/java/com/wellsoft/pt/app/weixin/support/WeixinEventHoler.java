/*
 * @(#)5/23/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.weixin.support;

import com.google.common.collect.Maps;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.weixin.entity.WeixinEventEntity;
import com.wellsoft.pt.app.weixin.service.WeixinEventService;
import com.wellsoft.pt.app.weixin.utils.WeixinEventUtils;
import com.wellsoft.pt.app.weixin.vo.WeixinConfigVo;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
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
 * 5/23/25.1	    zhulh		5/23/25		    Create
 * </pre>
 * @date 5/23/25
 */
public class WeixinEventHoler {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeixinEventHoler.class);

    private static final ThreadLocal<WeixinEventEntity> weixinEventHoler = new NamedThreadLocal<>("WeixinEventHoler");

    public static void create(JSONObject jsonObject, WeixinConfigVo weixinConfigVo) {
        String eventType = jsonObject.getString("ChangeType");

        WeixinEventEntity weixinEventEntity = new WeixinEventEntity();
        weixinEventEntity.setCorpId(weixinConfigVo.getCorpId());
        weixinEventEntity.setEventType(eventType);
        weixinEventEntity.setEventTypeName(WeixinEventUtils.getEventTypeName(eventType));
        if (StringUtils.isBlank(weixinEventEntity.getEventTypeName())) {
            weixinEventEntity.setEventTypeName(eventType);
        }
        weixinEventEntity.setHandleStatus(WeixinEventEntity.HandleStatus.WAIT);
        weixinEventEntity.setEventData(jsonObject.toString());
        weixinEventEntity.setSystem(weixinConfigVo.getSystem());
        weixinEventEntity.setTenant(weixinConfigVo.getTenant());
        weixinEventHoler.set(weixinEventEntity);
    }

    public static void error(String errorMsg) {
        WeixinEventEntity weixinEventEntity = weixinEventHoler.get();
        weixinEventEntity.setHandleStatus(WeixinEventEntity.HandleStatus.FAILED);
        weixinEventEntity.setHandleResult(errorMsg);
    }

    public static void success(String key, Object value) {
        Map<String, Object> data = Maps.newHashMap();
        data.put(key, value);
        WeixinEventEntity weixinEventEntity = weixinEventHoler.get();
        weixinEventEntity.setHandleStatus(WeixinEventEntity.HandleStatus.SUCCESS);
        weixinEventEntity.setHandleResult(JsonUtils.object2Json(data));
    }

    public static void commit() {
        try {
            WeixinEventEntity weixinEventEntity = weixinEventHoler.get();
            if (!WeixinEventEntity.HandleStatus.FAILED.equals(weixinEventEntity.getHandleStatus())) {
                weixinEventEntity.setHandleStatus(WeixinEventEntity.HandleStatus.SUCCESS);
            }
            WeixinEventService dingtalkEventService = ApplicationContextHolder.getBean(WeixinEventService.class);
            dingtalkEventService.save(weixinEventEntity);
        } catch (Exception e) {
            LOGGER.error("WeixinEventHoler commit error", e);
        }
        weixinEventHoler.remove();
    }

}
