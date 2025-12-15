package com.wellsoft.pt.app.service;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.app.dao.impl.AppUserWidgetDefDaoImpl;
import com.wellsoft.pt.app.entity.AppUserWidgetDefEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

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
public interface AppUserWidgetDefService extends
        JpaService<AppUserWidgetDefEntity, AppUserWidgetDefDaoImpl, String> {

    void saveUserWidgetDef(String userId, String widgetId, String json, AppUserWidgetDefEntity.Type type);

    String getUserWidgetDefition(String currentUserId, String widgetId);

    List<AppUserWidgetDefEntity> getUserWidgetDefition(String currentUserId, AppUserWidgetDefEntity.Type type);

    List<AppUserWidgetDefEntity> getWidgetDefinitionByType(AppUserWidgetDefEntity.Type type);

    List<AppUserWidgetDefEntity> getWidgetDefinitionByTypeAndAppId(AppUserWidgetDefEntity.Type type, String appId);


    List<AppUserWidgetDefEntity> pageQueryDefTypeAppWidgets(String keyword, AppUserWidgetDefEntity.Type type, List<String> appId, PagingInfo page);

    void saveUserWidgetDef(AppUserWidgetDefEntity body);

    void updateEnabled(String uuid, boolean enabled);
}
