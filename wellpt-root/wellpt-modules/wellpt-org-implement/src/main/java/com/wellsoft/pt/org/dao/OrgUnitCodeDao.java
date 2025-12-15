package com.wellsoft.pt.org.dao;

import com.wellsoft.pt.jpa.dao.JpaDao;
import com.wellsoft.pt.org.entity.OrgUnitCodeEntity;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月16日   chenq	 Create
 * </pre>
 */
public interface OrgUnitCodeDao extends JpaDao<OrgUnitCodeEntity, Long> {
    List<OrgUnitCodeEntity> getByOrgUnitUuid(Long orgUnitUuid);

    List<OrgUnitCodeEntity> getByOrgUnitId(String orgUnitId);
}
