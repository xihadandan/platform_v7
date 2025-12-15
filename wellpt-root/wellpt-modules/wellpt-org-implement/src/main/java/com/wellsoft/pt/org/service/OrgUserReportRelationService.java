package com.wellsoft.pt.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.org.dao.impl.OrgUserReportRelationDaoImpl;
import com.wellsoft.pt.org.entity.OrgUserReportRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年12月01日   chenq	 Create
 * </pre>
 */
public interface OrgUserReportRelationService extends JpaService<OrgUserReportRelationEntity, OrgUserReportRelationDaoImpl, Long> {

    List<OrgUserReportRelationEntity> listByOrgVersionUuid(Long orgVersionUuid);

    void deleteByOrgVersionUuid(Long orgVersionUuid);

    void saveUserReportRelation(String userId, Map<String, List<String>> orgElementIdReport, Long orgVersionUuid);

    void deleteByOrgVersionUuidAndUserId(String userId, Long orgVersionUuid);

    void deleteByOrgVersionUuidAndReportToUserId(String reportToUserId, Long orgVersionUuid);

    List<OrgUserReportRelationEntity> listByOrgVersionUuidAndUserId(Long orgVersionUuid, String userId);

    List<OrgUserReportRelationEntity> listByOrgVersionIdsAndReportToUserId(String[] orgVersionIds, String userId);

    List<OrgUserReportRelationEntity> listByOrgVersionIdsAndUserId(String[] orgVersionIds, String userId);

    List<OrgUserReportRelationEntity> listByOrgVersionIdsAndUserIdAndOrgElementIds(String[] orgVersionIds, String userId, List<String> eleIds);
}
