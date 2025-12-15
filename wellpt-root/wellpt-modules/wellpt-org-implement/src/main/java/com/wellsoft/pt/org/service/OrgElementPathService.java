package com.wellsoft.pt.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.org.dao.impl.OrgElementPathDaoImpl;
import com.wellsoft.pt.org.entity.OrgElementPathEntity;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月23日   chenq	 Create
 * </pre>
 */
public interface OrgElementPathService extends JpaService<OrgElementPathEntity, OrgElementPathDaoImpl, Long> {

    OrgElementPathEntity getByOrgEleUuid(Long orgElementUuid);

    OrgElementPathEntity getByOrgEleId(String orgElementId);

    List<OrgElementPathEntity> listByOrgEleIds(List<String> orgElementIds);

    void deleteByIdsAndOrgVersionUuid(List<String> ids, Long orgVersionUuid);

    List<OrgElementPathEntity> listByOrgVersionUuid(Long uuid);

    void deleteByOrgVersionUuid(Long orgVersionUuid);

    List<OrgElementPathEntity> listByOrgElementIdsAndOrgVersionUuid(List<String> orgElementIds, Long orgVersionUuid);

    OrgElementPathEntity getByIdPathAndOrgVersionUuid(String idPath, Long orgVersionUuid);

    OrgElementPathEntity getByOrgElementIdAndOrgVersionUuid(String orgElementId, Long orgVersionUuid);

    OrgElementPathEntity getByOrgElementIdAndOrgVersionUuid(String orgElementId, Long orgVersionUuid, String locale);

    String getLocaleOrgElementPath(String orgElementId, Long orgVersionUuid, String locale);

    List<OrgElementPathEntity> getOrgUserElementPaths(String userId, List<Long> orgVersionUuids);

    List<OrgElementPathEntity> getOrgElementPathLikeSuffixPath(String suffixPath, Long orgVersionUuid);

    String getLocaleOrgElementPath(Long orgElementUuid, String locale);

    Map<Long, String> getLocaleOrgElementPaths(List<Long> orgElementUuids, String locale);

}
