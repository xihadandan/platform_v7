package com.wellsoft.pt.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.org.dao.impl.OrgUnitDaoImpl;
import com.wellsoft.pt.org.dto.OrgUnitDto;
import com.wellsoft.pt.org.entity.OrgUnitEntity;
import com.wellsoft.pt.org.entity.OrganizationEntity;

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
public interface OrgUnitService extends JpaService<OrgUnitEntity, OrgUnitDaoImpl, Long> {
    Long saveOrgUnit(OrgUnitDto orgUnitDto);

    void enable(long uuid, Boolean enable);

    boolean deleteByUuid(Long uuid);

    OrgUnitEntity getById(String id);

    OrgUnitDto getOrgUnitDetailsByUuid(Long uuid);

    List<OrgUnitEntity> listBySystemAndTenant(String system, String tenant);

    /**
     * 根据单位UUID获取被使用的组织
     *
     * @param uuid
     * @return
     */
    List<OrganizationEntity> listUsedOrganizationByUuid(Long uuid);
}
