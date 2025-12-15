package com.wellsoft.pt.org.service.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.org.dao.impl.OrgGroupMemberDaoImpl;
import com.wellsoft.pt.org.entity.OrgGroupMemberEntity;
import com.wellsoft.pt.org.service.OrgGroupMemberService;
import org.springframework.stereotype.Service;

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
public class OrgGroupMemberServiceImpl extends AbstractJpaServiceImpl<OrgGroupMemberEntity, OrgGroupMemberDaoImpl, Long> implements OrgGroupMemberService {
    @Override
    public Set<String> queryGroupUuidsByMemberIds(List<String> memberIds) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("memberIds", memberIds);
        List<String> groupUuids = this.dao.listCharSequenceByHQL("select groupUuid from OrgGroupMemberEntity where memberId in (:memberIds)", params);
        return Sets.newHashSet(groupUuids);
    }

    @Override
    public List<String> getMemberIdsByGroupUuid(Long groupUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("groupUuid", groupUuid);
        return dao.listCharSequenceBySQL("select member_id from org_group_member where group_uuid=:groupUuid", params);
    }

    @Override
    public List<String> getMemberIdsByGroupId(String groupId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("groupId", groupId);
        return dao.listCharSequenceBySQL("select m.member_id from org_group_member m where exists (select 1 from org_group g where g.id = :groupId and g.uuid = m.group_uuid ) ", params);
    }
}
