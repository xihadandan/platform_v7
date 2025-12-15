/*
 * @(#)2017-12-19 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.facade.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.dms.bean.DmsRoleBean;

import java.util.Collection;
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
 * 2017-12-19.1	zhulh		2017-12-19		Create
 * </pre>
 * @date 2017-12-19
 */
public interface DmsRoleMgr extends BaseService {

    /**
     * 获取
     *
     * @param uuid
     * @return
     */
    DmsRoleBean getBean(String uuid);

    /**
     * 获取
     *
     * @param uuid
     * @return
     */
    List<DmsRoleBean> getAll();

    /**
     * 保存
     *
     * @param bean
     * @return
     */
    String saveBean(DmsRoleBean bean);

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
