/*
 * @(#)Feb 6, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support.event;

import com.wellsoft.context.event.WellptEvent;
import com.wellsoft.pt.workflow.work.bean.WorkBean;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Feb 6, 2017.1	zhulh		Feb 6, 2017		Create
 * </pre>
 * @date Feb 6, 2017
 */
public class WorkFlowOpinionSignEvent extends WellptEvent {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -7045560061639705313L;

    private WorkBean workBean;

    /**
     * @param source
     */
    public WorkFlowOpinionSignEvent(WorkBean workBean) {
        super(workBean);
        this.workBean = workBean;
    }

    /**
     * @return the workBean
     */
    public WorkBean getWorkBean() {
        return workBean;
    }

}
