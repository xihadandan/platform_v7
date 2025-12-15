package com.wellsoft.pt.org.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.org.support.dataprovider.OrgSelectProvider;
import com.wellsoft.pt.org.dao.impl.BizOrgElementDaoImpl;
import com.wellsoft.pt.org.dto.BizOrgElementDto;
import com.wellsoft.pt.org.entity.BizOrgElementEntity;
import com.wellsoft.pt.org.entity.BizOrgElementPathChainEntity;
import com.wellsoft.pt.org.entity.BizOrgElementPathEntity;

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
 * 2024年11月28日   chenq	 Create
 * </pre>
 */
public interface BizOrgElementService extends JpaService<BizOrgElementEntity, BizOrgElementDaoImpl, Long> {

    BizOrgElementEntity saveBizOrgElement(BizOrgElementDto dto);

    void deleteBizOrgElementById(String id);

    BizOrgElementEntity getById(String id);


    List<BizOrgElementEntity> getAllByBizOrgUuid(Long bizOrgUuid);


    Map<String, String> getNamesByIds(Set<String> ids);

    void deleteBizOrgElementByUuid(Long uuid);

    List<BizOrgElementPathEntity> getAllSubOrgElementPath(String bizOrgElementId);

    /**
     * 获取业务节点关联的权限角色UUID
     *
     * @param bizOrgElementId
     * @return
     */
    List<String> getBizOrgElementRelaRoleUuids(String bizOrgElementId);


    List<TreeNode> getBizOrgElementRolePrivilegeTree(String bizOrgElementId);


    void deleteOrgElementPathChainByBizOrgElementIds(Set<String> bizOrgEleIds);

    void saveBizOrgElementPathChains(List<BizOrgElementPathChainEntity> pathChainEntities);

    void saveBizOrgElementPaths(List<BizOrgElementPathEntity> pathEntities);

    void updateAllBizOrgDimensionElementType(String bizOrgDimensionId, Long bizOrgUuid);

    List<OrgSelectProvider.Node> getTreeByBizOrgUuid(Long bizOrgUuid, OrgSelectProvider.Params params);

    List<BizOrgElementEntity> getAllBizDimElementsByBizOrgUuid(Long uuid);

    List<BizOrgElementEntity> getBizOrgElementByParentUuid(Long parentUuid);

    List<BizOrgElementEntity> getBizOrgElementByParentDimensionUuid(Long uuid);

    void deleteAllBizOrgElementPathByBizOrgUuid(Long bizOrgUuid);

    OrgSelectProvider.PageNode getTreeUserNodesByBizOrgUuid(Long bizOrgUuid, OrgSelectProvider.Params params);

    List<BizOrgElementPathEntity> getElementPathsByBizOrgElementIds(Set<String> bizOrgEleIds);

    Set<String> getBizOrgElementAuthRoleUuidsByUserId(String userId);

    /**
     * 根据用户ID，获取用户资源维度的节点
     *
     * @param userId
     * @param isDimension
     * @param bizOrgIds
     * @return
     */
    // List<BizOrgElementEntity> listByUserId(String userId, boolean isDimension, String[] bizOrgIds);

    /**
     * 根据用户ID、元素类型，获取用户归属的节点
     *
     * @param userId
     * @param elementTypes
     * @param bizOrgIds
     * @return
     */
    List<BizOrgElementEntity> listByUserIdAndElementTypes(String userId, List<String> elementTypes, String[] bizOrgIds);

    /**
     * 根据用户ID，获取用户归属的上级节点
     *
     * @param userId
     * @param bizOrgIds
     * @return
     */
    List<BizOrgElementEntity> listParentByUserId(String userId, String[] bizOrgIds);

    /**
     * 根据用户ID、元素类型，获取用户归属的上级节点
     *
     * @param userId
     * @param elementTypes
     * @param bizOrgIds
     * @return
     */
    List<BizOrgElementEntity> listParentByUserIdAndElementTypes(String userId, List<String> elementTypes, String[] bizOrgIds);

    /**
     * 根据用户ID，获取用户归属的节点路径
     *
     * @param userId
     * @param bizOrgIds
     * @return
     */
    List<String> listIdPathsByUserId(String userId, String[] bizOrgIds);

    /**
     * 根据元素ID列表，获取节点
     *
     * @param ids
     * @return
     */
    List<BizOrgElementEntity> listByIds(List<String> ids);

    void resortOrgElements(List<Long> uuids);

    void resetBizOrgElementPathChains(Long bizOrgUuid);

    Long countByIdAndBizOrgId(String id, String bizOrgId);

    boolean containsBizOrgElement(List<String> bizOrgElementIds, String subBizOrgElementId, String bizOrgId);

    String getLocaleBizElementPathById(String bizOrgElementId, String locale);

    Map<String, String> getNamePathsByIds(List<String> bizIds);

    void translateAllElements(Long bizOrgUuid, Boolean onlyTranslateEmpty);
}
