/*
 * @(#)2019-07-30 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.ops.server.facade.service.impl;

import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.context.util.NetUtils;
import com.wellsoft.pt.ops.server.dto.ServerRegisterCenterDto;
import com.wellsoft.pt.ops.server.entity.ServerRegisterCenterEntity;
import com.wellsoft.pt.ops.server.facade.service.ServerRegisterCenterFacadeService;
import com.wellsoft.pt.ops.server.service.ServerRegisterCenterService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Description: 数据库表SERVER_REGISTER_CENTER的门面服务实现类
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
public class ServerRegisterCenterFacadeServiceImpl extends AbstractApiFacade implements
        ServerRegisterCenterFacadeService {

    @Autowired
    private ServerRegisterCenterService serverRegisterCenterService;


    @Override
    public ServerRegisterCenterDto getServerRegisterCenterDto(String uuid) {
        ServerRegisterCenterEntity entity = serverRegisterCenterService.getOne(uuid);
        ServerRegisterCenterDto dto = new ServerRegisterCenterDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public Select2QueryData loadIpPortSelections(Select2QueryInfo queryInfo) {
        List<ServerRegisterCenterEntity> serverRegisterCenterEntityList = serverRegisterCenterService.listAll();
        Select2QueryData data = new Select2QueryData();

        String idProperty = null;
        if (queryInfo != null && queryInfo.getRequest() != null) {
            queryInfo.getOtherParams("idProperty");
        }
        for (ServerRegisterCenterEntity entity : serverRegisterCenterEntityList) {
            data.addResultData(new Select2DataBean(StringUtils.isNotBlank(
                    idProperty) ? idProperty : (entity.getIp() + ":" + entity.getPort()),
                    String.format("%s/%s:%s", entity.getName(), entity.getIp(), entity.getPort())));
        }
        return data;
    }

    @Override
    public boolean testConnect(String ip, int port) {
        return NetUtils.ping(ip, port, null);
    }


    public List<List<String>> testConnectList(List<List<String>> list) throws Exception {
        ExecutorService executorService = Executors.newCachedThreadPool();
        List<Future<Boolean>> futureList = new ArrayList<>();
        for (List<String> strs : list) {
            String[] ipPorts = strs.get(1).split(":");
            final String ip = ipPorts[0];
            final int port = Integer.valueOf(ipPorts[1]);
            Future<Boolean> future = executorService.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return NetUtils.ping(ip, port, 1000);
                }
            });
            futureList.add(future);
        }
        for (int i = 0; i < list.size(); i++) {
            List<String> strs = list.get(i);
            strs.set(1, String.valueOf(futureList.get(i).get()));
        }
        return list;
    }
}
