package com.wellsoft.pt.user.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.user.dao.UserTypeRoleDaoImpl;
import com.wellsoft.pt.user.entity.UserTypeRoleEntity;
import com.wellsoft.pt.user.enums.UserTypeEnum;
import com.wellsoft.pt.user.service.UserTypeRoleService;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年08月13日   chenq	 Create
 * </pre>
 */
@Service
public class UserTypeRoleServiceImpl extends AbstractJpaServiceImpl<UserTypeRoleEntity, UserTypeRoleDaoImpl, String> implements UserTypeRoleService {
    @Override
    public Set<String> getUserTypeRoles(UserTypeEnum type) {
        List<UserTypeRoleEntity> roleEntityList = this.dao.listByFieldEqValue("type", type);
        Set<String> roles = Sets.newHashSet();
        for (UserTypeRoleEntity entity : roleEntityList) {
            roles.add(entity.getRole());
        }
        return roles;
    }

    @Override
    @Transactional
    public void saveUserTypeRole(UserTypeEnum type, Set<String> roles) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("t", type.ordinal());
        this.dao.deleteBySQL("delete from USER_TYPE_ROLE where type = :t", params);

        List<UserTypeRoleEntity> entities = Lists.newArrayList();
        for (String r : roles) {
            UserTypeRoleEntity entity = new UserTypeRoleEntity();
            entity.setType(type);
            entity.setRole(r);
            entities.add(entity);
        }
        this.dao.saveAll(entities);
    }

    @Override
    @Transactional
    public void saveUserTypeRole(Map<String, Set<String>> data) {
        if (MapUtils.isNotEmpty(data)) {
            Set<String> types = data.keySet();
            for (String typeEnum : types) {
                Set<String> set = Sets.newHashSet();
                set.addAll(data.get(typeEnum));
                this.saveUserTypeRole(UserTypeEnum.valueOf(typeEnum), set);
            }
        }
    }
}
