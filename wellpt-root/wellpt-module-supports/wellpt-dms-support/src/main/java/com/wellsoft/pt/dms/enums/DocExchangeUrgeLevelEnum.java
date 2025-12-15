package com.wellsoft.pt.dms.enums;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Description:文档交换缓急程度
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
public enum DocExchangeUrgeLevelEnum {
    //   0 一般 1 急件 2 特急
    NORMAL("一般"), URGE("急件"), EXTRA_URGE("特急");

    private String name;

    DocExchangeUrgeLevelEnum(String name) {
        this.name = name;
    }

    public static List<String> getNames() {
        List<String> nameList = Lists.newArrayList();
        DocExchangeUrgeLevelEnum[] levels = DocExchangeUrgeLevelEnum.values();
        for (DocExchangeUrgeLevelEnum l : levels) {
            nameList.add(l.getName());
        }
        return nameList;
    }

    public static DocExchangeUrgeLevelEnum get(Integer ordinal) {
        DocExchangeUrgeLevelEnum[] enums = DocExchangeUrgeLevelEnum.values();
        for (DocExchangeUrgeLevelEnum e : enums) {
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
