/*
 * @(#)2019-07-30 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.ops.server.facade.service;


import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.ops.server.dto.ServerRegisterCenterDto;

import java.util.List;

/**
 * Description: 数据库表SERVER_REGISTER_CENTER的门面服务接口，提供给其他模块以及前端调用的业务接口
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
public interface ServerRegisterCenterFacadeService extends Facade {


    ServerRegisterCenterDto getServerRegisterCenterDto(String uuid);

    Select2QueryData loadIpPortSelections(Select2QueryInfo queryInfo);

    boolean testConnect(String ip, int port);

    List<List<String>> testConnectList(List<List<String>> list) throws Exception;

}
