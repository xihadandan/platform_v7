/*
 * @(#)2020年7月3日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.app.dingtalk.dao.MultiOrgDingRoleDao;
import com.wellsoft.pt.app.dingtalk.entity.MultiOrgDingRole;
import com.wellsoft.pt.app.dingtalk.entity.MultiOrgDingUser;
import com.wellsoft.pt.app.dingtalk.service.MultiOrgDingRoleService;
import com.wellsoft.pt.app.dingtalk.service.MultiOrgDingUserService;
import com.wellsoft.pt.app.dingtalk.support.DingtalkConfig;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.audit.bean.RoleBean;
import com.wellsoft.pt.security.audit.entity.Role;
import com.wellsoft.pt.security.audit.facade.service.RoleFacadeService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年7月3日.1	zhongzh		2020年7月3日		Create
 * </pre>
 * @date 2020年7月3日
 */
@Service
@Deprecated
public class MultiOrgDingRoleServiceImpl extends AbstractJpaServiceImpl<MultiOrgDingRole, MultiOrgDingRoleDao, String>
        implements MultiOrgDingRoleService {

    @Autowired
    private DingtalkConfig dingtalkConfig;

    @Autowired
    private RoleFacadeService roleFacadeService;

    @Autowired
    private MultiOrgDingUserService multiOrgDingUserService;

    @Override
    @Transactional
    public void saveAndUpdateRoleFromDingtalk(JSONObject roleObj) {
        if (null != roleObj) {
            String id = roleObj.getString("id");
            String name = roleObj.getString("name");
            String groupId = roleObj.optString("groupId");
            String groupName = roleObj.optString("groupName");
            JSONArray roleUsers = roleObj.optJSONArray("users");
            MultiOrgDingRole multiOrgDingRole = getByDingRoleId(id);
            RoleBean roleBean = null;
            if (null != multiOrgDingRole && StringUtils.isNotBlank(multiOrgDingRole.getRoleId())) {
                Role role = roleFacadeService.getRoleById(multiOrgDingRole.getRoleId());
                // role = role == null ? roleFacadeService.getByName(name) : role;
                if (null != role && StringUtils.isNotBlank(role.getUuid())) {
                    roleBean = roleFacadeService.getBean(role.getUuid());
                }
            } else {
                List<Role> roles = roleFacadeService.queryByName(name);
                for (Role role : roles) {
                    boolean isExists = getByRoleId(role.getId()) != null;
                    if (isExists) {
                        // 去掉已经关联的角色
                        continue;
                    }
                    roleBean = roleFacadeService.getBean(role.getUuid());
                }
            }
            if (null == roleBean) {
                roleBean = new RoleBean();
                roleBean.setCode(id);
                roleBean.setId("ROLE_DINGTALK_" + StringUtils.replace(dingtalkConfig.getSystemUnitId(), "0", "") + "_"
                        + id);
            } else if (null != roleUsers && false == CollectionUtils.isEmpty(roleUsers)) {
                List<String> addMemberIds = Lists.newArrayList(), delMemberIds = Lists.newArrayList();
                String[] deleteIds = StringUtils.split(roleBean.getMemberIds(), Separator.SEMICOLON.getValue());
                if (null != deleteIds && deleteIds.length > 0) {
                    for (String memberId : deleteIds) {
                        if (StringUtils.startsWith(memberId, IdPrefix.USER.getValue())) {
                            MultiOrgDingUser multiOrgDingUser = multiOrgDingUserService.getByPtUserId(memberId);
                            if (null == multiOrgDingUser) {
                                continue;
                            }
                            delMemberIds.add(memberId);
                        } else {
                            addMemberIds.add(memberId);
                        }
                    }
                }
                for (int i = 0; i < roleUsers.size(); i++) {
                    JSONObject user = roleUsers.getJSONObject(i);
                    String userId = user.getString("userid");
                    MultiOrgDingUser multiOrgDingUser = multiOrgDingUserService.getByDingUserId(userId);
                    if (null == multiOrgDingUser || StringUtils.isBlank(multiOrgDingUser.getUserId())) {
                        continue;
                    }
                    addMemberIds.add(multiOrgDingUser.getUserId());
                }
                roleBean.setDeleteids(StringUtils.join(delMemberIds, Separator.SEMICOLON.getValue()));
                roleBean.setMemberIds(StringUtils.join(addMemberIds, Separator.SEMICOLON.getValue()));
            }

            roleBean.setName(name);
            roleFacadeService.saveBean(roleBean);
            if (null == multiOrgDingRole) {
                multiOrgDingRole = new MultiOrgDingRole();
                multiOrgDingRole.setDingRoleId(id);
                multiOrgDingRole.setRoleId(roleBean.getId());
                roleBean.setSystemUnitId(dingtalkConfig.getSystemUnitId());
            }
            multiOrgDingRole.setDingRoleName(name);
            if (StringUtils.isNotBlank(groupId) && StringUtils.isNotBlank(groupName)) {
                multiOrgDingRole.setDingGroupId(groupId);
                multiOrgDingRole.setDingGroupName(groupName);
            }
            save(multiOrgDingRole);
        }
    }

    @Override
    @Transactional
    public void deleteRoleFromDingtalk(String dingRoleId) {
        // 平台角色存在人员时不删除
        MultiOrgDingRole multiOrgDingRole = getByDingRoleId(dingRoleId);
        if (null != multiOrgDingRole) {
            Role role = roleFacadeService.getRoleById(multiOrgDingRole.getRoleId());
            if (null != role) {
                roleFacadeService.remove(role.getUuid());
            }
            delete(multiOrgDingRole);
        }
    }

    @Override
    public MultiOrgDingRole getByDingRoleId(String dingRoleId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("dingRoleId", dingRoleId);
        params.put("systemUnitId", dingtalkConfig.getSystemUnitId());
        String hql = "from MultiOrgDingRole t where t.dingRoleId = :dingRoleId and exists (select 1 from Role tt where tt.id = t.roleId and tt.systemUnitId = :systemUnitId)";
        List<MultiOrgDingRole> list = listByHQL(hql, params);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public MultiOrgDingRole getByRoleId(String roleId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("roleId", roleId);
        String hql = "from MultiOrgDingRole t where t.roleId = :roleId";
        List<MultiOrgDingRole> list = listByHQL(hql, params);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

}
