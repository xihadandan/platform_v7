/*
 * @(#)2018年6月15日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.recentuse.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年6月15日.1	zhulh		2018年6月15日		Create
 * </pre>
 * @date 2018年6月15日
 */
@Entity
@Table(name = "CD_RECENT_USE")
public class RecentUseEntity extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -6962016807842380011L;

    // 使用对象ID
    private String objectIdIdentity;
    // 用户ID
    private String userId;
    // 使用时间
    private Date useTime;
    // 模块ID
    private String moduleId;

    /**
     * @return the objectIdIdentity
     */
    public String getObjectIdIdentity() {
        return objectIdIdentity;
    }

    /**
     * @param objectIdIdentity 要设置的objectIdIdentity
     */
    public void setObjectIdIdentity(String objectIdIdentity) {
        this.objectIdIdentity = objectIdIdentity;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId 要设置的userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the useTime
     */
    public Date getUseTime() {
        return useTime;
    }

    /**
     * @param useTime 要设置的useTime
     */
    public void setUseTime(Date useTime) {
        this.useTime = useTime;
    }

    /**
     * @return the moduleId
     */
    public String getModuleId() {
        return moduleId;
    }

    /**
     * @param moduleId 要设置的moduleId
     */
    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

}
