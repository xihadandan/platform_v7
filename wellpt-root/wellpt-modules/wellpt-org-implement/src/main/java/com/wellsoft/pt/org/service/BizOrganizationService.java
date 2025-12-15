package com.wellsoft.pt.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.org.dao.impl.BizOrganizationDaoImpl;
import com.wellsoft.pt.org.dto.BizOrgConfigDto;
import com.wellsoft.pt.org.entity.*;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年11月25日   chenq	 Create
 * </pre>
 */
public interface BizOrganizationService extends JpaService<BizOrganizationEntity, BizOrganizationDaoImpl, Long> {


    Long saveBizOrg(BizOrganizationEntity entity);

    int updateSyncLocked(Long uuid, Boolean locked);

    BizOrganizationEntity getById(String id);

    void deleteBizOrg(List<Long> uuids);

    Long saveBizOrgRoleTemplate(BizOrgRoleTemplateEntity templateEntity);

    Long saveBizOrgDimension(BizOrgDimensionEntity entity);

    void deleteBizOrgRoleTemplate(List<Long> uuids);

    void deleteBizOrgDimension(List<Long> uuids);

    void saveBizOrgConfig(BizOrgConfigDto dto);

    List<BizOrgDimensionEntity> getBizOrgDimensionsBySystemAndTenant(String system, String tenant);

    List<BizOrgRoleTemplateEntity> getBizOrgRoleTemplateBySystemAndTenant(String system, String tenant);

    List<BizOrgRoleEntity> getBizOrgRolesByBizOrgUuid(Long bizOrgUuid);

    List<BizOrgRoleEntity> getBizOrgRolesByBizOrgId(String bizOrgId);

    BizOrgConfigEntity getBizOrgConfigByBizOrgUuid(Long bizOrgUuid);

    BizOrgDimensionEntity getBizOrgDimensionByUuid(Long uuid);

    BizOrgDimensionEntity getBizOrgDimensionById(String id);

    BizOrgRoleEntity getBizOrgRoleById(String id, Long bizOrgUuid);

    List<BizOrgRoleEntity> getBizOrgRolesByIds(List<String> ids, Long bizOrgUuid);

    void syncBizOrg(Long uuid);

    List<BizOrganizationEntity> getBizOrgByOrgUuid(Long orgUuid);

    List<BizOrganizationEntity> getValidBizOrgByOrgUuid(Long orgUuid);

    List<BizOrganizationEntity> listBizOrgByOrgId(String orgId);

    List<BizOrganizationEntity> listValidBizOrgByOrgId(String orgId);

    void batchUpdateAllBizOrgExpiredExceedExpiredTime();

    Long getBizOrgUuidByBizOrgId(String bizOrgId);
}
