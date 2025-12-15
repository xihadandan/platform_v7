package com.wellsoft.oauth2.enums;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/9/23
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/9/23    chenq		2019/9/23		Create
 * </pre>
 */
public enum GenderEnum {
    MALE("男"), FEMALE("女");

    private String name;

    GenderEnum(String name) {
        this.name = name;
    }
}
