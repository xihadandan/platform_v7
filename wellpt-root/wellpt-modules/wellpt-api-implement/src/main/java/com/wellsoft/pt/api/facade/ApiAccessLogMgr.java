/*
 * @(#)2015-09-23 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.api.bean.ApiAccessLogBean;

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
 * 2015-09-23.1	zhulh		2015-09-23		Create
 * </pre>
 * @date 2015-09-23
 */
public interface ApiAccessLogMgr extends BaseService {

    /**
     * 获取
     *
     * @param uuid
     * @return
     */
    ApiAccessLogBean getBean(String uuid);

    /**
     * 保存
     *
     * @param bean
     * @return
     */
    void saveBean(ApiAccessLogBean bean);

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
