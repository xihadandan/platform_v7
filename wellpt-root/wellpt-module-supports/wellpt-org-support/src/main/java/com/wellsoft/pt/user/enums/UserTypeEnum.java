package com.wellsoft.pt.user.enums;

/**
 * Description: 用户类型
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年08月11日   chenq	 Create
 * </pre>
 */
public enum UserTypeEnum {

    INDIVIDUAL("个人"), LEGAL_PERSION("法人"), STAFF("员工");

    private String name;

    UserTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
