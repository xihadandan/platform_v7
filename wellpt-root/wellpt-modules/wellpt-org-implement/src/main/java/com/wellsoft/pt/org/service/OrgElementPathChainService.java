package com.wellsoft.pt.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.org.dao.impl.OrgElementPathChainDaoImpl;
import com.wellsoft.pt.org.entity.OrgElementPathChainEntity;

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
public interface OrgElementPathChainService extends JpaService<OrgElementPathChainEntity, OrgElementPathChainDaoImpl, Long> {

    /**
     * 获取所有上级路径
     *
     * @param orgVersionUuid
     * @param orgElementId
     * @return
     */
    List<OrgElementPathChainEntity> getAllSuperiors(Long orgVersionUuid, String orgElementId);

    /**
     * 获取最近的上级
     *
     * @param subOrgElementId
     * @param orgElementType  上级类型
     * @return
     */
    OrgElementPathChainEntity getNearestSuperior(String subOrgElementId, String orgElementType, Long orgVersionUuid);


    /**
     * 获取所有下级路径
     *
     * @param orgVersionUuid
     * @param orgElementId
     * @return
     */
    List<OrgElementPathChainEntity> getAllSubordinate(Long orgVersionUuid, String orgElementId);

    void deleteBySubOrgEleIdsAndOrgVersionUuid(List<String> subOrgEleIds, Long orgVersionUuid);

    void deleteByOrgEleIdsAndOrgVersionUuid(List<String> ids, Long orgVersionUuid);

    List<OrgElementPathChainEntity> listByOrgVersionUuid(Long orgVersionUuid);

    void deleteByOrgVersionUuid(Long orgVersionUuid);

    void saveOrUpdatePathChainByOrgElementUuid(Long orgElementUuid, Long newParentUuid, Long oldParentUuid, String oldElementName);

    /**
     * 判断是否包含指定的组织元素
     *
     * @param orgElementIds
     * @param subOrgElementId
     * @param orgVersionIds
     * @return
     */
    boolean containsOrgElement(List<String> orgElementIds, String subOrgElementId, String[] orgVersionIds);
}
