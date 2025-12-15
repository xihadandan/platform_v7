package com.wellsoft.pt.dms.enums;

/**
 * Description: 数据标记类型
 *
 * @author chenq
 * @date 2018/6/19
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/6/19    chenq		2018/6/19		Create
 * </pre>
 */
public enum DataMarkTypeEnum {
    READ("阅读标记"), COLLECTION("收藏标记"), ATTENTION("关注标记");

    private String name;

    DataMarkTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
