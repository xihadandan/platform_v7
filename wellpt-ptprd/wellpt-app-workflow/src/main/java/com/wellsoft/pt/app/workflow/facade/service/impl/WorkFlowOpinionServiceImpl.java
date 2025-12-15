/*
 * @(#)2017-01-16 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.facade.service.impl;

import com.wellsoft.pt.app.workflow.facade.service.WorkFlowOpinionService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.workflow.bean.FlowOpinionCategoryBean;
import com.wellsoft.pt.workflow.service.FlowOpinionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description: 流程办理意见对外服务类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-01-16.1	zhulh		2017-01-16		Create
 * </pre>
 * @date 2017-01-16
 */
@Service
@Transactional(readOnly = true)
public class WorkFlowOpinionServiceImpl extends BaseServiceImpl implements WorkFlowOpinionService {

    @Autowired
    private FlowOpinionService flowOpinionService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.app.workflow.facade.service.WorkFlowOpinionService#getAllOpinionCategoryBeans()
     */
    @Override
    public List<FlowOpinionCategoryBean> getAllOpinionCategoryBeans() {
        return flowOpinionService.getAllOpinionCategoryBeans();
    }

}
