package com.wellsoft.pt.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.org.dao.impl.OrgPartnerSysApplyDaoImpl;
import com.wellsoft.pt.org.entity.OrgPartnerSysApplyEntity;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年02月07日   chenq	 Create
 * </pre>
 */
public interface OrgPartnerSysApplyService extends JpaService<OrgPartnerSysApplyEntity, OrgPartnerSysApplyDaoImpl, Long> {

    Long addOrgPartnerSysApply(OrgPartnerSysApplyEntity temp);

    void updateOrgPartnerSysApplyState(Long uuid, OrgPartnerSysApplyEntity.State state);


    void updateCategoryIsNullByCategoryUuid(Long categoryUuid);

    long countByStateAndSystemAndTenant(OrgPartnerSysApplyEntity.State state, String system, String tenant);
}
