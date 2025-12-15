/*
 * @(#)2016年3月30日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.html;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年3月30日.1	zhulh		2016年3月30日		Create
 * </pre>
 * @date 2016年3月30日
 */
public class Meta {

    public final static String CONTENT_TYPE = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">";

    public final static String VIEWPORT = "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">";


    //ieu兼容性设置:
    //若为ie时且版本高于ie11，则指定用兼容性ie10来渲染，dll控件最高只支持IE10
    //可参照: http://blog.csdn.net/wanglei9876/article/details/44280675
    public final static String X_UA_COMPATIBLE = "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=10\">";


    public final static String RENDERER = "<meta name=\"renderer\" content=\"webkit\">";

    public final static String CACHE_CONTROL = "<meta http-equiv=\"Cache-Control\" content=\"no-store\">";

    public final static String PRAGMA = "<meta http-equiv=\"Pragma\" content=\"no-cache\">";

    public final static String EXPIRES = "<meta http-equiv=\"Expires\" content=\"0\">";

}
