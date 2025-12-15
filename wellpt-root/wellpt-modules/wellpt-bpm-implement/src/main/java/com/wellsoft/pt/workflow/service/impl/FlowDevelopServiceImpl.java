/*
 * @(#)2014-10-24 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service.impl;

import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.workflow.bean.FlowDevelopBean;
import com.wellsoft.pt.workflow.service.FlowDefineService;
import com.wellsoft.pt.workflow.service.FlowDevelopService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-24.1	zhulh		2014-10-24		Create
 * </pre>
 * @date 2014-10-24
 */
@Service
public class FlowDevelopServiceImpl extends BaseServiceImpl implements FlowDevelopService {

    private static final String KEY_CUSTOM_JS_URL = "customJsUrl";
    private static final String KEY_CUSTOM_JS_MODULE = "customJsModule";
    @Autowired
    private FlowDefineService flowDefineService;

    /**
     * 如何描述该方法
     *
     * @param object
     * @return
     */
    private static String objectToString(Object object) {
        return object == null ? "" : object.toString();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowDevelopService#getBean(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public FlowDevelopBean getBean(String flowDefUuid) {
        FlowDefinition flowDefinition = flowDefineService.get(flowDefUuid);
        FlowDevelopBean bean = new FlowDevelopBean();
        BeanUtils.copyProperties(flowDefinition, bean);

        // 获取自定义信息
        String developJson = bean.getDevelopJson();
        if (StringUtils.isNotBlank(developJson)) {
            try {
                JSONObject jsonObject = new JSONObject(developJson);
                String customJsUrl = objectToString(jsonObject.has(KEY_CUSTOM_JS_URL) ? jsonObject
                        .get(KEY_CUSTOM_JS_URL) : null);
                String customJsModule = objectToString(jsonObject.has(KEY_CUSTOM_JS_MODULE) ? jsonObject
                        .get(KEY_CUSTOM_JS_MODULE) : null);
                bean.setCustomJsUrl(customJsUrl);
                bean.setCustomJsModule(customJsModule);
            } catch (JSONException e) {
                logger.error(ExceptionUtils.getStackTrace(e));
            }
        }
        return bean;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowDevelopService#saveBean(com.wellsoft.pt.workflow.bean.FlowDevelopBean)
     */
    @Override
    @Transactional
    public void saveBean(FlowDevelopBean flowDevelopBean) {
        String customJsUrl = flowDevelopBean.getCustomJsUrl();
        String customJsModule = flowDevelopBean.getCustomJsModule();
        String flowDefUuid = flowDevelopBean.getUuid();
        Map<String, String> jsonMap = new HashMap<String, String>();
        jsonMap.put(KEY_CUSTOM_JS_URL, customJsUrl);
        jsonMap.put(KEY_CUSTOM_JS_MODULE, customJsModule);
        FlowDefinition flowDefinition = flowDefineService.get(flowDefUuid);
        flowDefinition.setDevelopJson(JsonUtils.object2Json(jsonMap));
        flowDefineService.save(flowDefinition);
    }
}
