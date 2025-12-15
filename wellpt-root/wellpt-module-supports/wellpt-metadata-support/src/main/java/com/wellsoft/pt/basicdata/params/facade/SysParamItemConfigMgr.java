/*
 * @(#)2015-07-20 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.params.facade;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.basicdata.params.bean.SysParamItemBean;
import com.wellsoft.pt.basicdata.params.entity.SysParamItem;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author Lmw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-07-20.1	Lmw		2015-07-20		Create
 * </pre>
 * @date 2015-07-20
 */
public interface SysParamItemConfigMgr extends BaseService {

    /**
     * 获取
     *
     * @param uuid
     * @return
     */
    SysParamItemBean getBean(String uuid);

    /**
     * 保存
     *
     * @param bean
     * @return
     */
    void saveBean(SysParamItemBean bean);

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

    /**
     * @param key
     * @return
     */
    List<SysParamItem> listByKey(String key);

    String getValueByKey(String key);

    boolean saveModuleConfig(String module, Map<String, String> params);

    Map<String, String> getModuleConfig(String module);

    public abstract List<SysParamItem> query(String keyword, PagingInfo pagingInfo);

}
