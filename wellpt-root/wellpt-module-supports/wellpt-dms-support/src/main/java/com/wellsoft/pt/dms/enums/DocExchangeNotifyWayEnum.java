package com.wellsoft.pt.dms.enums;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Description:文档交换提醒方式
 *
 * @author chenq
 * @date 2018/5/14
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/14    chenq		2018/5/14		Create
 * </pre>
 */
public enum DocExchangeNotifyWayEnum {
    //    0 短信 1 邮件 2 在线消息
    SMS(0, "短信"), MAIL(1, "邮件"), IM(2, "在线消息");

    private String name;
    private Integer code;

    DocExchangeNotifyWayEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static List<String> getNames() {
        List<String> nameList = Lists.newArrayList();
        DocExchangeNotifyWayEnum[] levels = DocExchangeNotifyWayEnum.values();
        for (DocExchangeNotifyWayEnum l : levels) {
            nameList.add(l.getName());
        }
        return nameList;
    }

    public static DocExchangeNotifyWayEnum getByCode(Integer code) {
        DocExchangeNotifyWayEnum[] levels = DocExchangeNotifyWayEnum.values();
        for (DocExchangeNotifyWayEnum l : levels) {
            if (l.getCode() == code) {
                return l;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
