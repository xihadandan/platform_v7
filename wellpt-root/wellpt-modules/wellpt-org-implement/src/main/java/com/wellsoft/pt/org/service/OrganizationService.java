package com.wellsoft.pt.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.org.dao.impl.OrganizationDaoImpl;
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
public interface OrganizationService extends JpaService<OrganizationEntity, OrganizationDaoImpl, Long> {

    void enable(Long uuid, boolean enable);

    void setDefault(Long uuid, boolean isDefault);

    Long saveOrg(OrganizationEntity organizationEntity);

    void deleteOrg(Long uuid);


    List<OrganizationEntity> listEnabledBySystem(String system);

    List<OrganizationEntity> listEnabledBySystem(List<String> system);

    List<OrganizationEntity> listBySystem(String system);

    OrganizationEntity getDefaultOrgBySystem(String system);

    /**
     * 根据组织ID获取组织信息
     *
     * @param id
     * @return
     */
    OrganizationEntity getById(String id);

    /**
     * 根据业务组织ID获取组织信息
     *
     * @param bizOrgId
     * @return
     */
    OrganizationEntity getByBizOrgId(String bizOrgId);

    /**
     * 根据组织版本ID获取对应的组织
     *
     * @param orgVersionId
     * @return
     */
    OrganizationEntity getByOrgVersionId(String orgVersionId);

    /**
     * 根据组织版本UUID列表获取组织
     *
     * @param orgVersionIds
     * @return
     */
    List<OrganizationEntity> listByOrgVersionIds(List<String> orgVersionIds);

    /**
     * 根据业务组织ID列表获取组织ID列表
     *
     * @param bizOrgIds
     * @return
     */
    List<String> listIdByBizOrgIds(List<String> bizOrgIds);
}
