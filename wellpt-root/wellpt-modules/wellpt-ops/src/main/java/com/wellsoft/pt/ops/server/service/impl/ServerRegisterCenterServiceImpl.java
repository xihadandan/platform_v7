/*
 * @(#)2019-07-30 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.ops.server.service.impl;

import com.wellsoft.context.util.NetUtils;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.ops.server.dao.ServerRegisterCenterDao;
import com.wellsoft.pt.ops.server.entity.ServerRegisterCenterEntity;
import com.wellsoft.pt.ops.server.service.ServerRegisterCenterService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description: 数据库表SERVER_REGISTER_CENTER的service服务接口实现类
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
@Service
public class ServerRegisterCenterServiceImpl extends
        AbstractJpaServiceImpl<ServerRegisterCenterEntity, ServerRegisterCenterDao, String> implements
        ServerRegisterCenterService {

    @Value("${app.name:威尔基础平台应用}")
    private String appName;

    @Value("${app.description:威尔基础平台应用}")
    private String appDescription;

    @Override
    @Transactional
    public void registCurrentServer() {
        ServerRegisterCenterEntity entity = new ServerRegisterCenterEntity();
        entity.setIp(NetUtils.getLocalAddress());
        entity.setPort(NetUtils.getServerPort());
        List<ServerRegisterCenterEntity> registerd = this.dao.listByEntity(entity);
        if (CollectionUtils.isNotEmpty(registerd)) {
            entity = registerd.get(0);
        }
        entity.setName(appName);
        entity.setRemark(appDescription);
        entity.setMachine(NetUtils.getLocalHost());
        this.dao.save(entity);
    }
}
