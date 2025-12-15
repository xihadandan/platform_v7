package com.wellsoft.pt.app.service;

import com.wellsoft.pt.app.dao.AppSystemLoginPageDefinitionDao;
import com.wellsoft.pt.app.dto.AppSystemLoginPageDefinitionDto;
import com.wellsoft.pt.app.entity.AppSystemLoginPageDefinitionEntity;
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
public interface AppSystemLoginPageDefinitionService extends JpaService<AppSystemLoginPageDefinitionEntity, AppSystemLoginPageDefinitionDao, Long> {

    List<AppSystemLoginPageDefinitionEntity> getAllLoginPage(String tenant, String system);

    boolean enableLoginPage(Long uuid, boolean enabled);

    void updateLoginPageDefJson(String defJson, Long uuid);

    void setDefaultLoginDef(Long uuid);

    void updateLoginPageWithoutDefJson(AppSystemLoginPageDefinitionEntity body);

    AppSystemLoginPageDefinitionDto getEnableTenantSystemLoginPagePolicy(String tenant, String system);

    List<AppSystemLoginPageDefinitionEntity> getAllProdVersionLoginPage(Long prodVersionUuid);

    List<AppSystemLoginPageDefinitionEntity> getAllEnabledLoginPage();

    Long copyLoginPage(Long uuid);
}
