/*
 * @(#)2022-03-22 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.handover.service;

import com.wellsoft.pt.handover.dao.WhWorkSettingsDao;
import com.wellsoft.pt.handover.entity.WhWorkSettingsEntity;
import com.wellsoft.pt.jpa.service.JpaService;

/**
 * Description: 数据库表WH_WORK_SETTINGS的service服务接口
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2022-03-22.1	zenghw		2022-03-22		Create
 * </pre>
 * @date 2022-03-22
 */
public interface WhWorkSettingsService extends JpaService<WhWorkSettingsEntity, WhWorkSettingsDao, String> {

    /**
     * 通过当前单位获取默认执行时间
     *
     * @param systemUnitId
     * @return 取不到返回Null
     **/
    public WhWorkSettingsEntity getDetailByCurrentUnitId(String systemUnitId);
}
