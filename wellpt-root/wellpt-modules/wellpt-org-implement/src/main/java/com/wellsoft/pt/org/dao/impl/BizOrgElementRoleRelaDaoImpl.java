package com.wellsoft.pt.org.dao.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.org.entity.BizOrgElementEntity;
import com.wellsoft.pt.org.entity.BizOrgElementRoleRelaEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年11月28日   chenq	 Create
 * </pre>
 */
@Repository
public class BizOrgElementRoleRelaDaoImpl extends AbstractJpaDaoImpl<BizOrgElementRoleRelaEntity, Long> {
    public void saveBizOrgElementRoleRela(BizOrgElementEntity entity, List<String> roleUuids) {

        if (entity.getUuid() != null) {
            this.deleteByBizOrgElementId(entity.getId());
        }

        if (CollectionUtils.isNotEmpty(roleUuids)) {
            List<BizOrgElementRoleRelaEntity> roleRelas = Lists.newArrayList();
            for (String u : roleUuids) {
                roleRelas.add(new BizOrgElementRoleRelaEntity(u, entity.getId()));
            }
            saveAll(roleRelas);
        }
    }

    public void deleteByBizOrgElementId(String id) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);
        this.deleteByHQL("delete from BizOrgElementRoleRelaEntity where bizOrgElementId=:id", params);
    }
}
