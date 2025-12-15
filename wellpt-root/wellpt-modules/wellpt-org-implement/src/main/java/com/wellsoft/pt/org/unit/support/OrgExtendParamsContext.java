package com.wellsoft.pt.org.unit.support;

import net.sf.json.JSONObject;
import org.springframework.core.NamedThreadLocal;

public class OrgExtendParamsContext {
    public static final String showNotAllowedTenant = "showNotAllowedTenant";
    public static final String uiShowDisEmp = "uiShowDisEmp";
    private static final ThreadLocal<JSONObject> orgExtendParamsContextHolder = new NamedThreadLocal<JSONObject>(
            "OrgExtendParams type context");

    public static void set(String extendParams) {
        JSONObject json = JSONObject.fromObject(extendParams);
        orgExtendParamsContextHolder.set(json);
    }

    public static void remove() {
        orgExtendParamsContextHolder.remove();
    }

    public static String getExtendParam(String paramsName) {
        JSONObject orgExtendParams = orgExtendParamsContextHolder.get();
        if (orgExtendParams.containsKey(paramsName)) {
            return orgExtendParams.getString(paramsName);
        }
        return null;
    }


}
