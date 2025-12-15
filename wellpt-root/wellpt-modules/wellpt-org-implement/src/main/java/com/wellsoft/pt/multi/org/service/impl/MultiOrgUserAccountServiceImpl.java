/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.PropertyFilter;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.PinyinUtil;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeDto;
import com.wellsoft.pt.multi.org.bean.OrgUserDto;
import com.wellsoft.pt.multi.org.bean.OrgUserTableVo;
import com.wellsoft.pt.multi.org.dao.MultiOrgUserAccountDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgElement;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.query.MultiOrgUserAccountPwdCreateTimeQueryItem;
import com.wellsoft.pt.multi.org.service.MultiOrgTreeNodeService;
import com.wellsoft.pt.multi.org.service.MultiOrgUserAccountService;
import com.wellsoft.pt.security.config.entity.MultiUserLoginSettingsEntity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
public class MultiOrgUserAccountServiceImpl
        extends AbstractJpaServiceImpl<MultiOrgUserAccount, MultiOrgUserAccountDao, String>
        implements MultiOrgUserAccountService {

    @Autowired
    private MultiOrgService multiOrgService;
    @Autowired
    private MultiOrgTreeNodeService multiOrgTreeNodeService;

    @Override
    public MultiOrgUserAccount getAccountByLoignName(String loginName) {
        MultiOrgUserAccount q = new MultiOrgUserAccount();
        q.setLoginNameLowerCase(loginName.toLowerCase());
        List<MultiOrgUserAccount> objs = this.dao.listByEntity(q);
        if (CollectionUtils.isEmpty(objs)) {
            return null;
        }
        return objs.get(0);
    }

    @Override
    public List<OrgUserTableVo> query(QueryInfo queryInfo) {
        List<PropertyFilter> paramsList = queryInfo.getPropertyFilters();
        HashMap<String, Object> params = new HashMap<String, Object>();
        if (!CollectionUtils.isEmpty(paramsList)) {
            for (PropertyFilter f : paramsList) {
                params.put(f.getPropertyNames()[0], f.getMatchValue());
            }
        }
        params.put("orderBy", queryInfo.getOrderBy());
        // 需要按组织节点过滤，所以计算出该节点对应的所有职位
        if (params.containsKey("eleIdPath")) {
            String[] ids = params.get("eleIdPath").toString().split("/");
            String verId = ids[0];
            String eleId = ids[ids.length - 1];
            List<OrgTreeNodeDto> jobs = this.multiOrgTreeNodeService.queryJobListByEleIdAndVersionId(verId, eleId);
            if (CollectionUtils.isNotEmpty(jobs)) {
                List<String> jobSql = Lists.newArrayList();
                for (OrgTreeNodeDto dto : jobs) {
                    jobSql.add(" ( i.job_ids like '%" + dto.getEleId() + "%' ) ");
                }
                params.put("jobSql", StringUtils.join(jobSql, "or"));
            } else {
                // 该节点下面没有任何职位，可以直接返回空数组了
                return Lists.newArrayList();
            }
        }
        List<OrgUserTableVo> objs = this.dao.listItemByNameSQLQuery("queryUserAccountListForPage", OrgUserTableVo.class,
                params, queryInfo.getPagingInfo());
        List<OrgUserTableVo> list = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(objs)) {
            for (OrgUserTableVo vo : objs) {
                List<String> jobNames = Lists.newArrayList();
                List<String> deptNames = Lists.newArrayList();
                String[] jobIds = StringUtils.isBlank(vo.getJobIds()) ? new String[0] : vo.getJobIds().split(";");
                String[] deptIds = StringUtils.isBlank(vo.getDeptIds()) ? new String[0] : vo.getDeptIds().split(";");
                if (jobIds.length > 0) {
                    for (String id : jobIds) {
                        MultiOrgElement e = multiOrgService.getOrgElementById(id);
                        jobNames.add(e.getName());
                    }
                }
                if (deptIds.length > 0) {
                    for (String id : deptIds) {
                        MultiOrgElement e = multiOrgService.getOrgElementById(id);
                        deptNames.add(e.getName());
                    }
                }
                vo.setJobNames(StringUtils.join(jobNames, ";"));
                vo.setDeptNames(StringUtils.join(deptNames, ";"));
                list.add(vo);
            }
        }
        return list;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     */
    @Override
    public List<String> queryUserIdsLikeName(String name) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("name", name);
        List<QueryItem> queryItemList = listQueryItemByNameSQLQuery("queryUserIdsLikeName", params, null);
        return Lists.transform(queryItemList, new Function<QueryItem, String>() {
            @Override
            public String apply(QueryItem item) {
                return item.getString("id");
            }
        });
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     */
    @Override
    public List<OrgUserDto> queryUserDtoListByIds(List<String> userIds) {
        if (userIds == null || userIds.size() == 0) {
            return new ArrayList<>();
        }
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("userIds", userIds);
        return this.listItemByNameSQLQuery("queryUserDtoListByIds", OrgUserDto.class, params, null);
    }

    @Override
    public List<MultiOrgUserAccount> queryAllAccountOfUnit(String systemUnitId) {
        MultiOrgUserAccount q = new MultiOrgUserAccount();
        q.setSystemUnitId(systemUnitId);
        return this.dao.listByEntity(q);
    }

    @Override
    public MultiOrgUserAccount getAccountById(String userId) {
        List<MultiOrgUserAccount> list = this.dao.listByFieldEqValue("id", userId);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    @Override
    public List<MultiOrgUserAccount> queryAllAdminIdsBySystemUnitId(String unitId) {
        MultiOrgUserAccount q = new MultiOrgUserAccount();
        q.setSystemUnitId(unitId);
        q.setIsForbidden(0);
        q.setType(MultiOrgUserAccount.TYPE_UNIT_ADMIN);
        return this.dao.listByEntity(q);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.multi.org.service.MultiOrgUserAccountService#findByExample(com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount)
     */
    @Override
    public List<MultiOrgUserAccount> findByExample(MultiOrgUserAccount q) {
        return this.dao.listByEntity(q);
    }

    /**
     * 根据中文名获取唯一登入名
     *
     * @param userName
     * @return
     */
    @Override
    public String getUniqueLoginNameByUserName(String userName) {
        int iteratorCount = 1;
        String loginName = PinyinUtil.getLoginName(userName);
        MultiOrgUserAccount entity = new MultiOrgUserAccount();
        do {
            String tmpLoginName = loginName + (iteratorCount > 1 ? iteratorCount : "");
            entity.setLoginName(tmpLoginName);
            List<MultiOrgUserAccount> list = dao.listByEntity(entity);
            if (list == null || list.isEmpty()) {
                return tmpLoginName;
            } else {
                iteratorCount++;
            }
        } while (true);
    }

    @Override
    public long countBySystemUnitId(String systemUnitId, Date startTime, Date endTime) {
        Map<String, Object> params = new HashMap<>();
        params.put("systemUnitId", systemUnitId);
        StringBuilder hql = new StringBuilder(
                "select count(uuid) from MultiOrgUserAccount where systemUnitId=:systemUnitId");
        if (startTime != null) {
            hql.append(" and createTime >= :startTime");
            params.put("startTime", startTime);
        }
        if (endTime != null) {
            hql.append(" and createTime <= :endTime");
            params.put("endTime", endTime);
        }
        long count = this.dao.countByHQL(hql.toString(), params);
        return count;
    }

    @Override
    @Transactional
    public void initPwdCreateTime() {
        Map<String, Object> params = new HashMap<>();
        this.dao.updateByNamedSQL("initPwdCreateTime", params);
    }

    @Override
    public List<MultiOrgUserAccountPwdCreateTimeQueryItem> getAllAccountPwdCreateTime() {
        Map<String, Object> values = Maps.newHashMap();
        // QueryItem implements BaseQueryItem
        List<MultiOrgUserAccountPwdCreateTimeQueryItem> queryItems = this.dao.listItemByNameSQLQuery(
                "getAllAccountPwdCreateTime", MultiOrgUserAccountPwdCreateTimeQueryItem.class, values,
                new PagingInfo(1, Integer.MAX_VALUE));
        return queryItems;
    }

    @Override
    public List<MultiOrgUserAccount> getUnlockAccounts() {
        Map<String, Object> values = Maps.newHashMap();
        return this.dao.listByNameSQLQuery("getUnlockAccounts", values);
    }

    @Override
    public List<MultiOrgUserAccount> getCoerceUnlockAccounts() {
        Map<String, Object> values = Maps.newHashMap();
        return this.dao.listByNameSQLQuery("getCoerceUnlockAccounts", values);
    }

    @Override
    public List<MultiOrgUserAccount> getUserAccountListByloginNames(List<String> loginNames) {

        Map<String, Object> values = Maps.newHashMap();
        values.put("loginNames", loginNames);
        return this.dao.listByNameSQLQuery("getUserAccountListByloginNames", values);
    }

    @Override
    public List<MultiOrgUserAccount> getAccountByUsername(String username) {
        return this.dao.listByFieldEqValue("userName", username);
    }

    @Override
    public List<MultiOrgUserAccount> getAccountsByUserIds(List<String> ids) {
        if (ids == null || ids.size() == 0) {
            return Lists.newArrayList();
        }
        return this.dao.listByFieldInValues("id", ids);
    }

    @Override
    public int countUnforbiddenAccount(String systemUnitId) {
        Map<String, Object> params = Maps.newHashMap();
        StringBuilder builder = new StringBuilder("from MultiOrgUserAccount where isForbidden = 0");
        if (StringUtils.isNotBlank(systemUnitId)) {
            params.put("systemUnitId", systemUnitId);
            builder.append(" and systemUnitId=:systemUnitId");
        }
        return (int) this.dao.countByHQL(builder.toString(), params);
    }

    @Override
    public long countBySystemUnitIds(List<String> systemUnitIds) {
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("unitIds", systemUnitIds);
        long count = this.dao.countByHQL("from MultiOrgUserAccount where systemUnitId in (:unitIds)", query);
        return count;
    }

    @Override
    public List<MultiOrgUserAccount> getUserAccountByLoginNameIgnoreCase(String userName,
                                                                         MultiUserLoginSettingsEntity loginSetting) {
        Map<String, Object> param = new HashMap<>();
        param.putAll(JSONObject.parseObject(JSONObject.toJSONString(loginSetting)));
        param.put("userName", userName);
        return listByNameSQLQuery("getAllAccountByUserName", param);
    }

    @Override
    public List<MultiOrgUserAccount> getMultiOrgUserAccountListByUserNameKey(String userNameKey, String userUnitId) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("userNameKey", userNameKey);
        values.put("userUnitId", userUnitId);
        return this.dao.listByNameSQLQuery("getMultiOrgUserAccountListByUserNameKey", values);
    }
}
