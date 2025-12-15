package com.wellsoft.pt.org.dao.impl;

import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.org.dao.OrgUnitExtAttrDao;
import com.wellsoft.pt.org.entity.OrgUnitExtAttrEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

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
public class OrgUnitExtAttrDaoImpl extends AbstractJpaDaoImpl<OrgUnitExtAttrEntity, Long> implements OrgUnitExtAttrDao {
    @Override
    public List<OrgUnitExtAttrEntity> getByOrgUnitId(String orgUnitId) {
        return this.listByFieldEqValue("orgUnitId", orgUnitId);
    }

    @Override
    public List<OrgUnitExtAttrEntity> getByOrgUnitUuid(Long orgUnitUuid) {
        return this.listByFieldEqValue("orgUnitUuid", orgUnitUuid);

    }
}
