package com.wellsoft.pt.org.service.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.org.dao.impl.OrgGroupRoleDaoImpl;
import com.wellsoft.pt.org.entity.OrgGroupRoleEntity;
import com.wellsoft.pt.org.service.OrgGroupRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

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
 * 2023年02月10日   chenq	 Create
 * </pre>
 */
@Service
public class OrgGroupRoleServiceImpl extends AbstractJpaServiceImpl<OrgGroupRoleEntity, OrgGroupRoleDaoImpl, Long> implements OrgGroupRoleService {
    @Override
    public Set<String> queryRoleUuidsByGroupMemberIds(List<String> memberIds) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("memberIds", memberIds);
        List<String> groupUuids = this.dao.listCharSequenceBySQL("select r.role_uuid from org_group_role r where exists (" +
                "select 1 from org_group_member m ,org_group g where g.uuid=m.group_uuid and m.member_id in (:memberIds) and r.group_uuid = g.uuid )", params);
        return Sets.newHashSet(groupUuids);
    }

    @Override
    @Transactional
    public void deleteByRoleUuid(String roleUuid) {
        Assert.hasLength(roleUuid, "角色UUID不能为空！");

        Map<String, Object> params = Maps.newHashMap();
        params.put("roleUuid", roleUuid);
        this.dao.deleteByHQL("delete from OrgGroupRoleEntity where roleUuid=:roleUuid", params);
    }
}
