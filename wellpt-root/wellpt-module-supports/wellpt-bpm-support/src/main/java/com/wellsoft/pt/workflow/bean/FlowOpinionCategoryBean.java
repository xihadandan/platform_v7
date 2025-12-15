/*
 * @(#)2013-7-30 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.bean;

import com.wellsoft.pt.workflow.entity.FlowOpinionCategory;
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
 * 2013-7-30.1	zhulh		2013-7-30		Create
 * </pre>
 * @date 2013-7-30
 */
@ApiModel("流程意见分类")
public class FlowOpinionCategoryBean extends FlowOpinionCategory {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -7810505443628729935L;

    @ApiModelProperty("流程意见列表")
    private List<FlowOpinionBean> opinions;

    /**
     * @return the opinions
     */
    public List<FlowOpinionBean> getOpinions() {
        return opinions;
    }

    /**
     * @param opinions 要设置的opinions
     */
    public void setOpinions(List<FlowOpinionBean> opinions) {
        this.opinions = opinions;
    }

}
