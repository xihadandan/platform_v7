/*
 * @(#)8/11/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.enums;

/**
 * Description: 字典类型分类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 8/11/23.1	zhulh		8/11/23		Create
 * </pre>
 * @date 8/11/23
 */
public enum EnumDictionaryCategoryType {
    buildIn,// 系统内置
    module// 业务模块
    ;

    /**
     * @param type
     * @return
     */
    public static Integer getTypeCode(EnumDictionaryCategoryType type) {
        EnumDictionaryCategoryType[] values = values();
        for (int index = 0; index < values.length; index++) {
            if (values[index].equals(type)) {
                return index;
            }
        }
        return null;
    }
}
