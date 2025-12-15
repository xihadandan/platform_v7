package com.wellsoft.pt.org.service;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.org.dao.impl.OrgElementRoleRelaDaoImpl;
import com.wellsoft.pt.org.entity.OrgElementRoleRelaEntity;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年02月01日   chenq	 Create
 * </pre>
 */
public interface OrgElementRoleRelaService extends JpaService<OrgElementRoleRelaEntity, OrgElementRoleRelaDaoImpl, Long> {

    void saveOrgElementRoleRela(Long orgElementUuid, List<String> roleUuids, Long orgVersionUuid);

    List<OrgElementRoleRelaEntity> listByOrgVersionUuid(Long orgVersionUuid);

    void deleteByOrgVersionUuid(Long orgVersionUuid);

    void deleteByOrgElementIdsAndOrgVersionUuid(List<String> orgElementIds, Long orgVersionUuid);

    void deleteByRoleUuid(String roleUuid);

    List<OrgElementRoleRelaEntity> listByOrgElementUuid(Long orgElementUuid);

    List<QueryItem> queryRoleAndUserPaths(String userId);

    void deleteOrgElementRoleRelaByIdsAndRoleUuid(List<String> orgEleIds, String roleUuid, Long orgVersionUuid);
}
