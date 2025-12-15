package com.wellsoft.pt.app.ui.client.widget;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.app.support.AppFunctionType;
import com.wellsoft.pt.app.ui.client.DefaultWidgetDefinitionProxyView;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StepNavWidget extends DefaultWidgetDefinitionProxyView {

    private static final String CONFIGURATION = "configuration";
    private static final String BUTTONS = "buttons";
    private static final String RESOURCE = "resource";
    private static final String ID = "id";
    private static final String EVENT_HANDLER = "eventHandler";

    /**
     * 如何描述该构造方法
     *
     * @param widgetDefinition
     * @throws Exception
     */
    public StepNavWidget(String widgetDefinition) throws Exception {
        super(widgetDefinition);
    }

    /**
     * (non-Javadoc)
     *
     * @throws JSONException
     * @see com.wellsoft.pt.app.ui.client.DefaultWidgetDefinitionProxyView#getDefinitionJson()
     */
    @Override
    public String getDefinitionJson() throws Exception {
        if (!jsonObject.has(CONFIGURATION) || !jsonObject.getJSONObject(CONFIGURATION).has(BUTTONS)) {
            return this.jsonObject.toString();
        }
        JSONObject configuration = jsonObject.getJSONObject(CONFIGURATION);
        if (!configuration.has(BUTTONS)) {
            return this.jsonObject.toString();
        }
        SecurityApiFacade securityApiFacade = ApplicationContextHolder.getBean(SecurityApiFacade.class);
        JSONArray buttons = configuration.getJSONArray(BUTTONS);
        JSONArray newButtons = new JSONArray();
        for (int i = 0; i < buttons.length(); i++) {
            JSONObject button = buttons.getJSONObject(i);
            if (button.has(RESOURCE) && button.getJSONObject(RESOURCE).has(ID)) {
                String resourceId = button.getJSONObject(RESOURCE).optString(ID, "");
                if (!securityApiFacade.isGranted(resourceId, AppFunctionType.AppProductIntegration)) {
                    continue;
                }
            }

            if (button.has(EVENT_HANDLER) && button.getJSONObject(EVENT_HANDLER).has(ID)) {
                String eventHandlerId = button.getJSONObject(EVENT_HANDLER).optString(ID, "");
                if (!securityApiFacade.isGranted(eventHandlerId, AppFunctionType.AppProductIntegration)) {
                    continue;
                }
            }
            newButtons.put(button);
        }
        configuration.remove(BUTTONS);
        configuration.put(BUTTONS, newButtons);
        return jsonObject.toString();

    }
}