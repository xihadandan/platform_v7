package com.wellsoft.pt.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.org.dao.impl.OrgPartnerSysOrgDaoImpl;
import com.wellsoft.pt.org.entity.OrgPartnerSysOrgEntity;

import java.util.List;

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
public interface OrgPartnerSysOrgService extends JpaService<OrgPartnerSysOrgEntity, OrgPartnerSysOrgDaoImpl, Long> {


    List<OrgPartnerSysOrgEntity> listBySystem(String system);
}
