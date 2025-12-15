/*
 * @(#)2021-07-13 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service;


import com.wellsoft.pt.dms.dao.DmsDocExchangeExpireDao;
import com.wellsoft.pt.dms.entity.DmsDocExchangeExpireEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.Date;

/**
 * Description: 数据库表DMS_DOC_EXCHANGE_EXPIRE的service服务接口
 *
 * @author yt
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-07-13.1	yt		2021-07-13		Create
 * </pre>
 * @date 2021-07-13
 */
public interface DmsDocExchangeExpireService extends JpaService<DmsDocExchangeExpireEntity, DmsDocExchangeExpireDao, String> {


    public String add(Integer type, String msgTemplateUuid, String docExchangeRecordUuid, Date sendTime);


    public void taskJob();
}
