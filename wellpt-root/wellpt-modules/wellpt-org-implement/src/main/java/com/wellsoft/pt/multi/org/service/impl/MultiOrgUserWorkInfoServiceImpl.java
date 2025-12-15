/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.basicdata.preferences.facade.service.CdUserPreferencesFacadeService;
import com.wellsoft.pt.jpa.hibernate4.NamedQueryScriptLoader;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.dao.MultiOrgUserWorkInfoDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserWorkInfo;
import com.wellsoft.pt.multi.org.service.MultiJobRankLevelService;
import com.wellsoft.pt.multi.org.service.MultiOrgUserWorkInfoService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class MultiOrgUserWorkInfoServiceImpl
        extends AbstractJpaServiceImpl<MultiOrgUserWorkInfo, MultiOrgUserWorkInfoDao, String>
        implements MultiOrgUserWorkInfoService {

    @Autowired
    private CdUserPreferencesFacadeService cdUserPreferencesFacadeService;

    @Autowired
    private MultiJobRankLevelService multiJobRankLevelService;

    @Override
    public MultiOrgUserWorkInfo getUserWorkInfo(String userId) {
        List<MultiOrgUserWorkInfo> obj = this.dao.listByFieldEqValue("userId", userId);
        if (CollectionUtils.isNotEmpty(obj)) {
            return obj.get(0);
        }
        return null;
    }

    @Override
    public void deleteAllUserByUnit(String unitId) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("unitId", unitId);
        this.dao.updateByNamedSQL("deleteAllUserByUnit", params);

    }

    @Override
    public void deleteUser(String id) {
        MultiOrgUserWorkInfo obj = this.getUserWorkInfo(id);
        if (obj != null) {
            this.dao.delete(obj);
            // 根据用户id删除工作职级职档
            multiJobRankLevelService.deleteByUserId(obj.getUserId());
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.multi.org.service.MultiOrgUserWorkInfoService#getUserWorkInfosByJobId(java.lang.String)
     */
    @Override
    public List<MultiOrgUserWorkInfo> getUserWorkInfosByJobId(String jobId) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("jobId", jobId);
        return this.dao.listByNameSQLQuery("getUserWorkInfosByJobIdQuery", values);
    }

    @Override
    public int countUserByJob(Map<String, Object> params) {
        return this.dao.getNumberBySQL(NamedQueryScriptLoader.generateDynamicNamedQueryString(this.dao.sessionFactory(),
                "countUserByJob", params), params);
    }

    @Override
    @Transactional
    public void updateDefWorkbench(String defPageUuid) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        cdUserPreferencesFacadeService.save("WORKBENCH", StringUtils.EMPTY, userId, "WORKBENCH", defPageUuid,
                "系统工作台记住上一次选择");
    }

    @Override
    public List<MultiOrgUserWorkInfo> getUserWorkInfosByUserIds(List<String> userids) {
        if (CollectionUtils.isEmpty(userids)) {
            return Lists.newArrayList();
        }
        return this.dao.listByFieldInValues("userId", userids);
    }

    @Override
    public List<MultiOrgUserWorkInfo> getUserWorkInfoByUserIds(List<String> userIds) {

        Map<String, Object> values = Maps.newHashMap();
        if (userIds == null || userIds.size() == 0) {
            return new ArrayList<>();
        }

        // 超过1000 会报错
        if (userIds.size() > 1000) {
            int num = userIds.size() / 1000;
            List<List<String>> userIdList = new ArrayList<>();
            for (int i = 0; i < num; i++) {
                int end = i * 1000 + 999;
                userIdList.add(userIds.subList(i * 1000, end));
                // 不足量处理
                if (i == num - 1) {
                    end = (i + 1) * 1000 + (2005 % 1000);
                    userIdList.add(userIds.subList((i + 1) * 1000, end));
                }
            }
            List<MultiOrgUserWorkInfo> multiOrgUserWorkInfos = Lists.newArrayList();
            for (List<String> userIds1 : userIdList) {
                values.put("userIds", userIds1);
                List<MultiOrgUserWorkInfo> multiOrgUserWorkInfos1 = this.dao
                        .listByNameSQLQuery("getUserWorkInfoByUserIds", values);
                if (multiOrgUserWorkInfos1 != null && multiOrgUserWorkInfos1.size() > 0) {
                    multiOrgUserWorkInfos.addAll(multiOrgUserWorkInfos1);
                }
            }
            return multiOrgUserWorkInfos;
        } else {
            values.put("userIds", userIds);
            return this.dao.listByNameSQLQuery("getUserWorkInfoByUserIds", values);
        }

    }

    @Override
    public List<MultiOrgUserWorkInfo> getUserWorkInfoByLeaderIds(List<String> leaderIds) {
        StringBuffer hqlSb = new StringBuffer(" from MultiOrgUserWorkInfo  ");
        for (int i = 0; i < leaderIds.size(); i++) {
            if (i == 0) {
                hqlSb.append(" where directLeaderIds like '%" + leaderIds.get(i) + "%' ");
            } else {
                hqlSb.append(" or directLeaderIds like '%" + leaderIds.get(i) + "%' ");
            }
        }
        return listByHQL(hqlSb.toString(), new HashMap<>());
    }


}
