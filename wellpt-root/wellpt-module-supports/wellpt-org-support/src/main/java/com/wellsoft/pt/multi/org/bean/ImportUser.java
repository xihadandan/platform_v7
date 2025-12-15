/*
 * @(#)2018年10月10日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.bean;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年10月10日.1	zyguo		2018年10月10日		Create
 * </pre>
 * @date 2018年10月10日
 */
public class ImportUser implements Serializable {
    private static final long serialVersionUID = 5398139349667352487L;
    private String unitName; // 单位名称
    private String deptPath; // 部门路径
    private String jobName; // 职位名称
    private String loginName; // 账号
    private String loginNameZh; // 中文账号
    private String userName; // 姓名
    private String sex; // 性别
    private String mobilePhone;// 手机号
    private String mainEmail; // 邮箱
    private String code; // 编号，员工编号
    private String homePhone; // 家庭电话
    private String officePhone; // 办公电话
    private String idNumber; // 身份证号
    private String englishName; // 英文名

    public ImportUser() {

    }

    public ImportUser(Row row) {
        Cell unitNameCell = row.getCell(0);
        Cell deptPathCell = row.getCell(1);
        Cell jobNameCell = row.getCell(2);
        Cell loginNameCell = row.getCell(3);
        Cell loginNameZhCell = row.getCell(4);
        Cell userNameCell = row.getCell(5);
        Cell sexCell = row.getCell(6);
        Cell mobileCell = row.getCell(7);
        Cell emailCell = row.getCell(8);
        Cell codeCell = row.getCell(9);
        Cell homePhoneCell = row.getCell(10);
        Cell officePhoneCell = row.getCell(11);
        Cell idNumberCell = row.getCell(12);
        Cell englishNameCell = row.getCell(13);

        this.unitName = unitNameCell == null ? null : unitNameCell.getStringCellValue();
        this.deptPath = deptPathCell == null ? null : deptPathCell.getStringCellValue();
        this.jobName = jobNameCell == null ? null : jobNameCell.getStringCellValue();
        this.loginName = loginNameCell == null ? null : loginNameCell.getStringCellValue();
        this.loginNameZh = loginNameZhCell == null ? null : loginNameZhCell.getStringCellValue();
        this.userName = userNameCell == null ? null : userNameCell.getStringCellValue();
        this.englishName = englishNameCell == null ? null : englishNameCell.getStringCellValue();
        this.sex = sexCell == null ? null : sexCell.getStringCellValue();
        this.mainEmail = emailCell == null ? null : emailCell.getStringCellValue();
        if (StringUtils.isBlank(loginNameZh)) {
            loginNameZh = userName;
        }
        DecimalFormat df = new DecimalFormat("0");
        try {
            this.code = codeCell == null ? null : codeCell.getStringCellValue();
        } catch (Exception e) {
            this.code = codeCell == null ? null : df.format(codeCell.getNumericCellValue());
        }

        try {
            this.mobilePhone = mobileCell == null ? null : mobileCell.getStringCellValue();
        } catch (Exception e) {
            this.mobilePhone = mobileCell == null ? null : df.format(mobileCell.getNumericCellValue());
        }
        try {
            this.homePhone = homePhoneCell == null ? null : homePhoneCell.getStringCellValue();
        } catch (Exception e) {
            this.homePhone = homePhoneCell == null ? null : df.format(homePhoneCell.getNumericCellValue());
        }
        try {
            this.officePhone = officePhoneCell == null ? null : officePhoneCell.getStringCellValue();
        } catch (Exception e) {
            this.officePhone = officePhoneCell == null ? null : df.format(officePhoneCell.getNumericCellValue());
        }
        try {
            this.idNumber = idNumberCell == null ? null : idNumberCell.getStringCellValue();
        } catch (Exception e) {
            this.idNumber = idNumberCell == null ? null : df.format(idNumberCell.getNumericCellValue());
        }

    }

    /**
     * @return the loginName
     */
    public String getLoginName() {
        return loginName;
    }

    /**
     * @param loginName 要设置的loginName
     */
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName 要设置的userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the jobPath
     */
    public String getJobPath() {
        List<String> names = Lists.newArrayList();
        if (StringUtils.isNotBlank(this.unitName)) {
            names.add(this.unitName);
        }
        if (StringUtils.isNotBlank(this.deptPath)) {
            names.add(this.deptPath);
        }
        if (StringUtils.isNotBlank(this.jobName)) {
            names.add(this.jobName);
        }
        return StringUtils.join(names, "/");
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the englishName
     */
    public String getEnglishName() {
        return englishName;
    }

    /**
     * @param englishName 要设置的englishName
     */
    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    /**
     * @return the sex
     */
    public String getSex() {
        return sex;
    }

    /**
     * @param sex 要设置的sex
     */
    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
     * @return the homePhone
     */
    public String getHomePhone() {
        return homePhone;
    }

    /**
     * @param homePhone 要设置的homePhone
     */
    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    /**
     * @return the officePhone
     */
    public String getOfficePhone() {
        return officePhone;
    }

    /**
     * @param officePhone 要设置的officePhone
     */
    public void setOfficePhone(String officePhone) {
        this.officePhone = officePhone;
    }

    /**
     * @return the mainEmail
     */
    public String getMainEmail() {
        return mainEmail;
    }

    /**
     * @param mainEmail 要设置的mainEmail
     */
    public void setMainEmail(String mainEmail) {
        this.mainEmail = mainEmail;
    }

    /**
     * @return the mobilePhone
     */
    public String getMobilePhone() {
        return mobilePhone;
    }

    /**
     * @param mobilePhone 要设置的mobilePhone
     */
    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    /**
     * @return the idNumber
     */
    public String getIdNumber() {
        return idNumber;
    }

    /**
     * @param idNumber 要设置的idNumber
     */
    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    /**
     * @return the unitName
     */
    public String getUnitName() {
        return unitName;
    }

    /**
     * @param unitName 要设置的unitName
     */
    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    /**
     * @return the deptPath
     */
    public String getDeptPath() {
        return deptPath;
    }

    /**
     * @param deptPath 要设置的deptPath
     */
    public void setDeptPath(String deptPath) {
        this.deptPath = deptPath;
    }

    /**
     * @return the jobName
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * @param jobName 要设置的jobName
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getLoginNameZh() {
        return loginNameZh;
    }

    public void setLoginNameZh(String loginNameZh) {
        this.loginNameZh = loginNameZh;
    }
}
