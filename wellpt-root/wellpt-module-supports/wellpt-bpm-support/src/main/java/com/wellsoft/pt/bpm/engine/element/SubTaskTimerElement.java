/*
 * @(#)2020年9月23日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.element;

import com.wellsoft.context.base.BaseObject;
import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年9月23日.1	zhulh		2020年9月23日		Create
 * </pre>
 * @date 2020年9月23日
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubTaskTimerElement extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -1867238471151520678L;

    // 子流程环节ID
    private String taskId;

    // 计时方式
    private String timingMode;

    // 子流程计时器列表
    private List<NewFlowTimerElement> timers;

    /**
     * @return the taskId
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * @param taskId 要设置的taskId
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * @return the timingMode
     */
    public String getTimingMode() {
        return timingMode;
    }

    /**
     * @param timingMode 要设置的timingMode
     */
    public void setTimingMode(String timingMode) {
        this.timingMode = timingMode;
    }

    /**
     * @return the timers
     */
    public List<NewFlowTimerElement> getTimers() {
        return timers;
    }

    /**
     * @param timers 要设置的timers
     */
    public void setTimers(List<NewFlowTimerElement> timers) {
        this.timers = timers;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean isUseNewFlowTimer() {
        return "2".equals(timingMode) && CollectionUtils.isNotEmpty(timers);
    }

}
