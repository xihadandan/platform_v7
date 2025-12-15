package com.wellsoft.pt.org.adsycn.vo;

import java.io.Serializable;

/*
displayName:: 6YOR55m76b6Z
name:: 6YOR55m76b6Z
cn:: 6YOR55m76b6Z
sn:: 6YOR
givenName:: 55m76b6Z
mail: zhangs@leedarson.com
userPrincipalName: zhangs@LEEDARSON.LOCAL
sAMAccountName: zhangs
objectClass: user
objectClass: top
objectClass: person
objectClass: organizationalPerson
sAMAccountType: 805306368
homePhone: 1e //家庭电话
mobile: 3e  //移动电话
facsimileTelephoneNumber: 5e //传真
telephoneNumber //办公电话
*/

public class ADUser implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 6190281760760542017L;

    public String sAMAccountName;//zhangs

    public String name;//张三

    public String cn;//张三

    public String sn; //张

    public String displayName; //张三

    public String mail;//zhangs@leedarson.com

    public String userPrincipalName;

    public String givenName;//三

    public String[] deptPath;//部门路径

    public String pwd;//密码

    public String homePhone; //家庭电话

    public String mobile;//移动电话

    public String facsimileTelephoneNumber;//传真

    public String telephoneNumber;

    public boolean isEnabled;//是否启用帐号

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

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getUserPrincipalName() {
        return userPrincipalName;
    }

    public void setUserPrincipalName(String userPrincipalName) {
        this.userPrincipalName = userPrincipalName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String[] getDeptPath() {
        return deptPath;
    }

    public void setDeptPath(String[] deptPath) {
        this.deptPath = deptPath;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getFacsimileTelephoneNumber() {
        return facsimileTelephoneNumber;
    }

    public void setFacsimileTelephoneNumber(String facsimileTelephoneNumber) {
        this.facsimileTelephoneNumber = facsimileTelephoneNumber;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

}
