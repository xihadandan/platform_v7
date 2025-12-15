/*
 * @(#)2016-07-24 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.facade.service;

import com.wellsoft.context.component.select2.Select2QueryApi;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.app.bean.AppProductBean;

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
 * 2016-07-24.1	zhulh		2016-07-24		Create
 * </pre>
 * @date 2016-07-24
 */
public interface AppProductMgr extends BaseService, Select2QueryApi {

    /**
     * 获取
     *
     * @param uuid
     * @return
     */
    AppProductBean getBean(String uuid);

    /**
     * 保存
     *
     * @param bean
     * @return
     */
    void saveBean(AppProductBean bean);

    /**
     * 更新产品，自动更新乐观锁的版本号
     *
     * @param uuid
     */
    void update(String uuid);

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
