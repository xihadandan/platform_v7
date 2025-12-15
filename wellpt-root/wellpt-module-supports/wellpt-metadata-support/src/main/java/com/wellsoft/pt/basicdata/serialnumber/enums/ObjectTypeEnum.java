package com.wellsoft.pt.basicdata.serialnumber.enums;

/**
 * @Auther: yt
 * @Date: 2022/5/9 14:31
 * @Description:
 */
public enum ObjectTypeEnum {

    TABLE(1);//数据库表


    Integer type;

    ObjectTypeEnum(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }


}
