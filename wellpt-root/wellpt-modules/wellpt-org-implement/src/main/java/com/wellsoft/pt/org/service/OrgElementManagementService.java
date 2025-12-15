package com.wellsoft.pt.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.org.dao.impl.OrgElementManagementDaoImpl;
import com.wellsoft.pt.org.entity.OrgElementManagementEntity;

import java.util.Collection;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月24日   chenq	 Create
 * </pre>
 */
public interface OrgElementManagementService extends JpaService<OrgElementManagementEntity, OrgElementManagementDaoImpl, Long> {
    List<OrgElementManagementEntity> listByOrgVersionUuid(Long orgVersionUuid);

    OrgElementManagementEntity getByOrgElementUuid(Long orgElementUuid);

    void deleteByOrgVersionUuid(Long orgVersionUuid);

    void deleteByOrgElementIdsAndOrgVersionUuid(List<String> orgElementIds, Long orgVersionUuid);

    List<OrgElementManagementEntity> listByElementIds(Collection<String> orgElementIds, Long[] orgVersionUuids);

    List<OrgElementManagementEntity> listByDirector(String director, String[] orgVersionIds);

    List<OrgElementManagementEntity> listByLeader(String leader, String[] orgVersionIds);

    /**
     * 根据分管领导ID，获取分管的组织ID路径
     *
     * @param leader
     * @param orgVersionIds
     * @return
     */
    List<String> listOrgElementPathByLeader(String leader, String[] orgVersionIds);

    boolean isOrgElementManager(String userId, Long orgUuid);

    List<OrgElementManagementEntity> listUserOrgElementManagement(String userId, Long orgUuid);
}
