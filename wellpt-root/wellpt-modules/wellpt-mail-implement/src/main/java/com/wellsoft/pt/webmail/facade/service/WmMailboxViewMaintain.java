/*
 * @(#)2016-06-03 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.facade.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.webmail.bean.WmMailboxBean;

import java.util.Collection;

/**
 * Description: 邮箱视图维护
 *
 * @author t
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-06-03.1	t		2016-06-03		Create
 * </pre>
 * @date 2016-06-03
 */
public interface WmMailboxViewMaintain extends BaseService {

    /**
     * 获取
     *
     * @param uuid
     * @return
     */
    WmMailboxBean getBean(String uuid);

    /**
     * 保存
     *
     * @param bean
     * @return
     */
    void saveBean(WmMailboxBean bean);

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
