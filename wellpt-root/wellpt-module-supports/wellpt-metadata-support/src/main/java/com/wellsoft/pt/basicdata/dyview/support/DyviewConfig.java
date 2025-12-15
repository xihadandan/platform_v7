/*
 * @(#)2014-1-20 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.dyview.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: 视图常量类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-1-20.1	wubin		2014-1-20		Create
 * </pre>
 * @date 2014-1-20
 */
public class DyviewConfig {

    //视图数据类型——动态表单
    public static final int DYVIEW_DATASCOPE_DYTABLE = 1;
    //视图数据类型——实体类
    public static final int DYVIEW_DATASCOPE_ENTITY = 2;
    //视图数据类型——模块数据
    public static final int DYVIEW_DATASCOPE_MOUDLE = 3;
    //视图前台调用后台控制层的类型（点击排序）
    public static final String DYVIEW_CLICKTYPE_SORT = "clickSort";
    //视图前台调用后台控制层的类型（条件查询）
    public static final String DYVIEW_CLICKTYPE_CONDSELECT = "condSelect";
    //视图前台调用后台控制层的类型（关键字查询）
    public static final String DYVIEW_CLICKTYPE_KEYWORD = "keyWord";
    //默认排序升序
    public static final String DYVIEW_SORT_ASC = "升序";
    //默认排序降序
    public static final String DYVIEW_SORT_DESC = "降序";
    private static final Map<String, String> DYVIEW_COLUMNCSS_CONDTION = new HashMap<String, String>();

    static {
        DYVIEW_COLUMNCSS_CONDTION.put("1", "==");
        DYVIEW_COLUMNCSS_CONDTION.put("2", "!=");
        DYVIEW_COLUMNCSS_CONDTION.put("3", "<");
        DYVIEW_COLUMNCSS_CONDTION.put("4", "<=");
        DYVIEW_COLUMNCSS_CONDTION.put("5", ">");
        DYVIEW_COLUMNCSS_CONDTION.put("6", ">=");
        DYVIEW_COLUMNCSS_CONDTION.put("7", "包含");
        DYVIEW_COLUMNCSS_CONDTION.put("8", "不包含");
    }

    private DyviewConfig() {
        //  singlenton
    }

    /**
     * @return the dyviewColumncssCondtion
     */
    public static Map<String, String> getDyviewColumncssCondtion() {
        return Collections.unmodifiableMap(DYVIEW_COLUMNCSS_CONDTION);
    }

}
