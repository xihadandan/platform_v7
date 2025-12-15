/*
 * @(#)2015-10-10 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.facade.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.org.bean.OrganizationBean;

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
 * 2015-10-10.1	zhulh		2015-10-10		Create
 * </pre>
 * @date 2015-10-10
 */
public interface OrganizationMgr extends BaseService {

    /**
     * 获取
     *
     * @param uuid
     * @return
     */
    OrganizationBean getBean(String uuid);

    /**
     * 保存
     *
     * @param bean
     * @return
     */
    void saveBean(OrganizationBean bean);

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
