package com.wellsoft.pt.org.support;

import java.util.ArrayList;
import java.util.List;


/**
 * 组织树类型枚举
 *
 * @author xiem
 */
//@EnumClass(objectName = "OrgVersionTreeType", keyName = "type", valueName = "name")
public enum TreeOrgType {

    employee("employee", "员工"), job("job", "职位"), department("department", "部门");
    private String type;
    private String name;


    TreeOrgType(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public static List<String> getCodeByVagueName(String name) {
        List<String> types = new ArrayList<String>();
        for (TreeOrgType emun : TreeOrgType.values()) {
            if (emun.getName().indexOf(name) >= 0) {
                types.add(emun.getType());
            }
        }
        return types;
    }

    public static String getNameByCode(String type) {
        for (TreeOrgType emun : TreeOrgType.values()) {
            if (emun.getType().equals(type)) {
                return emun.getName();
            }
        }
        return type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
