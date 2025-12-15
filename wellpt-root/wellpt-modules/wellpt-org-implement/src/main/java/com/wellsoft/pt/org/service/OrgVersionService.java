package com.wellsoft.pt.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.org.dao.impl.OrgVersionDaoImpl;
import com.wellsoft.pt.org.dto.OrgVersionDto;
import com.wellsoft.pt.org.entity.OrgVersionEntity;
import com.wellsoft.pt.org.query.OrgVersionQueryItem;

import java.util.Date;
import java.util.List;
import java.util.Set;

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
public interface OrgVersionService extends JpaService<OrgVersionEntity, OrgVersionDaoImpl, Long> {

    Long saveCopyByUuid(Long uuid, boolean copyUser);

    void updatePublished(Long uuid);

    OrgVersionEntity getByStateAndOrgUuid(OrgVersionEntity.State state, Long orgUuid);

    void updatePublishTime(Long uuid, Date date);

    void deleteOrgVersion(Long uuid);

    OrgVersionDto getDetailsByUuid(Long uuid);

    void deleteAllByOrgUuid(Long uuid);

    OrgVersionEntity getById(String orgVersionId);

    /**
     * 获取默认组织使用的组织版本
     *
     * @param system
     * @return
     */
    OrgVersionEntity getDefaultOrgVersionBySystem(String system);

    /**
     * 获取指定组织使用的组织版本
     *
     * @param orgId
     * @return
     */
    OrgVersionEntity getOrgVersionByOrgId(String orgId);

    /**
     * 获取指定组织使用的组织版本
     *
     * @param orgUuid
     * @return
     */
    OrgVersionEntity getOrgVersionByOrgUuid(Long orgUuid);

    /**
     * 根据组织版本ID，获取最新的组织版本ID
     *
     * @param orgVersionId
     * @return
     */
    OrgVersionEntity getLatestOrgVersionByOrgVersionId(String orgVersionId);

    /**
     * 根据组织ID列表，获取组织版本列表
     *
     * @param orgIds
     * @return
     */
    List<OrgVersionEntity> listByOrgIds(List<String> orgIds);

    /**
     * 根据组织ID列表、归属系统，获取组织版本列表
     *
     * @param orgIds
     * @param system
     * @return
     */
    List<OrgVersionEntity> listByOrgIdsAndSystem(List<String> orgIds, String system);

    /**
     * 根据组织版本ID列表，获取组织版本列表
     *
     * @param ids
     * @return
     */
    List<OrgVersionEntity> listByIds(Set<String> ids);


    List<OrgVersionEntity> listAllByOrgUuid(Long orgUuid);

    /**
     * 根据组织版本ID列表，获取组织版本信息列表
     *
     * @param orgVersionIds
     * @return
     */
    List<OrgVersionQueryItem> listItemByOrgVersionIds(List<String> orgVersionIds);

    List<OrgVersionEntity> listAllByOrgId(String orgId);

    /**
     * 获取系统可使用的组织版本
     *
     * @param system
     * @return
     */
    List<OrgVersionEntity> listPublishedBySystemAndTenant(String system, String tenant);

    /**
     * 从组织版本中创建空的组织版本
     *
     * @param fromVersion
     * @param copyUser
     * @return
     */
    OrgVersionEntity createEmptyOrgVersionFromOrgVersion(OrgVersionEntity fromVersion, boolean copyUser);

    List<OrgVersionEntity> getAllPublishedVersionBySystem(String system);

    void batchUpdatePublish(Date date);
}
