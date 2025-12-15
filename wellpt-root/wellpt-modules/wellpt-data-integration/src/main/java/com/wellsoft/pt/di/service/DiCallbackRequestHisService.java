/*
 * @(#)2019-07-25 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.di.service;


import com.wellsoft.pt.di.dao.DiCallbackRequestHisDao;
import com.wellsoft.pt.di.entity.DiCallbackRequestHisEntity;
import com.wellsoft.pt.jpa.service.JpaService;

/**
 * Description: 数据库表DI_CALLBACK_REQUEST_HIS的service服务接口
 *
 * @author chenq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019-07-25.1	chenq		2019-07-25		Create
 * </pre>
 * @date 2019-07-25
 */
public interface DiCallbackRequestHisService extends JpaService<DiCallbackRequestHisEntity, DiCallbackRequestHisDao, String> {

}
