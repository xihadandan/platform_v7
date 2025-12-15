package com.wellsoft.pt.org.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.org.support.dataprovider.OrgSelectProvider;
import com.wellsoft.pt.org.dao.impl.OrgElementDaoImpl;
import com.wellsoft.pt.org.dto.OrgElementDto;
import com.wellsoft.pt.org.entity.OrgElementEntity;
import com.wellsoft.pt.org.entity.OrgElementExtAttrEntity;
import com.wellsoft.pt.org.entity.OrgElementI18nEntity;
import com.wellsoft.pt.org.entity.OrgVersionEntity;

import java.util.List;
import java.util.Map;
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
public interface OrgElementService extends JpaService<OrgElementEntity, OrgElementDaoImpl, Long> {

    Long saveOrgElement(OrgElementDto entity);

    void deleteOrgElement(Long uuid);

    List<OrgElementEntity> listByOrgVersionUuid(Long orgVersionUuid);

    List<OrgElementExtAttrEntity> listExtAttrsByOrgVersionUuid(Long orgVersionUuid);

    void saveExtAttrs(List<OrgElementExtAttrEntity> waitSaveExtAttrs);

    void deleteByOrgVersionUuid(Long uuid);

    void updateStateByOrgVersionUuid(OrgVersionEntity.State state, Long orgVersionUuid);

    OrgElementDto getDetails(Long uuid);

    List<OrgElementEntity> listByIdsAndOrgVersionId(String[] ids, Long orgVersionUuid);

    OrgElementEntity getByIdAndOrgVersionUuid(String orgElementId, Long orgVersionUuid);

    List<OrgElementEntity> listByParentUUid(Long parentUuid);

    List<OrgElementEntity> listByOrgVersionUuidAndParentUuidIsNull(Long orgVersionUuid);

    List<OrgSelectProvider.Node> getTree(String type, OrgSelectProvider.Params params);

    OrgSelectProvider.PageNode getTreeUserNodes(String type, OrgSelectProvider.Params params);

    List<OrgSelectProvider.Node> getTreeNodesByKeys(String type, OrgSelectProvider.Params params);

    Map<String, String> getNamesByIds(List<String> ids);

    Map<String, String> getNamePathsByIds(List<String> ids);

    /**
     * 通过组织元素ID,获取该节点下的所有职位ID和名称
     *
     * @param ids
     * @param orgVersionIds
     * @return
     */
    List<OrgElementEntity> listJobElementInIds(List<String> ids, String[] orgVersionIds);

    /**
     * 通过组织元素ID,获取该节点下的所有部门ID和名称
     *
     * @param ids
     * @param orgVersionIds
     * @return
     */
    List<OrgElementEntity> listDepartmentElementInIds(List<String> ids, String[] orgVersionIds);

    /**
     * 通过组织元素ID,获取该节点下的所有单位ID和名称
     *
     * @param ids
     * @param orgVersionIds
     * @return
     */
    List<OrgElementEntity> listUnitElementInIds(List<String> ids, String[] orgVersionIds);

    /**
     * 通过组织元素ID,获取该节点下的所有组织ID和名称
     *
     * @param ids
     * @param orgVersionIds
     * @return
     */
    List<OrgElementEntity> listOrgElementInIds(List<String> ids, String[] orgVersionIds);

    /**
     * 通过组织元素ID列表,获取组织元素
     *
     * @param ids
     * @param orgVersionIds
     * @return
     */
    List<OrgElementEntity> listElementByIds(List<String> ids, String[] orgVersionIds);

    List<TreeNode> getOrgElementRolePrivilegeTree(Long orgElementUuid);

    List<OrgElementEntity> getOrgElementsByTypesAndOrgVersionUuid(Long orgVersionUuid, List<String> type);

    boolean existOrgElementType(String type, String system);

    /**
     * 根据组织元素ID获取组织版本ID列表
     *
     * @param id
     * @return
     */
    List<String> listOrgVersionIdById(String id);


    /**
     * 根据权限角色删除组件元素角色信息
     *
     * @param roleUuid
     */
    void deleteElementRoleRelaByRoleUuid(String roleUuid);

    /**
     * 根据组织元素ID列表、角色UUID添加用户角色
     *
     * @param orgEleIds
     * @param roleUuid
     * @param orgVersionUuid
     */
    void addElementRoleRelaByIdsAndRoleUuid(List<String> orgEleIds, String roleUuid, Long orgVersionUuid);

    void addElementRoleRelaByUuidsAndRoleUuid(List<Long> orgEleUuids, String roleUuid);


    /**
     * 根据权限角色UUID、组织版本获取组织元素
     *
     * @param roleUuid
     * @param orgVersionUuid
     * @return
     */
    List<OrgElementEntity> listByRelaRoleUuidOrgVersionUuid(String roleUuid, Long orgVersionUuid);

    /**
     * 根据权限角色UUID获取组织元素
     *
     * @param roleUuid
     * @return
     */
    List<OrgElementEntity> listByRelaRoleUuid(String roleUuid);

    void deleteOrgElementRoleRelaByIdsAndRoleUuid(List<String> orgEleIds, String roleUuid, Long uuid);

    List<OrgElementEntity> getRoleOrgElementMemberByRoleSystemTenant(String roleUuid, String system, String tenant);

    OrgElementEntity getOrgElementByIdPublished(String id);

    List<OrgSelectProvider.Node> getTreeNodesByTitles(String type, OrgSelectProvider.Params params);

    List<OrgElementEntity> getOrgElementsByNameAndVersion(String name, Long uuid);

    List<OrgSelectProvider.Node> getTreeNodesByTypeKeys(OrgSelectProvider.Params params);

    List<OrgElementEntity> getAllPublishedByOrgUuid(Long orgUuid);

    Long countByIdAndOrgVersionUuid(String id, Long orgVersionUuid);

    void updateUnitOrgElementName(String name, String sourceId);

    void updateUnitOrgElementI18ns(String sourceId, List<OrgElementI18nEntity> i18ns);

    Map<String, Long> getUuidsByIds(String[] ids, Long orgVersionUuid);

    String getLocaleOrgElementName(String id, Long orgVersionUuid, String locale);

    String getLocalOrgElementName(Long orgElementUuid, String locale);

    String getLocalOrgElementShortName(Long orgElementUuid, String locale);

    void translateAllElements(Long orgVersionUuid, Boolean onlyTranslateEmpty);

    /**
     * 根据组织版本UUID获取组织元素数量
     *
     * @param orgVersionUuid
     * @return
     */
    long countByOrgVersionUuid(Long orgVersionUuid);

    void saveOrgElementLeader(Long orgElementUuid, String director, String leader, String manager);

    /**
     * @param orgElementUuid
     * @return
     */
    List<String> listRoleUuidByUuid(Long orgElementUuid);

    void updateOrgElementSeq(List<OrgElementDto> list);

    Set<String> getRelaRoleUuids(Long orgElementUuid);

    void updateOrgElementPathChain(Long orgUuid, Long orgVersionUuid);

    void updateOrgUserUnderOrgElement(Long orgElementUuid);
}
