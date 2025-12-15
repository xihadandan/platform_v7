/*
 * @(#)Jan 16, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.facade.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.workflow.bean.FlowOpinionCategoryBean;

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
 * Jan 16, 2017.1	zhulh		Jan 16, 2017		Create
 * </pre>
 * @date Jan 16, 2017
 */
public interface WorkFlowOpinionService extends BaseService {

    /**
     * 获取所有的意见立场分类
     *
     * @return
     */
    List<FlowOpinionCategoryBean> getAllOpinionCategoryBeans();

}
