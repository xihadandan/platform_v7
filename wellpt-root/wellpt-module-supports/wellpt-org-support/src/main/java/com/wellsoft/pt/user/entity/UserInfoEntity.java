package com.wellsoft.pt.user.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.user.enums.UserTypeEnum;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * Description: 用户信息表
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年08月10日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "USER_INFO")
@DynamicUpdate
@DynamicInsert
public class UserInfoEntity extends IdEntity {


    private static final long serialVersionUID = -7101189016479466L;

    private String loginName;
    private String accountUuid;
    private String userName;
    private String enName;
    private UserTypeEnum type;//用户类型：个人/法人/员工
    private String mail;//邮箱地址
    private String ceilPhoneNumber;//手机号码
    private String userId;
    private String avatar;
    private String userNo;//编号
    private String pinYin;//拼音
    private Gender gender;
    private String remark; // 备注
    private String workState;

    private String idNumber;

    private List<UserNameI18nEntity> userNameI18ns;


    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UserTypeEnum getType() {
        return type;
    }

    public void setType(UserTypeEnum type) {
        this.type = type;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getCeilPhoneNumber() {
        return ceilPhoneNumber;
    }

    public void setCeilPhoneNumber(String ceilPhoneNumber) {
        this.ceilPhoneNumber = ceilPhoneNumber;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEnName() {
        return this.enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getUserNo() {
        return this.userNo;
    }

    public void setUserNo(final String userNo) {
        this.userNo = userNo;
    }

    public String getPinYin() {
        return this.pinYin;
    }

    public void setPinYin(final String pinYin) {
        this.pinYin = pinYin;
    }


    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public static enum Gender {
        MALE, FEMALE
    }

    public String getWorkState() {
        return workState;
    }

    public void setWorkState(String workState) {
        this.workState = workState;
    }

    @Transient
    public List<UserNameI18nEntity> getUserNameI18ns() {
        return userNameI18ns;
    }

    public void setUserNameI18ns(List<UserNameI18nEntity> userNameI18ns) {
        this.userNameI18ns = userNameI18ns;
    }

    @Transient
    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }
}
