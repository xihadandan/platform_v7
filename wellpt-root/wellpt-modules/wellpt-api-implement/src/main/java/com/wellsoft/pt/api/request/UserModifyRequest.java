/**
 * Copyright (c) 2005-2012, www.dengwl.com
 * All rights reserved.
 *
 * @Title: UserModifyRequest.java
 * @Package com.wellsoft.pt.api.request
 * @Description: TODO
 * @author Administrator
 * @date 2015-1-9 上午9:40:28
 * @version V1.0
 */
package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.response.UserModifyResponse;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author Administrator
 * @ClassName: UserModifyRequest
 * @Description: TODO
 * @date 2015-1-9 上午9:40:28
 */
public class UserModifyRequest extends WellptRequest<UserModifyResponse> {

    /**
     * @Fields id : 用户id
     */
    private String userid;
    /**
     * @Fields name : 姓名
     */
    private String name;
    /**
     * @Fields photoId : 原头像id
     */
    private String photoId;
    /**
     * @Fields smallPhotoId : 小头像id
     */
    private String smallPhotoId;

    /**
     * @Fields mobilePhone : 手机
     */
    private String mobilePhone;

    /**
     * @Fields officePhone : 办公电话
     */
    private String officePhone;

    /**
     * @Fields sex : 性别 1男 2女
     */
    private String sex;

    /**
     * @Fields email : 电子邮件
     */
    private String email;

    /**
     * @Fields allowMsg : 是否接受短信
     */
    private String allowMsg;


    /* (No Javadoc)
     * <p>Title: getApiServiceName</p>
     * <p>Description: </p>
     * @return
     * @see com.wellsoft.pt.api.WellptRequest#getApiServiceName()
     */
    @Override
    public String getApiServiceName() {
        // TODO Auto-generated method stub
        return ApiServiceName.USER_MODIFY;
    }

    /* (No Javadoc)
     * <p>Title: getResponseClass</p>
     * <p>Description: </p>
     * @return
     * @see com.wellsoft.pt.api.WellptRequest#getResponseClass()
     */
    @Override
    @JsonIgnore
    public Class<UserModifyResponse> getResponseClass() {
        // TODO Auto-generated method stub
        return UserModifyResponse.class;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the photoId
     */
    public String getPhotoId() {
        return photoId;
    }

    /**
     * @param photoId the photoId to set
     */
    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    /**
     * @return the smallPhotoId
     */
    public String getSmallPhotoId() {
        return smallPhotoId;
    }

    /**
     * @param smallPhotoId the smallPhotoId to set
     */
    public void setSmallPhotoId(String smallPhotoId) {
        this.smallPhotoId = smallPhotoId;
    }

    /**
     * @return the userid
     */
    public String getUserid() {
        return userid;
    }

    /**
     * @param userid the userid to set
     */
    public void setUserid(String userid) {
        this.userid = userid;
    }

    /**
     * @return the allowMsg
     */
    public String getAllowMsg() {
        return allowMsg;
    }

    /**
     * @param allowMsg the allowMsg to set
     */
    public void setAllowMsg(String allowMsg) {
        this.allowMsg = allowMsg;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the sex
     */
    public String getSex() {
        return sex;
    }

    /**
     * @param sex the sex to set
     */
    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
     * @return the officePhone
     */
    public String getOfficePhone() {
        return officePhone;
    }

    /**
     * @param officePhone the officePhone to set
     */
    public void setOfficePhone(String officePhone) {
        this.officePhone = officePhone;
    }

    /**
     * @return the mobilePhone
     */
    public String getMobilePhone() {
        return mobilePhone;
    }

    /**
     * @param mobilePhone the mobilePhone to set
     */
    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

}
