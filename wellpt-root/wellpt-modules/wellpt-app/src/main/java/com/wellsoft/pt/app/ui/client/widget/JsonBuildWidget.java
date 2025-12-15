package com.wellsoft.pt.app.ui.client.widget;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.app.support.AppConstants;
import com.wellsoft.pt.app.support.AppFunctionType;
import com.wellsoft.pt.app.ui.WidgetConfiguration;
import com.wellsoft.pt.app.ui.client.DefaultWidgetDefinitionProxyView;
import com.wellsoft.pt.app.ui.client.widget.configuration.JsonBuildConfiguraion;
import com.wellsoft.pt.security.audit.facade.service.SecurityAuditFacadeService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年04月27日   chenq	 Create
 * </pre>
 */
public class JsonBuildWidget extends DefaultWidgetDefinitionProxyView {

    private static final String SECURITY_KEY = "_$security";
    private static final String SECURITY_ID_KEY = "id";
    private static final String SECURITY_CONTEXT_KEY = "context";
    private static final String SECURITY_TYPE_KEY = "type";


    public JsonBuildWidget(String widgetDefinition) throws Exception {
        super(widgetDefinition);
    }

    @Override
    public WidgetConfiguration getConfiguration() {
        return getConfiguration(JsonBuildConfiguraion.class);
    }

    @Override
    public String getDefinitionJson() throws Exception {
        String widgetDefinionJson = super.getDefinitionJson();
        if (jsonObject.has(AppConstants.KEY_CONFIGURATION)) {
            Object obj = jsonObject.get(AppConstants.KEY_CONFIGURATION);
            JSONObject configuration = (JSONObject) JSON.parse(obj.toString());
            /**
             *  权限规则节点
             *         "_$security": {
             *           "id": "$.buttons[0].id",   // jsonpath表达式或者权限资源值，如果是jsonpath表达式，没有设置context值情况下，
             *                                         会根据表达式计算上下文（适用于功能元素节点权限判断）
             *           "context": "$.buttons[0]"  // 权限影响的节点上下文
             *         }
             */
            List<JSONObject> securityObjs = (List<JSONObject>) JSONPath.eval(configuration, "$.." + SECURITY_KEY);
            if (CollectionUtils.isEmpty(securityObjs)) {
                return widgetDefinionJson;
            }
            SecurityAuditFacadeService securityAuditFacadeService = ApplicationContextHolder
                    .getBean(SecurityAuditFacadeService.class);
            for (JSONObject j : securityObjs) {
                if (!j.containsKey(SECURITY_ID_KEY)) {
                    continue;
                }
                String id = j.getString(SECURITY_ID_KEY);
                boolean isJSONPathExpression = id.startsWith("$.");
                id = isJSONPathExpression ? (String) JSONPath.eval(configuration, id) : id;
                String type = StringUtils.defaultIfBlank(j.getString(SECURITY_TYPE_KEY), AppFunctionType.AppProductIntegration);
                if (AppFunctionType.AppWidgetFunctionElement.equalsIgnoreCase(type)) {
                    type = AppFunctionType.AppWidgetFunctionElement;
                    id = DigestUtils.md5Hex(id + getAttribute(AppConstants.KEY_ID));
                }
                if (!securityAuditFacadeService.isGranted(id, type)) {//没有权限，则移除
                    String context = j.getString(SECURITY_CONTEXT_KEY);
                    if (isJSONPathExpression && StringUtils.isBlank(context)) {
                        context = id.substring(0, id.lastIndexOf("."));
                    }
                    JSONPath.remove(configuration, context);
                }
            }
            jsonObject.put(AppConstants.KEY_CONFIGURATION, configuration);
            return jsonObject.toString();
        } else {
            return widgetDefinionJson;
        }

    }

}
