/*
 * @(#)2019年10月18日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.work.bean;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.base.BaseObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

/**
 * Description: 流程办理状态信息
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年10月18日.1	zhulh		2019年10月18日		Create
 * </pre>
 * @date 2019年10月18日
 */
@ApiModel("流程办理状态信息")
public class FlowHandingStateInfoDto extends BaseObject {

    // 流程已启动常量
    public static final String STARTED_KEY = "started";
    // 流程已完成常量
    public static final String COMPLETED_KEY = "completed";
    // 环节状态常量
    public static final String PRE_TASK_IDS_KEY = "preTaskIds";
    public static final String PRE_GATEWAY_IDS_KEY = "preGatewayIds";
    public static final String STATE_KEY = "state";
    public static final String STATE_TODO = "todo";
    public static final String STATE_DONE = "done";
    public static final String STATE_UNDO = "undo";
    public static final String IS_TIMMING_KEY = "isTimming";
    public static final String IS_ALARM_KEY = "isAlarm";
    public static final String IS_OVER_DUE_KEY = "isOverDue";
    // 开始ID常量
    public static final String FROM_ID_KEY = "fromId";
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -6129768119586686762L;
    // 开始结点状态
    @ApiModelProperty("开始结点状态")
    private Map<String/* started */, Boolean/* true/false */> start = Maps.newHashMap();

    // 环节结点状态
    @ApiModelProperty("环节结点状态")
    private Map<String/* taskId */, Map<String/* state */, Object/* state:todo/done/undo */>> tasks = Maps
            .newHashMap();

    // 子流程结点状态
    @ApiModelProperty("子流程结点状态")
    private Map<String/* taskId */, Map<String/* state */, Object/* state:todo/done/undo */>> subflows = Maps
            .newHashMap();

    // 结束结点
    @ApiModelProperty("结束结点")
    private List<Map<String/* fromId/completed */, Object>> ends = Lists.newArrayList();

    /**
     * 获取开始结点状态
     *
     * @return the start
     */
    public Map<String, Boolean> getStart() {
        return start;
    }

    /**
     * 设置开始结点状态
     *
     * @param start 要设置的start
     */
    public void setStart(Map<String, Boolean> start) {
        this.start = start;
    }

    /**
     * 获取环节结点状态
     *
     * @return the tasks
     */
    public Map<String, Map<String, Object>> getTasks() {
        return tasks;
    }

    /**
     * 设置环节结点状态
     *
     * @param tasks 要设置的tasks
     */
    public void setTasks(Map<String, Map<String, Object>> tasks) {
        this.tasks = tasks;
    }

    /**
     * 获取子流程结点状态
     *
     * @return the subflows
     */
    public Map<String, Map<String, Object>> getSubflows() {
        return subflows;
    }

    /**
     * 设置子流程结点状态
     *
     * @param subflows 要设置的subflows
     */
    public void setSubflows(Map<String, Map<String, Object>> subflows) {
        this.subflows = subflows;
    }

    /**
     * 获取结束结点
     *
     * @return the ends
     */
    public List<Map<String, Object>> getEnds() {
        return ends;
    }

    /**
     * 设置结束结点
     *
     * @param ends 要设置的ends
     */
    public void setEnds(List<Map<String, Object>> ends) {
        this.ends = ends;
    }

}
