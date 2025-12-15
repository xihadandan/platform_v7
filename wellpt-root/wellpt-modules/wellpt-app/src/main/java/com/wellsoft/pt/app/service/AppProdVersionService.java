package com.wellsoft.pt.app.service;

import com.wellsoft.pt.app.dao.AppProdVersionDao;
import com.wellsoft.pt.app.dto.AppProdVersionDto;
import com.wellsoft.pt.app.entity.*;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.security.audit.entity.Privilege;
import com.wellsoft.pt.security.audit.entity.Role;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年07月28日   chenq	 Create
 * </pre>
 */
public interface AppProdVersionService extends JpaService<AppProdVersionEntity, AppProdVersionDao, Long> {

    AppProdVersionEntity saveAsNewVersion(Long fromUuid, String version, String prodId);

    void updateStatus(Long uuid, AppProdVersionEntity.Status status);

    List<AppProdVersionEntity> queryLatestPubVersions(List<String> prodIds);

    Long saveVersion(AppProdVersionDto versionDto);

    List<AppModule> getVersionModulesByVersionUuid(Long prodVersionUuid);

    AppProdVersionEntity queryLatestPubVersion(String prodId);

    List<AppProdVersionEntity> getAllByProdId(String prodId);

    List<Role> getVersionRolesByVersionId(String prodVersionId);

    List<Privilege> getVersionPrivilegesByVersionId(String prodVersionId);

    void deleteByProdId(String prodId);

    void deleteVersion(Long uuid);

    Long saveProdVersionLog(AppProdVersionDto versionDto);

    AppProdVersionEntity queryLatestCreate(String prodId);

    List<AppProdVersionEntity> queryLatestCreateVersions(List<String> prodIds);

    AppProductVersionLogEntity getProdVersionLog(Long prodVersionUuid);

    AppProdVersionEntity queryEarliestCreateVersion(String prodId);

    List<AppModule> getVersionModulesByVersionId(String prodVersionId);

    List<AppProdModuleEntity> getModulesByProdVersionUuid(Long prodVersionUuid);

    List<AppPageDefinition> getProdVersionPages(Long prodVersionUuid);

    void deleteProdVersionPage(String pageId, Long prodVersionUuid);


    void updateProdVersionPage(Long prodVersionUuid, String pageFormUuid, String pageToUuid);

    String getProdVersionPageUuid(Long prodVersionUuid, String pageId);

    Long saveVersionLoginDef(AppProdVersionLoginEntity data);

    void deleteVersionLoginDef(List<Long> uuids);

    List<AppProdVersionLoginEntity> queryProdVersionLoginByProdVersionUuid(Long prodVersionUuid);

    AppProdVersionLoginEntity getDefaultLoginDef(Long prodVersionUuid, Boolean isPc);

    AppProdVersionLoginEntity getLoginDef(Long loginUuid);

    AppProdVersionEntity getByVersionAndProdId(String version, String prodId);


    String getProdVersionPageTheme(Long prodVersionUuid, String pageId);

    void updateProdVersionRelaPageTheme(Long prodVersionUuid, List<AppProdRelaPageEntity> pages);

    List<AppProdRelaPageEntity> getProdVersionRelaPage(Long prodVersionUuid);

    List<AppProdVersionEntity> listProdVersionByIds(List<String> id);

    AppProdVersionEntity getProdVersionByVersionId(String prodVersionId);

    void setDefaultLoginDef(Long uuid);

    boolean isProdVersionPage(String pageUuid);

    List<String> getProdVersionAuthenticatePageUuids(Long prodVersionUuid, String prodId);

    AppProdVersionDto getProdVersionDetails(Long prodVersionUuid);

    List<AppPageDefinition> getPageInfosIgnoreDefinitionUnderProdVersion(Long prodVersionUuid);

    void removeUnusedProdModuleNestedRole(Long prodVersionUuid, List<String> moduleIds);
}
