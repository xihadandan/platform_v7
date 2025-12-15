/*
 * @(#)2019-07-30 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.ops.server.service;


import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.ops.server.dao.ServerRegisterCenterDao;
import com.wellsoft.pt.ops.server.entity.ServerRegisterCenterEntity;

/**
 * Description: 数据库表SERVER_REGISTER_CENTER的service服务接口
 *
 * @author chenq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019-07-30.1	chenq		2019-07-30		Create
 * </pre>
 * @date 2019-07-30
 */
public interface ServerRegisterCenterService extends
        JpaService<ServerRegisterCenterEntity, ServerRegisterCenterDao, String> {

    void registCurrentServer();
}
