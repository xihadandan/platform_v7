/*
 * @(#)2018年3月1日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.calendar.service.impl;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;
import com.wellsoft.pt.app.calendar.dao.MyAttentionDao;
import com.wellsoft.pt.app.calendar.entity.MyAttentionEntity;
import com.wellsoft.pt.app.calendar.service.MyAttentionService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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
public class MyAttentionServiceImpl extends AbstractJpaServiceImpl<MyAttentionEntity, MyAttentionDao, String> implements MyAttentionService {

    @Autowired
    private OrgApiFacade orgApiFacade;

    // 按类型获取我的关注列表
    @Override
    public List<MyAttentionEntity> queryMyAttentionListByType(String type) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        MyAttentionEntity q = new MyAttentionEntity();
        q.setCreator(userId);
        q.setAttentionObjType(type);
        List<MyAttentionEntity> objs = this.dao.listByEntity(q);
        if (objs != null) {
            // 按关注时间正排
            Ordering<MyAttentionEntity> order = Ordering.natural().onResultOf(new Function<MyAttentionEntity, Comparable<Date>>() {
                @Override
                public Comparable<Date> apply(MyAttentionEntity arg0) {
                    return arg0.getModifyTime();
                }
            });
            return order.sortedCopy(objs);
        }
        return objs;

    }

    // 取消关注用户
    @Override
    @Transactional
    public boolean cancelAttentionUser(String userId) {
        String currUserId = SpringSecurityUtils.getCurrentUserId();
        MyAttentionEntity q = new MyAttentionEntity();
        q.setCreator(currUserId);
        q.setAttentionObjType(MyAttentionEntity.TYPE_USER);
        q.setAttentionObjId(userId);
        List<MyAttentionEntity> objs = this.dao.listByEntity(q);
        this.dao.deleteByEntities(objs);
        return true;
    }

    // 取消关注群组
    @Override
    @Transactional
    public boolean cancelAttentionGroup(String groupUuid) {
        String currUserId = SpringSecurityUtils.getCurrentUserId();
        MyAttentionEntity q = new MyAttentionEntity();
        q.setCreator(currUserId);
        q.setAttentionObjType(MyAttentionEntity.TYPE_GROUP);
        q.setAttentionObjId(groupUuid);
        List<MyAttentionEntity> objs = this.dao.listByEntity(q);
        this.dao.deleteByEntities(objs);
        return true;

    }

}
