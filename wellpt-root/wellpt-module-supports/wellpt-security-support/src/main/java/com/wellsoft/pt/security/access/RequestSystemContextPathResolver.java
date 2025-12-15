package com.wellsoft.pt.security.access;

import org.apache.commons.lang3.BooleanUtils;

/**
 * Description: 当前线程请求的系统ID
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年02月06日   chenq	 Create
 * </pre>
 */
public class RequestSystemContextPathResolver {

    private static ThreadLocal<String> SYSTEM = new ThreadLocal<>();
    private static ThreadLocal<String> PAGE_ID = new ThreadLocal<>();
    private static ThreadLocal<Boolean> IS_MOBILE_APP = new ThreadLocal<>();


    public static String system() {
        return SYSTEM.get();
    }

    public static void setSystem(String system) {
        SYSTEM.set(system);
    }

    public static void setIsMobileApp(boolean isMobileApp) {
        IS_MOBILE_APP.set(isMobileApp);
    }

    public static boolean isMobileApp() {
        return BooleanUtils.isTrue(IS_MOBILE_APP.get());
    }

    public static String pageId() {
        return PAGE_ID.get();
    }

    public static void setPageId(String pageId) {
        PAGE_ID.set(pageId);
    }

    public static void clear() {
        SYSTEM.remove();
        PAGE_ID.remove();
        IS_MOBILE_APP.remove();
    }
}
