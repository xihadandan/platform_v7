package com.wellsoft.pt.dms.enums;

/**
 * Description: 文档交换通讯录ID前缀
 *
 * @author chenq
 * @date 2018/6/1
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/6/1    chenq		2018/6/1		Create
 * </pre>
 */
public enum DocExcContactBookIdPrefixEnum {

    CONTACT_ID("CI_DEC", "通讯录联系人ID"), CONTACT_UNIT_ID("UI_DEC", "通讯录单位ID");

    private String id;
    private String name;

    DocExcContactBookIdPrefixEnum(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
