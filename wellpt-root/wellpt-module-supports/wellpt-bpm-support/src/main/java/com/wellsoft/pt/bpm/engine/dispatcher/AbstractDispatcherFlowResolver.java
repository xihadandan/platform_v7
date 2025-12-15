/*
 * @(#)2014-2-25 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.dispatcher;

import com.google.common.collect.Maps;
import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.expression.Condition;
import com.wellsoft.pt.bpm.engine.expression.ConditionFactory;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import ognl.Ognl;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * 2014-2-25.1	zhulh		2014-2-25		Create
 * </pre>
 * @date 2014-2-25
 */
public abstract class AbstractDispatcherFlowResolver implements DispatcherFlowResolver {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 拷贝任务数据
     *
     * @param taskData
     * @return
     */
    protected TaskData copyTaskData(TaskData taskData) {
        Map<String, DyFormData> dyformDataMap = new HashMap<String, DyFormData>();
        for (Object key : taskData.keySet()) {
            Object object = taskData.get(key);
            if (object instanceof DyFormData) {
                dyformDataMap.put(key.toString(), (DyFormData) object);
            }
        }
        Token token = taskData.getToken();

        for (String dyformDataKey : dyformDataMap.keySet()) {
            taskData.remove(dyformDataKey);
        }
        taskData.remove("token");
        Map<String, DyFormData> updatedDyformDataMap = taskData.getUpdatedDyformDataMap();
        Map<String, DyFormData> copyUpdatedDyformDataMap = Maps.newHashMap();
        copyUpdatedDyformDataMap.putAll(updatedDyformDataMap);
        taskData.setUpdatedDyformDataMap(null);

        TaskData data = (TaskData) SerializationUtils.clone(taskData);

        // 清空流水号
        data.setTaskSerialNo(StringUtils.EMPTY);
        data.setLogUserOperation(false);

        taskData.setUpdatedDyformDataMap(copyUpdatedDyformDataMap);
        data.setUpdatedDyformDataMap(copyUpdatedDyformDataMap);
        for (String dyformDataKey : dyformDataMap.keySet()) {
            taskData.setDyFormData(dyformDataKey, dyformDataMap.get(dyformDataKey));
            data.setDyFormData(dyformDataKey, dyformDataMap.get(dyformDataKey));
        }
        taskData.setUpdatedDyformDataMap(updatedDyformDataMap);
        data.setUpdatedDyformDataMap(updatedDyformDataMap);
        
        taskData.setToken(token);
        data.setToken(token);
        return data;
    }

    /**
     * 判断创建流程的条件
     *
     * @param executionContext
     * @param conditionValue
     */
    protected boolean evaluate(ExecutionContext executionContext, Object conditionValue) {
        Condition condition = ConditionFactory.getCondition(conditionValue.toString());
        Object result = 0;
        try {
            result = Ognl
                    .getValue(condition.evaluate(executionContext.getToken(), null), new HashMap<String, String>());
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return (result instanceof Boolean) ? (Boolean) result : Integer.valueOf(1).equals(result);
    }
}
