package com.wellsoft.pt.app.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.app.dao.AppSystemInfoDao;
import com.wellsoft.pt.app.dto.AppSystemInfoDto;
import com.wellsoft.pt.app.entity.*;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年01月16日   chenq	 Create
 * </pre>
 */
public interface AppSystemInfoService extends JpaService<AppSystemInfoEntity, AppSystemInfoDao, Long> {
    Long saveSystemInfo(AppSystemInfoEntity body);

    AppSystemInfoDto getSystemInfoWithLayoutThemeByTenantAndSystem(String tenant, String system);

    Long saveSystemPageSetting(AppSystemPageSettingEntity body);

    AppSystemPageSettingEntity getSystemPageSetting(String tenant, String system);

    List<AppSystemLoginPolicyEntity> getTenantSystemLoginPolicies(String tenant, String system);

    void saveTenantSystemLoginPolicies(List<AppSystemLoginPolicyEntity> list);

    void updateAppSystemPageSettingLayoutConf(String layoutConf, Boolean userLayoutDefinable, Long uuid);

    void updateAppSystemPageSettingThemeStyle(String themeStyle, Boolean userThemeDefinable, Long uuid);

    void saveAppSystemPageTheme(String system, String theme, List<AppSystemPageThemeEntity> pageThemes, Boolean userThemeDefinable);

    void saveAppSystemParam(AppSystemParamEntity param);

    void deleteAppSystemParam(List<Long> uuid);

    String getAppSystemParam(String key, String system);

    AppSystemParamEntity getAppSystemParamByKeyAndSystem(String key, String system);

    List<AppSystemParamEntity> listAllAppSystemParam();

    Boolean checkAppSystemParamPropKeyExist(String propKey, String system);

    Boolean enableUserLayoutDefinable(String tenant, String system);

    Boolean enableUserThemeDefinable(String tenant, String system);

    List<AppPageDefinition> systemAuthenticatePage(String system, String tenant);

    String getSystemPageTheme(String pageId, String tenant, String system);

    List<AppSystemPageThemeEntity> getAppSystemPageThemesBySystem(String system, String tenant);

    List<AppPageDefinition> queryAppSystemPages(String tenant, String system);

    TreeNode querySystemUnderControllableResourceTree(String system, String tenant);

    AppSystemInfoDto getSystemInfoWithProdVersion(String tenant, String system);

    List<AppModule> queryAppSystemModules(String tenant, String system);

    void createSystemPageAndModuleDefaultRolePvg(String system, String tenant);

    void createAppSystemParamsFromAppProdVersion(String system);
}
