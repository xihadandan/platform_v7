package com.wellsoft.pt.org.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.org.dao.impl.OrgPartnerSysOrgDaoImpl;
import com.wellsoft.pt.org.entity.OrgPartnerSysOrgEntity;
import com.wellsoft.pt.org.service.OrgPartnerSysOrgService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年02月09日   chenq	 Create
 * </pre>
 */
@Service
public class OrgPartnerSysOrgServiceImpl extends AbstractJpaServiceImpl<OrgPartnerSysOrgEntity, OrgPartnerSysOrgDaoImpl, Long> implements OrgPartnerSysOrgService {
    @Override
    public List<OrgPartnerSysOrgEntity> listBySystem(String system) {
        return this.dao.listByFieldEqValue("system", system);
    }
}
