/*
 * @(#)2020年3月5日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.facade.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;

import javax.persistence.MappedSuperclass;

/**
 * Description: 表单实体基类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年3月5日.1	zhulh		2020年3月5日		Create
 * </pre>
 * @date 2020年3月5日
 */
@MappedSuperclass
public class DyformEntity extends TenantEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 8291958035415005075L;

    // 表单定义UUID
    private String formUuid;

    // 状态
    private String status;

    /**
     * @return the formUuid
     */
    public String getFormUuid() {
        return formUuid;
    }

    /**
     * @param formUuid 要设置的formUuid
     */
    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status 要设置的status
     */
    public void setStatus(String status) {
        this.status = status;
    }

}
