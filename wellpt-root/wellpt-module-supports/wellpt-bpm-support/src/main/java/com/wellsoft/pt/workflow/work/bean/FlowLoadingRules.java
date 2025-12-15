/*
 * @(#)2020年12月8日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.work.bean;

import com.google.common.collect.Lists;
import com.wellsoft.context.base.BaseObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

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
 * 2020年12月8日.1	zhulh		2020年12月8日		Create
 * </pre>
 * @date 2020年12月8日
 */
@ApiModel("流程加载规则")
public class FlowLoadingRules extends BaseObject {

    // 加载模式，1待办视图列表、2连续签批模式
    public static final int MODE_LIST_VIEW = 1;
    public static final int MODE_BATCH_APPROVE = 2;
    // 加载规则，1、按列表顺序加载、2按办理时限加载、3智能加载
    public static final int RULE_LIST_VIEW = 1;
    public static final int RULE_LIMIT_TIME = 2;
    public static final int RULE_INTELLIGENT = 3;
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1316841705907640133L;
    // 加载模式
    @ApiModelProperty("加载模式，1待办视图列表、2连续签批模式，默认值1")
    private int mode = 1;

    // 加载规则
    @ApiModelProperty("加载规则，1、按列表顺序加载、2按办理时限加载、3智能加载，默认值1")
    private int rule = 1;

    // 流程分类UUID列表
    @ApiModelProperty("流程分类UUID列表")
    private List<String> flowCategoryUuids = Lists.newArrayList();

    // 流程定义ID列表
    @ApiModelProperty("流程定义ID列表")
    private List<String> flowDefIds = Lists.newArrayList();

    // 今日到达
    @ApiModelProperty("今日到达，默认false")
    private boolean arriveToday;

    /**
     * @return the mode
     */
    public int getMode() {
        return mode;
    }

    /**
     * @param mode 要设置的mode
     */
    public void setMode(int mode) {
        this.mode = mode;
    }

    /**
     * @return the rule
     */
    public int getRule() {
        return rule;
    }

    /**
     * @param rule 要设置的rule
     */
    public void setRule(int rule) {
        this.rule = rule;
    }

    /**
     * @return the flowCategoryUuids
     */
    public List<String> getFlowCategoryUuids() {
        return flowCategoryUuids;
    }

    /**
     * @param flowCategoryUuids 要设置的flowCategoryUuids
     */
    public void setFlowCategoryUuids(List<String> flowCategoryUuids) {
        this.flowCategoryUuids = flowCategoryUuids;
    }

    /**
     * @return the flowDefIds
     */
    public List<String> getFlowDefIds() {
        return flowDefIds;
    }

    /**
     * @param flowDefIds 要设置的flowDefIds
     */
    public void setFlowDefIds(List<String> flowDefIds) {
        this.flowDefIds = flowDefIds;
    }

    /**
     * @return the arriveToday
     */
    public boolean isArriveToday() {
        return arriveToday;
    }

    /**
     * @param arriveToday 要设置的arriveToday
     */
    public void setArriveToday(boolean arriveToday) {
        this.arriveToday = arriveToday;
    }

}
