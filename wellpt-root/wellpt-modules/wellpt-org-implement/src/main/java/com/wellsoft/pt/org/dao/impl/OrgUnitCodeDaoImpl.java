package com.wellsoft.pt.org.dao.impl;

import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.org.dao.OrgUnitCodeDao;
import com.wellsoft.pt.org.entity.OrgUnitCodeEntity;
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
public class OrgUnitCodeDaoImpl extends AbstractJpaDaoImpl<OrgUnitCodeEntity, Long> implements OrgUnitCodeDao {
    @Override
    public List<OrgUnitCodeEntity> getByOrgUnitUuid(Long orgUnitUuid) {
        return listByFieldEqValue("orgUnitUuid", orgUnitUuid);
    }

    @Override
    public List<OrgUnitCodeEntity> getByOrgUnitId(String orgUnitId) {
        return listByFieldEqValue("orgUnitId", orgUnitId);
    }
}
