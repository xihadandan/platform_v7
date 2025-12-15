/*
 * @(#)2016-09-09 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.support;

import com.wellsoft.context.version.Version;

/**
 * Description: 常量类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-09-09.1	zhulh		2016-09-09		Create
 * </pre>
 * @date 2016-09-09
 */
public interface AppConstants {
    // .html
    public static final String DOT_HTML = ".html";

    // widget key
    public static final String KEY_UUID = "uuid";
    public static final String KEY_ID = "id";
    public static final String KEY_CTX = "ctx";
    public static final String KEY_WTYPE = "wtype";
    public static final String KEY_TITLE = "title";
    public static final String KEY_COLUMN_INDEX = "columnIndex";
    public static final String KEY_REF_WIDGET_DEF_UUID = "refWidgetDefUuid";
    public static final String KEY_REF_WIDGET_DEF_TITLE = "refWidgetDefTitle";
    public static final String KEY_HTML = "html";
    public static final String KEY_CONFIGURATION = "configuration";
    public static final String KEY_ITEMS = "items";

    // app key
    public static final String KEY_APP_TYPE = "appType";
    public static final String KEY_APP_PATH = "appPath";

    // web resources path
    public static final String WEB_RES_PATH = "/web/res";

    // web app path
    public static final String WEB_APP_PATH = "/web/app";

    // web app version
    public static final String KEY_WEB_APP_VERSION = "app_version";
    public static final String WEB_APP_VERSION = Version.getRuntimeVersion();

    // require js module
    public static final String REQUIRE_JS_MODULE = "requirejs";

    // Empty JSON Object
    public static final String JSON_EMPTY = "{}";

    // web app theme
    public static final String KEY_APP_THEME = "app.theme";

    // 默认主题
    public static final String THEME_DEFAULT = "default";

    // 是否手机应用
    public static final String KEY_IS_MOBILE_APP = "isMobileApp";

    // 窗口模式，1、浏览器窗口，2、模态窗口，3、手机窗口
    public static final String KEY_WINDOW_MODEL = "windowModel";
    public static final int WINDOW_MODEL_BROWSER = 1;
    public static final int WINDOW_MODEL_MODAL = 2;
    public static final int WINDOW_MODEL_MOBILE = 3;

    public static final String PAGE_PREFIX = "page_";
    public static final String PAGE_DEF_PREFIX = "ROLE_page_sysdef_";
    public static final String PAGEREF_PREFIX = "pageref_";
    public static final String FUNCTIONREF_OF_PAGE_PREFIX = "funcref_page";

    // 页面资源引用方式1、配置资源，2、开发资源
    public static final String FUNCTIONREF_TYPE_SYSTEM = "1";
    public static final String FUNCTIONREF_TYPE_CUSTOMER = "2";

    // 工作台
    public static final String WORKBENCH = "WORKBENCH";
}
