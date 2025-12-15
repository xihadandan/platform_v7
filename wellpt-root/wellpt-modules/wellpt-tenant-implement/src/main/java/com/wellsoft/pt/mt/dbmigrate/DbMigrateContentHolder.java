/*
 * @(#)2016年3月8日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.dbmigrate;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年3月8日.1	zhongzh		2016年3月8日		Create
 * </pre>
 * @date 2016年3月8日
 */
public class DbMigrateContentHolder {

    private static final ThreadLocal<String> content = new ThreadLocal<String>();

    private DbMigrateContentHolder() {

    }

    public static String getContent() {
        return DbMigrateContentHolder.content.get();
    }

    public static void setContent(String content) {
        DbMigrateContentHolder.content.set(content);
    }
}
