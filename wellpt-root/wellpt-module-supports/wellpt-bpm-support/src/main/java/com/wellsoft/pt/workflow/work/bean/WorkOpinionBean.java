/*
 * @(#)2013-4-21 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.work.bean;

import com.wellsoft.pt.workflow.bean.FlowOpinionCategoryBean;
import com.wellsoft.pt.workflow.entity.FlowOpinion;
import com.wellsoft.pt.workflow.entity.FlowOpinionCategory;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 流程意见立场
 *
 * @author rzhu
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-21.1	rzhu		2013-4-21		Create
 * </pre>
 * @date 2013-4-21
 */
public class WorkOpinionBean implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -7553557856919088889L;

    // 意见立场列表
    private List<FlowOpinion> opinions;

    // 最近使用
    private List<FlowOpinion> recents;

    // 用户意见
    private List<FlowOpinionCategoryBean> userOpinionCategories;

    // 公共意见
    private FlowOpinionCategory publicOpinionCategory;

    // 启用待办意见立场
    private boolean enableOpinionPosition;

    // 意见立场必填
    private boolean requiredOpinionPosition;

    /**
     * @return the opinions
     */
    public List<FlowOpinion> getOpinions() {
        return opinions;
    }

    /**
     * @param opinions 要设置的opinions
     */
    public void setOpinions(List<FlowOpinion> opinions) {
        this.opinions = opinions;
    }

    /**
     * @return the recents
     */
    public List<FlowOpinion> getRecents() {
        return recents;
    }

    /**
     * @param recents 要设置的recents
     */
    public void setRecents(List<FlowOpinion> recents) {
        this.recents = recents;
    }

    /**
     * @return the userOpinionCategories
     */
    public List<FlowOpinionCategoryBean> getUserOpinionCategories() {
        return userOpinionCategories;
    }

    /**
     * @param userOpinionCategories 要设置的userOpinionCategories
     */
    public void setUserOpinionCategories(List<FlowOpinionCategoryBean> userOpinionCategories) {
        this.userOpinionCategories = userOpinionCategories;
    }

    /**
     * @return the publicOpinionCategory
     */
    public FlowOpinionCategory getPublicOpinionCategory() {
        return publicOpinionCategory;
    }

    /**
     * @param publicOpinionCategory 要设置的publicOpinionCategory
     */
    public void setPublicOpinionCategory(FlowOpinionCategory publicOpinionCategory) {
        this.publicOpinionCategory = publicOpinionCategory;
    }

    /**
     * @return the enableOpinionPosition
     */
    public boolean isEnableOpinionPosition() {
        return enableOpinionPosition;
    }

    /**
     * @param enableOpinionPosition 要设置的enableOpinionPosition
     */
    public void setEnableOpinionPosition(boolean enableOpinionPosition) {
        this.enableOpinionPosition = enableOpinionPosition;
    }

    /**
     * @return the requiredOpinionPosition
     */
    public boolean isRequiredOpinionPosition() {
        return requiredOpinionPosition;
    }

    /**
     * @param requiredOpinionPosition 要设置的requiredOpinionPosition
     */
    public void setRequiredOpinionPosition(boolean requiredOpinionPosition) {
        this.requiredOpinionPosition = requiredOpinionPosition;
    }

}
