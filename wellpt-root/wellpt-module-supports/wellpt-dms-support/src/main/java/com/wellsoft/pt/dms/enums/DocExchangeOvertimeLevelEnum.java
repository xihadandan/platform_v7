package com.wellsoft.pt.dms.enums;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Description:文档交换超时紧要性
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
public enum DocExchangeOvertimeLevelEnum {
    NONE(""), NORMAL("一般"), IMPORTANT("重要"), URGE("紧急");

    private String name;

    DocExchangeOvertimeLevelEnum(String name) {
        this.name = name;
    }

    public static List<String> getNames() {
        List<String> nameList = Lists.newArrayList();
        DocExchangeOvertimeLevelEnum[] levels = DocExchangeOvertimeLevelEnum.values();
        for (DocExchangeOvertimeLevelEnum l : levels) {
            nameList.add(l.getName());
        }
        return nameList;
    }

    public static DocExchangeOvertimeLevelEnum get(Integer ordinal) {
        DocExchangeOvertimeLevelEnum[] enums = DocExchangeOvertimeLevelEnum.values();
        for (DocExchangeOvertimeLevelEnum e : enums) {
            if (e.ordinal() == ordinal) {
                return e;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
