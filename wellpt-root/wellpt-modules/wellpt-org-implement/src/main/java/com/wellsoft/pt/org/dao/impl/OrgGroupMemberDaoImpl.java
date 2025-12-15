package com.wellsoft.pt.org.dao.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.org.entity.OrgGroupMemberEntity;
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
public class OrgGroupMemberDaoImpl extends AbstractJpaDaoImpl<OrgGroupMemberEntity, Long> {

    public void saveGroupMember(Long groupUuid, List<String> memberIds, List<String> memberIdPaths) {
        this.deleteByGroupUuid(groupUuid);
        if (CollectionUtils.isNotEmpty(memberIds)) {
            List<OrgGroupMemberEntity> waitSave = Lists.newArrayListWithCapacity(memberIds.size());
            int i = 1;
            for (String mem : memberIds) {
                OrgGroupMemberEntity entity = new OrgGroupMemberEntity();
                entity.setGroupUuid(groupUuid);
                entity.setMemberId(mem);
                if (CollectionUtils.isNotEmpty(memberIdPaths) && memberIdPaths.size() == memberIds.size()) {
                    entity.setMemberIdPath(memberIdPaths.get(i - 1));
                }
                entity.setSeq(i++);
                waitSave.add(entity);
            }
            saveAll(waitSave);
        }
    }

    public List<String> listGroupMemmberIds(Long groupUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("groupUuid", groupUuid);
        List<OrgGroupMemberEntity> entities = this.listByHQL("from OrgGroupMemberEntity where groupUuid=:groupUuid order by seq asc",
                params);
        if (CollectionUtils.isNotEmpty(entities)) {
            List<String> ids = Lists.newArrayList();
            for (OrgGroupMemberEntity entity : entities) {
                ids.add(entity.getMemberId());
            }
            return ids;
        }

        return Collections.emptyList();
    }

    public List<OrgGroupMemberEntity> listGroupMember(Long groupUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("groupUuid", groupUuid);
        List<OrgGroupMemberEntity> entities = this.listByHQL("from OrgGroupMemberEntity where groupUuid=:groupUuid order by seq asc",
                params);
        return entities;
    }

    public void deleteByGroupUuid(Long groupUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("groupUuid", groupUuid);
        deleteByHQL("delete OrgGroupMemberEntity where groupUuid=:groupUuid", params);
    }
}
