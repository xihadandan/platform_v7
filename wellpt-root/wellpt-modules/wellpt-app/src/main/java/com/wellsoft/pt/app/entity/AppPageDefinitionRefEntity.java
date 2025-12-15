/*
 * @(#)2019年6月13日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年6月13日.1	zhulh		2019年6月13日		Create
 * </pre>
 * @date 2019年6月13日
 */
@Entity
@Table(name = "APP_PAGE_DEFINITION_REF")
@DynamicUpdate
@DynamicInsert
public class AppPageDefinitionRefEntity extends TenantEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -4689852839473306019L;

    // 引用的页面定义UUID
    private String refUuid;

    // 所在产品集成UUID
    private String appPiUuid;

    /**
     * @return the refUuid
     */
    public String getRefUuid() {
        return refUuid;
    }

    /**
     * @param refUuid 要设置的refUuid
     */
    public void setRefUuid(String refUuid) {
        this.refUuid = refUuid;
    }

    /**
     * @return the appPiUuid
     */
    public String getAppPiUuid() {
        return appPiUuid;
    }

    /**
     * @param appPiUuid 要设置的appPiUuid
     */
    public void setAppPiUuid(String appPiUuid) {
        this.appPiUuid = appPiUuid;
    }

}
