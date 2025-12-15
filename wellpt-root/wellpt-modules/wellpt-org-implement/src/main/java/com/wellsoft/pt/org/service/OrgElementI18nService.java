package com.wellsoft.pt.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.org.dao.OrgElementI18nDao;
import com.wellsoft.pt.org.entity.OrgElementI18nEntity;

import java.util.List;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2025年03月11日   chenq	 Create
 * </pre>
 */
public interface OrgElementI18nService extends JpaService<OrgElementI18nEntity, OrgElementI18nDao, Long> {

    OrgElementI18nEntity getOrgElementI18n(String dataId, String dataCode, String locale);

    OrgElementI18nEntity getOrgElementI18n(Long dataUuid, String dataCode, String locale);

    List<OrgElementI18nEntity> getOrgElementI18ns(Long dataUuid, String dataCode);

    List<OrgElementI18nEntity> getOrgElementI18ns(String dataId, String dataCode);

    List<OrgElementI18nEntity> getOrgElementI18nsByDataIds(List<String> dataId);

    List<OrgElementI18nEntity> getOrgElementI18nsByDataUuids(List<Long> dataUuid);


    void saveAllAfterDelete(String dataId, List<OrgElementI18nEntity> newList);

    void saveAllAfterDelete(Long dataUuid, List<OrgElementI18nEntity> newList);

    void deleteByDataUuid(Long dataUuid);

    void deleteByDataId(String dataId);

    List<OrgElementI18nEntity> getOrgElementI18ns(Long dataUuid);

    List<OrgElementI18nEntity> getOrgElementI18nsByDataUuidAndLocale(Long dataUuid, String locale);

    List<OrgElementI18nEntity> getOrgElementI18nsByDataUuidsAndLocale(List<Long> dataUuid, String locale);


    List<OrgElementI18nEntity> getOrgElementI18ns(String dataId);


    List<OrgElementI18nEntity> getOrgElementI18ns(Set<String> dataIds, String dataCode, String locale);

    List<OrgElementI18nEntity> listOrgElementI18ns(Set<Long> dataUuids, String dataCode, String locale);

}
