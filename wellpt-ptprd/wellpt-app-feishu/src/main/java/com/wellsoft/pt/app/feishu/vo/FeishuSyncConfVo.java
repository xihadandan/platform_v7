package com.wellsoft.pt.app.feishu.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 飞书同步配置json对象
 * 部门
 * 姓名 头像 英文名 性别 手机号码 个人邮箱 员工编号
 * 职位（人员和职位的关系 对应职位，需创建或匹配，没有就直接挂在部门下）
 * 默认true启用
 */
@ApiModel("飞书同步配置json对象")
public class FeishuSyncConfVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 行政组织UUID
     */
    private Long orgUuid;

    /**
     * 行政组织名称
     */
    private String orgName;

    @ApiModelProperty("部门是否同步")
    private boolean dept;
    @ApiModelProperty("用户姓名是否同步")
    private boolean userName;
    @ApiModelProperty("用户头像是否同步")
    private boolean userAvatar;
    @ApiModelProperty("用户英文名是否同步")
    private boolean userEnName;
    @ApiModelProperty("用户性别是否同步")
    private boolean userGender;
    @ApiModelProperty("用户手机号码是否同步")
    private boolean userMobile;
    @ApiModelProperty("用户邮箱是否同步")
    private boolean userEmail;
    @ApiModelProperty("用户工号是否同步")
    private boolean userNo;
    @ApiModelProperty("职位用户关系是否同步")
    private boolean jobUser;

    public static FeishuSyncConfVo getDefaultSyncConfVo() {
        FeishuSyncConfVo confVo = new FeishuSyncConfVo();
        confVo.setDept(true);
        confVo.setUserName(true);
        confVo.setUserAvatar(true);
        confVo.setUserEnName(true);
        confVo.setUserGender(true);
        confVo.setUserMobile(true);
        confVo.setUserEmail(true);
        confVo.setUserNo(true);
        confVo.setJobUser(true);
        return confVo;
    }

    public boolean isDept() {
        return dept;
    }

    public void setDept(boolean dept) {
        this.dept = dept;
    }

    public boolean isUserName() {
        return userName;
    }

    public void setUserName(boolean userName) {
        this.userName = userName;
    }

    public boolean isUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(boolean userAvatar) {
        this.userAvatar = userAvatar;
    }

    public boolean isUserEnName() {
        return userEnName;
    }

    public void setUserEnName(boolean userEnName) {
        this.userEnName = userEnName;
    }

    public boolean isUserGender() {
        return userGender;
    }

    public void setUserGender(boolean userGender) {
        this.userGender = userGender;
    }

    public boolean isUserMobile() {
        return userMobile;
    }

    public void setUserMobile(boolean userMobile) {
        this.userMobile = userMobile;
    }

    public boolean isUserEmail() {
        return userEmail;
    }

    public void setUserEmail(boolean userEmail) {
        this.userEmail = userEmail;
    }

    public boolean isUserNo() {
        return userNo;
    }

    public void setUserNo(boolean userNo) {
        this.userNo = userNo;
    }

    public boolean isJobUser() {
        return jobUser;
    }

    public void setJobUser(boolean jobUser) {
        this.jobUser = jobUser;
    }
}
