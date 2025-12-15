package com.wellsoft.pt.user.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.org.entity.UserRoleEntity;
import com.wellsoft.pt.user.dao.UserRoleDaoImpl;
import com.wellsoft.pt.user.service.UserRoleService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年11月28日   chenq	 Create
 * </pre>
 */
@Service
public class UserRoleServiceImpl extends AbstractJpaServiceImpl<UserRoleEntity, UserRoleDaoImpl, Long> implements UserRoleService {
    @Override
    @Transactional
    public void deleteByUserUuid(String userUuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("userUuid", userUuid);
        dao.deleteByHQL("delete from UserRoleEntity where userUuid=:userUuid", param);
    }

    @Override
    @Transactional
    public void deleteByRoleUuid(String roleUuid) {
        Assert.hasLength(roleUuid, "角色UUID不能为空");

        Map<String, Object> param = Maps.newHashMap();
        param.put("roleUuid", roleUuid);
        dao.deleteByHQL("delete from UserRoleEntity where roleUuid=:roleUuid", param);
    }

    @Override
    public List<String> getRolesByUserUuid(String userUuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("userUuid", userUuid);
        return dao.listCharSequenceByHQL("select roleUuid from UserRoleEntity where userUuid=:userUuid", param);
    }

    @Override
    @Transactional
    public void saveUserRole(List<String> roleUuids, String userUuid) {
        if (CollectionUtils.isNotEmpty(roleUuids)) {
            Map<String, Object> param = Maps.newHashMap();
            param.put("userUuid", userUuid);
            List<UserRoleEntity> list = Lists.newArrayList();
            for (String role : roleUuids) {
                param.put("roleUuid", role);
                UserRoleEntity userRoleEntity = dao.getOneByHQL("from UserRoleEntity where userUuid=:userUuid and roleUuid=:roleUuid", param);
                if (userRoleEntity == null) {
                    userRoleEntity = new UserRoleEntity(userUuid, role);
                    list.add(userRoleEntity);
                }
            }
            if (!list.isEmpty()) {
                saveAll(list);
            }
        }
    }

    @Override
    @Transactional
    public void deleteUserRoleByRoleUuidAndUserUuids(String roleUuid, List<String> userUuids) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("roleUuid", roleUuid);
        param.put("userUuids", userUuids);
        dao.deleteByHQL("delete from UserRoleEntity where roleUuid=:roleUuid and userUuid in :userUuids", param);
    }
}
