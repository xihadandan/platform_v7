/*
 * @(#)2021-07-22 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service;


import com.wellsoft.pt.dms.dao.DmsDocExchangeFieldVerDao;
import com.wellsoft.pt.dms.entity.DmsDocExchangeFieldVerEntity;
import com.wellsoft.pt.jpa.service.JpaService;

/**
 * Description: 数据库表DMS_DOC_EXCHANGE_FIELD_VER的service服务接口
 *
 * @author leo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-07-22.1	leo		2021-07-22		Create
 * </pre>
 * @date 2021-07-22
 */
public interface DmsDocExchangeFieldVerService extends JpaService<DmsDocExchangeFieldVerEntity, DmsDocExchangeFieldVerDao, String> {

    /**
     * 记录已读
     *
     * @param userId
     * @param version
     */
    public void recordRead(String userId, String version);

    /**
     * 是否已读
     *
     * @param userId
     * @param version
     * @return
     */
    public boolean isRead(String userId, String version);
}
