/*
 * @(#)2016-07-04 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.work.facade.service;

import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.workflow.work.bean.TaskDelegationBean;

import java.util.Collection;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-07-04.1	zhulh		2016-07-04		Create
 * </pre>
 * @date 2016-07-04
 */
public interface WfTaskDelegationMgr extends BaseService {

    /**
     * 列表查询
     *
     * @param queryInfo
     * @return
     */
    QueryData query(QueryInfo queryInfo);

    /**
     * 获取
     *
     * @param uuid
     * @return
     */
    TaskDelegationBean getBean(String uuid);

    /**
     * 保存
     *
     * @param bean
     * @return
     */
    void saveBean(TaskDelegationBean bean);

    /**
     * 删除
     *
     * @param uuid
     * @return
     */
    void remove(String uuid);

    /**
     * 批量删除
     *
     * @param uuid
     * @return
     */
    void removeAll(Collection<String> uuids);

}
