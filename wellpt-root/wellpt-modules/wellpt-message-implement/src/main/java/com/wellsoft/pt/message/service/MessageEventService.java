package com.wellsoft.pt.message.service;

/*
 * @(#)2014-10-22 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */


import com.wellsoft.context.jdbc.entity.IdEntity;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: 消息接口实现类
 *
 * @author tony
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-22.1	tony		2014-10-22		Create
 * </pre>
 * @date 2014-10-22
 */
public interface MessageEventService {
    //获得所有的数据接口
    public List getEventSourceList(String s, String id);

    //获得所有的数据接口
    public List getEventClientSourceList(String s, String id);

    //获得所有的数据接口
    public List getEventServerSourceList(String s, String id);

    //获得一个数据接口的执行犯法
    public void exeServerEventInstance(String message, String viewpoint, String note);

    public void exeServerEventInstanceWithNone(String id);

    public <ENTITY extends IdEntity> void exeClientEventInstance(String id, String templateId, Collection<ENTITY> entities,
                                                                 Map<Object, Object> dataMap, Map<String, Object> extraData, Collection<String> recipients, ENTITY entity);

}
