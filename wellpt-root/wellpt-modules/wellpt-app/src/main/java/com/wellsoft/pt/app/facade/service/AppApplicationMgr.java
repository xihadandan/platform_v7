/*
 * @(#)2016-05-09 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.facade.service;

import com.wellsoft.context.component.select2.Select2QueryApi;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.app.bean.AppApplicationBean;
import com.wellsoft.pt.app.entity.AppApplication;

import java.util.Collection;

/**
 * Description: 如何描述该类
 *
 * @author t
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-05-09.1	t		2016-05-09		Create
 * </pre>
 * @date 2016-05-09
 */
public interface AppApplicationMgr extends BaseService, Select2QueryApi {

    /**
     * 获取
     *
     * @param uuid
     * @return
     */
    AppApplicationBean getBean(String uuid);

    /**
     * 保存
     *
     * @param bean
     * @return
     */
    AppApplication saveBean(AppApplicationBean bean);

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
