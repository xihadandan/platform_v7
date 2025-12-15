package com.wellsoft.pt.dms.enums;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Description:文档交换密级
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
public enum DocExchangeEncryptionLevelEnum {
    //    0 非密 1 秘密 2 机密
    NO_ENCRYPT("非密"), SECRET("秘密"), CONFIDENTIAL("机密");

    private String name;

    DocExchangeEncryptionLevelEnum(String name) {
        this.name = name;
    }

    public static List<String> getNames() {
        List<String> nameList = Lists.newArrayList();
        DocExchangeEncryptionLevelEnum[] levels = DocExchangeEncryptionLevelEnum.values();
        for (DocExchangeEncryptionLevelEnum l : levels) {
            nameList.add(l.getName());
        }
        return nameList;
    }

    public static DocExchangeEncryptionLevelEnum get(Integer ordinal) {
        DocExchangeEncryptionLevelEnum[] enums = DocExchangeEncryptionLevelEnum.values();
        for (DocExchangeEncryptionLevelEnum e : enums) {
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
