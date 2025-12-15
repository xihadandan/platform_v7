package com.wellsoft.pt.ei.dto.org;

import com.wellsoft.pt.ei.annotate.FieldType;
import com.wellsoft.pt.ei.constants.ExportFieldTypeEnum;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021/9/28.1	liuyz		2021/9/28		Create
 * </pre>
 * @date 2021/9/28
 */
public class OrgUserData implements Serializable {
    @FieldType(desc = "主键uuid")
    private String uuid;
    @FieldType(desc = "账号名", required = true)
    private String loginName;
    @FieldType(desc = "中文账号名", required = false)
    private String loginNameZh;
    @FieldType(desc = "姓名", required = true)
    private String userName;
    @FieldType(desc = "用户Id")
    private String id;
    @FieldType(desc = "编码", required = true)
    private String code;
    @FieldType(desc = "备注")
    private String remark;
    @FieldType(desc = "头像照片的uuid", type = ExportFieldTypeEnum.FILE)
    private List<String> photoUuid;
    @FieldType(desc = "英文名")
    private String englishName;
    @FieldType(desc = "性别", dictValue = "0：女；1：男")
    private String sex;
    //    private String sexVal;
    @FieldType(desc = "身份证号")
    private String idNumber;
    @FieldType(desc = "手机号")
    private String mobilePhone;
    @FieldType(desc = "家庭电话")
    private String homePhone;
    @FieldType(desc = "办公电话")
    private String officePhone;
    @FieldType(desc = "邮箱")
    private String mainEmail;
    @FieldType(desc = "员工编号")
    private String employeeNumber;
    @FieldType(desc = "主要职位的Id路径")
    private String mainJobIdPath;
    @FieldType(desc = "主要职位的名称路径")
    private String mainJobNamePath;
    @FieldType(desc = "其它职位的Id路径")
    private String otherJobIdPaths;
    @FieldType(desc = "其它职位的名称路径")
    private String otherJobNamePaths;
    @FieldType(desc = "直属上级领导的Id路径")
    private String directLeaderIdPaths;
    @FieldType(desc = "直属上级领导的名称路径")
    private String directLeaderNamePaths;

    /*public String getSexVal() {
        return sexVal;
    }

    public void setSexVal(String sexVal) {
        this.sexVal = sexVal;
    }*/

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<String> getPhotoUuid() {
        return photoUuid;
    }

    public void setPhotoUuid(List<String> photoUuid) {
        this.photoUuid = photoUuid;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getOfficePhone() {
        return officePhone;
    }

    public void setOfficePhone(String officePhone) {
        this.officePhone = officePhone;
    }

    public String getMainEmail() {
        return mainEmail;
    }

    public void setMainEmail(String mainEmail) {
        this.mainEmail = mainEmail;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getMainJobIdPath() {
        return mainJobIdPath;
    }

    public void setMainJobIdPath(String mainJobIdPath) {
        this.mainJobIdPath = mainJobIdPath;
    }

    public String getMainJobNamePath() {
        return mainJobNamePath;
    }

    public void setMainJobNamePath(String mainJobNamePath) {
        this.mainJobNamePath = mainJobNamePath;
    }

    public String getOtherJobIdPaths() {
        return otherJobIdPaths;
    }

    public void setOtherJobIdPaths(String otherJobIdPaths) {
        this.otherJobIdPaths = otherJobIdPaths;
    }

    public String getOtherJobNamePaths() {
        return otherJobNamePaths;
    }

    public void setOtherJobNamePaths(String otherJobNamePaths) {
        this.otherJobNamePaths = otherJobNamePaths;
    }

    public String getDirectLeaderIdPaths() {
        return directLeaderIdPaths;
    }

    public void setDirectLeaderIdPaths(String directLeaderIdPaths) {
        this.directLeaderIdPaths = directLeaderIdPaths;
    }

    public String getDirectLeaderNamePaths() {
        return directLeaderNamePaths;
    }

    public void setDirectLeaderNamePaths(String directLeaderNamePaths) {
        this.directLeaderNamePaths = directLeaderNamePaths;
    }

    public String getLoginNameZh() {
        return loginNameZh;
    }

    public void setLoginNameZh(String loginNameZh) {
        this.loginNameZh = loginNameZh;
    }
}
