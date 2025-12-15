package com.wellsoft.pt.dms.enums;

/**
 * Description:文档交换类型
 *
 * @author chenq
 * @date 2018/5/16
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/16    chenq		2018/5/16		Create
 * </pre>
 */
public enum DocExchangeTypeEnum {

    DYFORM("动态表单"), FILE("文件");

    private String name;

    DocExchangeTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
