package com.wellsoft.pt.org.dao.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.org.entity.OrgGroupOwnerEntity;
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
public class OrgGroupOwnerDaoImpl extends AbstractJpaDaoImpl<OrgGroupOwnerEntity, Long> {

    public void saveOwner(Long groupUuid, List<String> owner) {
        deleteByGroupUuid(groupUuid);

        if (CollectionUtils.isNotEmpty(owner)) {
            List<OrgGroupOwnerEntity> waitSave = Lists.newArrayListWithCapacity(owner.size());
            for (String userId : owner) {
                OrgGroupOwnerEntity entity = new OrgGroupOwnerEntity();
                entity.setGroupUuid(groupUuid);
                entity.setOwnerId(userId);
                waitSave.add(entity);
            }
            saveAll(waitSave);
        }
    }

    public List<String> listGroupOwnerIds(Long groupUuid) {
        List<OrgGroupOwnerEntity> entities = this.listByFieldEqValue("groupUuid", groupUuid);
        if (CollectionUtils.isNotEmpty(entities)) {
            List<String> ids = Lists.newArrayList();
            for (OrgGroupOwnerEntity entity : entities) {
                ids.add(entity.getOwnerId());
            }
            return ids;
        }

        return Collections.emptyList();
    }

    public void deleteByGroupUuid(Long groupUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("groupUuid", groupUuid);
        deleteByHQL("delete OrgGroupOwnerEntity where groupUuid=:groupUuid", params);
    }
}
