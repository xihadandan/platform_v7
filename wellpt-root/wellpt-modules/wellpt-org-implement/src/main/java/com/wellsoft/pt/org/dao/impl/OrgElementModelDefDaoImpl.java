package com.wellsoft.pt.org.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.org.dao.OrgElementModelDefDao;
import com.wellsoft.pt.org.entity.OrgElementModelDefEntity;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
public class OrgElementModelDefDaoImpl extends AbstractJpaDaoImpl<OrgElementModelDefEntity, Long> implements OrgElementModelDefDao {
    @Override
    public OrgElementModelDefEntity getByIdAndSystem(String id, String system) {
        OrgElementModelDefEntity example = new OrgElementModelDefEntity();
        example.setOrgElementModelId(id);
        if (StringUtils.isNotBlank(system)) {
            example.setSystem(system);
            example.setTenant(SpringSecurityUtils.getCurrentTenantId());
        }
        List<OrgElementModelDefEntity> list = this.listByEntity(example);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    @Override
    public OrgElementModelDefEntity getByIdAndNullSystem(String id) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("id", id);
        return this.getOneByHQL("from OrgElementModelDefEntity where orgElementModelId=:id and system is null", param);
    }

    @Override
    @Transactional
    public void deleteByIdAndSystem(String id, String system) {
        OrgElementModelDefEntity defEntity = getByIdAndSystem(id, system);
        if (defEntity != null) {
            delete(defEntity);
        }
    }
}
