package com.wellsoft.context;

import java.lang.management.ManagementFactory;
import java.util.List;

/**
 * Description: 上下文相关方法、变量
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年08月19日   chenq	 Create
 * </pre>
 */
public class Context {

    private volatile static Boolean OAUTH2_ENABLE = null;
    private volatile static Boolean IS_DEBUG_MODE = null;

    /**
     * 判断是否开启oauth2认证
     *
     * @return
     */
    public static boolean isOauth2Enable() {
        if (OAUTH2_ENABLE != null) {
            return OAUTH2_ENABLE;
        }
        try {
            Class.forName("com.wellsoft.pt.security.oauth2.configuration.OAuth2ClientConfiguration");
            OAUTH2_ENABLE = true;
        } catch (Exception e) {
            OAUTH2_ENABLE = false;
        }
        return OAUTH2_ENABLE;
    }

    public static boolean isDebug() {
        if (IS_DEBUG_MODE != null) {
            return IS_DEBUG_MODE;
        }
        try {
            List<String> args = ManagementFactory.getRuntimeMXBean().getInputArguments();
            for (String arg : args) {
                if (arg.startsWith("-Xrunjdwp") || arg.startsWith("-agentlib:jdwp")) {
                    IS_DEBUG_MODE = true;
                    return true;
                }
            }
            IS_DEBUG_MODE = true;
        } catch (Exception e) {
            IS_DEBUG_MODE = false;
        }
        return IS_DEBUG_MODE;
    }

}
