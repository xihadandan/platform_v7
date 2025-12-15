package com.wellsoft.pt.org.adsycn.vo;

import java.io.Serializable;

/*url:
cn:: Q0ZM5Zub5bed5Z+65Zyw
dSCorePropagationData: 16010101000000.0Z
distinguishedName:: Q049Q0ZM5Zub5bed5Z+65ZywLE9VPUNGTOWbm+W3neWfuuWcsCxPVT1DR
 kzkuqflk4Hnur8sT1U9T1UsREM9bGNwLERDPWNu
groupType: -2147483646
instanceType: 4
member:: Q049546L5LqULE9VPUNGTOWbm+W3neWfuuWcsCxPVT1DRkzkuqflk4Hnur8sT1U9T1Us
 REM9bGNwLERDPWNu
name:: Q0ZM5Zub5bed5Z+65Zyw
objectCategory: CN=Group,CN=Schema,CN=Configuration,DC=lcp,DC=cn
objectClass: group
objectClass: top
objectGUID:: 77+9VWpuDO+/ve+/vULvv73vv73vv73vv70=
objectSid:: AQUAAAAAAAUVAAAAcU0h77+9EO+/vRoI77+977+9ajnvv70EAAA=
sAMAccountName:: Q0ZM5Zub5bed5Z+65Zyw
sAMAccountType: 268435456
uSNChanged: 29753
uSNCreated: 29749
whenChanged: 20141121015440.0Z
whenCreated: 20141121015350.0Z
	*/

/**
 * Description: AD安全组
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-11-21.1  zhengky	2014-11-21	  Create
 * </pre>
 * @date 2014-11-21
 */
public class ADGroup implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 5951534367985346288L;

    public String sAMAccountName;//部门1

    public String name;//部门1

    public String cn;//部门1

    public String[] deptPath;//部门路径

    public boolean isCommunicationGroup;//是否通讯组

    public String getsAMAccountName() {
        return sAMAccountName;
    }

    public void setsAMAccountName(String sAMAccountName) {
        this.sAMAccountName = sAMAccountName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public boolean isCommunicationGroup() {
        return isCommunicationGroup;
    }

    public void setCommunicationGroup(boolean isCommunicationGroup) {
        this.isCommunicationGroup = isCommunicationGroup;
    }

    public String[] getDeptPath() {
        return deptPath;
    }

    public void setDeptPath(String[] deptPath) {
        this.deptPath = deptPath;
    }

}
