/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.dao.MultiOrgUserLeaderDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserLeader;
import com.wellsoft.pt.multi.org.service.MultiOrgUserLeaderService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
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
 * 2018年4月4日.1	chenqiong		2018年4月4日		Create
 * </pre>
 * @date 2018年4月4日
 */
@Service
public class MultiOrgUserLeaderServiceImpl extends AbstractJpaServiceImpl<MultiOrgUserLeader, MultiOrgUserLeaderDao, String> implements
        MultiOrgUserLeaderService {

    @Override
    public List<MultiOrgUserLeader> addLeaderListByType(String userId, String ids, String names, Integer leaderType) {
        String[] idArray = ids.split(";");
        String[] nameArray = names.split(";");
        ArrayList<MultiOrgUserLeader> list = new ArrayList<MultiOrgUserLeader>();
        for (int i = 0; i < idArray.length; i++) {
            MultiOrgUserLeader m = new MultiOrgUserLeader();
            m.setUserId(userId);
            m.setLeaderObjId(idArray[i]);
            m.setLeaderObjName(nameArray[i]);
            m.setLeaderObjType(idArray[i].substring(0, 1));
            m.setLeaderType(leaderType);
            list.add(m);
        }
        this.saveAll(list);
        return list;
    }

    @Override
    public List<MultiOrgUserLeader> queryLeaderListByType(String userId, Integer leaderType) {
        MultiOrgUserLeader q = new MultiOrgUserLeader();
        q.setUserId(userId);
        q.setLeaderType(leaderType);
        return this.dao.listByEntity(q);
    }

    @Override
    public boolean deleteLeaderList(String userId) {
        MultiOrgUserLeader q = new MultiOrgUserLeader();
        q.setUserId(userId);
        List<MultiOrgUserLeader> objs = this.dao.listByEntity(q);
        if (!CollectionUtils.isEmpty(objs)) {
            this.deleteByEntities(objs);
        }
        return true;
    }
}
