package com.wellsoft.oauth2.enums;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/9/25
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/9/25    chenq		2019/9/25		Create
 * </pre>
 */
public enum DataImportTypeEnum {
    USER_IMPORT("导入用户");

    private String name;

    DataImportTypeEnum(String name) {
        this.name = name;
    }
}
