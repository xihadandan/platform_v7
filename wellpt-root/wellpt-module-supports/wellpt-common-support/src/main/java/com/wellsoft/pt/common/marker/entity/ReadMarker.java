/*
 * @(#)2013-3-1 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.marker.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

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
 * 2013-3-1.1	zhulh		2013-3-1		Create
 * </pre>
 * @date 2013-3-1
 */
@Entity
@Table(name = "cd_read_marker")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY, region = "entity")
public class ReadMarker extends IdEntity {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -8622020707836398042L;

    // 实体UUID
    private String entityUuid;
    // 用户ID
    private String userId;

    private Date readTime;

    /**
     * @return the entityUuid
     */
    public String getEntityUuid() {
        return entityUuid;
    }

    /**
     * @param entityUuid 要设置的entityUuid
     */
    public void setEntityUuid(String entityUuid) {
        this.entityUuid = entityUuid;
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
     * @return the readTime
     */
    public Date getReadTime() {
        return readTime;
    }

    /**
     * @param readTime 要设置的readTime
     */
    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }

}
