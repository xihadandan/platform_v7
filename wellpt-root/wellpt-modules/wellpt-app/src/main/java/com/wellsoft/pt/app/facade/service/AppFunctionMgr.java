/*
 * @(#)2016-07-26 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.facade.service;

import com.wellsoft.context.component.select2.Select2QueryApi;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.app.bean.AppFunctionBean;

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
 * 2016-07-26.1	zhulh		2016-07-26		Create
 * </pre>
 * @date 2016-07-26
 */
public interface AppFunctionMgr extends BaseService, Select2QueryApi {

    /**
     * 获取
     *
     * @param uuid
     * @return
     */
    AppFunctionBean getBean(String uuid);

    /**
     * 保存
     *
     * @param bean
     * @return
     */
    void saveBean(AppFunctionBean bean);

    /**
     * 删除
     *
     * @param uuid
     * @return
     */
    void remove(String uuid);

    /**
     * 删除，并解除集成信息的数据
     *
     * @param dataUuid
     */
    void forceRemove(String uuid);

    /**
     * 批量删除
     *
     * @param uuid
     * @return
     */
    void removeAll(Collection<String> uuids);

    /**
     * @param select2QueryInfo
     * @return
     */
    Select2QueryData loadSelectDataForUuid(Select2QueryInfo select2QueryInfo);

    /**
     * @param select2QueryInfo
     * @return
     */
    Select2QueryData loadSelectDataByUuids(Select2QueryInfo select2QueryInfo);

}
