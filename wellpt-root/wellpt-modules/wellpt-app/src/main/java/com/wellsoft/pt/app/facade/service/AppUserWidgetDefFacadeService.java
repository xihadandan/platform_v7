package com.wellsoft.pt.app.facade.service;

import com.wellsoft.context.service.Facade;

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
public interface AppUserWidgetDefFacadeService extends Facade {

    void saveCurrentUserWidgetDefinition(String widgetId, String configuraion);

    String getCurrentUserWidgetDefintion(String widgetId);
}
