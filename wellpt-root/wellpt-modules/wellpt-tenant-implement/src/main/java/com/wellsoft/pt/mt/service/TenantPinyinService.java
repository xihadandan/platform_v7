/*
 * @(#)2014-1-2 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.mt.dao.TenantPinyinDao;
import com.wellsoft.pt.mt.entity.TenantPinyin;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-1-2.1	zhulh		2014-1-2		Create
 * </pre>
 * @date 2014-1-2
 */
public interface TenantPinyinService extends JpaService<TenantPinyin, TenantPinyinDao, String> {

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    void deleteByEntityUuid(String entityUuid);
}
