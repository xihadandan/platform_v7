package com.wellsoft.pt.app.facade.service.impl;

import com.wellsoft.pt.app.entity.AppUserWidgetDefEntity;
import com.wellsoft.pt.app.facade.service.AppUserWidgetDefFacadeService;
import com.wellsoft.pt.app.service.AppUserWidgetDefService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/27
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/27    chenq		2019/8/27		Create
 * </pre>
 */
@Service
public class AppUserWidgetDefFacadeServiceImpl implements AppUserWidgetDefFacadeService {

    @Autowired
    AppUserWidgetDefService appUserWidgetDefService;


    @Override
    public void saveCurrentUserWidgetDefinition(String widgetId, String configuraion) {
        appUserWidgetDefService.saveUserWidgetDef(SpringSecurityUtils.getCurrentUserId(), widgetId,
                configuraion, AppUserWidgetDefEntity.Type.WIDGET);
    }

    @Override
    public String getCurrentUserWidgetDefintion(String widgetId) {
        return appUserWidgetDefService.getUserWidgetDefition(SpringSecurityUtils.getCurrentUserId(),
                widgetId);
    }
}
