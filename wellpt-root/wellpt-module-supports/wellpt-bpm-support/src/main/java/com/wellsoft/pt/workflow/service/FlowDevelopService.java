/*
 * @(#)2014-10-24 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.workflow.bean.FlowDevelopBean;

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
public interface FlowDevelopService extends BaseService {

    FlowDevelopBean getBean(String flowDefUuid);

    void saveBean(FlowDevelopBean flowDevelopBean);

}
