/*
 * @(#)2021-07-13 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * Description: 数据库表DMS_DOC_EXCHANGE_DYFORM的实体类
 *
 * @author yt
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-07-13.1	yt		2021-07-13		Create
 * </pre>
 * @date 2021-07-13
 */
@Entity
@Table(name = "DMS_DOC_EXCHANGE_DYFORM")
@DynamicUpdate
@DynamicInsert
public class DmsDocExchangeDyformEntity extends TenantEntity {

    private static final long serialVersionUID = 1626145242905L;

    // 动态表单定义uuid
    private String dyformUuid;
    // 文档交换-配置UUID
    private String dmsDocExchangeConfigUuid;
    // 表单定义JSON
    private String definitionJson;

    /**
     * @return the dyformUuid
     */
    public String getDyformUuid() {
        return this.dyformUuid;
    }

    /**
     * @param dyformUuid
     */
    public void setDyformUuid(String dyformUuid) {
        this.dyformUuid = dyformUuid;
    }

    /**
     * @return the dmsDocExchangeConfigUuid
     */
    public String getDmsDocExchangeConfigUuid() {
        return this.dmsDocExchangeConfigUuid;
    }

    /**
     * @param dmsDocExchangeConfigUuid
     */
    public void setDmsDocExchangeConfigUuid(String dmsDocExchangeConfigUuid) {
        this.dmsDocExchangeConfigUuid = dmsDocExchangeConfigUuid;
    }

    /**
     * @return the definitionJson
     */
    public String getDefinitionJson() {
        return this.definitionJson;
    }

    /**
     * @param definitionJson
     */
    public void setDefinitionJson(String definitionJson) {
        this.definitionJson = definitionJson;
    }

}
