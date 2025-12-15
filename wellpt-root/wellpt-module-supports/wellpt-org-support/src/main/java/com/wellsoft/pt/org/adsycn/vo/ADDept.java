package com.wellsoft.pt.org.adsycn.vo;

import java.io.Serializable;

/*url:
name:: Q0ZM5Zub5bed5Z+65Zyw
instanceType: 4
ou:: Q0ZM5Zub5bed5Z+65Zyw
uSNCreated: 173215
uSNChanged: 173215
objectClass: top
objectClass: organizationalUnit
distinguishedName:: T1U9Q0ZM5Zub5bed5Z+65ZywLE9VPUNGTOS6p+WTgee6vyxPVT1PVSxEQ
 z1MRUVEQVJTT04sREM9TE9DQUw=
objectCategory: CN=Organizational-Unit,CN=Schema,CN=Configuration,DC=LEEDARSO
 N,DC=LOCAL
objectGUID:: 77+9O++/vVEhb3hJ77+9alnvv71oKmlJ
whenCreated: 20140417060032.0Z
whenChanged: 20140420044809.0Z
dSCorePropagationData: 16010101000000.0Z
*/

public class ADDept implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -632250846042967123L;

    public String name;

    public String ou;

    public String[] deptPath;//部门路径

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOu() {
        return ou;
    }

    public void setOu(String ou) {
        this.ou = ou;
    }

    public String[] getDeptPath() {
        return deptPath;
    }

    public void setDeptPath(String[] deptPath) {
        this.deptPath = deptPath;
    }

}
