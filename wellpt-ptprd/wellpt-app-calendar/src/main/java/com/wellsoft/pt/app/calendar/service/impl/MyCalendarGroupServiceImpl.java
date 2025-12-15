/*
 * @(#)2018年3月1日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.calendar.service.impl;

import com.wellsoft.pt.app.calendar.dao.MyCalendarGroupDao;
import com.wellsoft.pt.app.calendar.entity.MyCalendarGroupEntity;
import com.wellsoft.pt.app.calendar.service.MyCalendarGroupService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月1日.1	chenqiong		2018年3月1日		Create
 * </pre>
 * @date 2018年3月1日
 */
@Service
public class MyCalendarGroupServiceImpl extends AbstractJpaServiceImpl<MyCalendarGroupEntity, MyCalendarGroupDao, String> implements MyCalendarGroupService {

    // 通过组名获取组信息
    @Override
    public MyCalendarGroupEntity getGroupByName(String userId, String groupName) {
        MyCalendarGroupEntity q = new MyCalendarGroupEntity();
        q.setGroupName(groupName);
        q.setCreator(userId);
        List<MyCalendarGroupEntity> objs = this.dao.listByEntity(q);
        if (CollectionUtils.isEmpty(objs)) {
            return null;
        }
        return objs.get(0);
    }

}
