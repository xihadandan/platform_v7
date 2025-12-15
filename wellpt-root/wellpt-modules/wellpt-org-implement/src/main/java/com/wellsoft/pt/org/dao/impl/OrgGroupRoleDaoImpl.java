package com.wellsoft.pt.org.dao.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.org.entity.OrgGroupRoleEntity;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月09日   chenq	 Create
 * </pre>
 */
@Repository
public class OrgGroupRoleDaoImpl extends AbstractJpaDaoImpl<OrgGroupRoleEntity, Long> {

    public void saveRole(Long groupUuid, List<String> roleUuids) {
        this.deleteByGroupUuid(groupUuid);

        if (CollectionUtils.isNotEmpty(roleUuids)) {
            List<OrgGroupRoleEntity> waitSave = Lists.newArrayListWithCapacity(roleUuids.size());
            for (String roleUuid : roleUuids) {
                OrgGroupRoleEntity role = new OrgGroupRoleEntity();
                role.setGroupUuid(groupUuid);
                role.setRoleUuid(roleUuid);
                waitSave.add(role);
            }
            saveAll(waitSave);
        }
    }

    public List<String> listGroupRoleUuids(Long groupUuid) {
        List<OrgGroupRoleEntity> entities = this.listByFieldEqValue("groupUuid", groupUuid);
        if (CollectionUtils.isNotEmpty(entities)) {
            List<String> ids = Lists.newArrayList();
            for (OrgGroupRoleEntity entity : entities) {
                ids.add(entity.getRoleUuid());
            }
            return ids;
        }

        return Collections.emptyList();
    }

    public void deleteByGroupUuid(Long groupUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("groupUuid", groupUuid);
        deleteByHQL("delete OrgGroupRoleEntity where groupUuid=:groupUuid", params);
    }
}
