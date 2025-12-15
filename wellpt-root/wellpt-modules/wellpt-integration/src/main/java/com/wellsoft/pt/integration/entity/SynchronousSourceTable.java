/*
 * @(#)2013-3-13 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author ruanhg
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-4-28.1	ruanhg		2014-4-28		Create
 * </pre>
 * @date 2014-4-28
 */
@Entity
@Table(name = "is_synchronous_source_table")
@DynamicUpdate
@DynamicInsert
public class SynchronousSourceTable extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -8999701794941226349L;

    private String name;//名称

    private String id;//ID

    private String code;//编号

    private String type;//数据类型

    private String tableCnName;//表中文名

    private String tableEnName;//表英文名

    private String definitionUuid;

    private String joinTable;

    private String whereStr;

    private String method;

    private Boolean isUserUse;

    private Boolean isEnable;

    private String preDataUuid;

    private Date preModifyTime;

    private Boolean isRelationTable;//是否关系表

    private String relationTable;//涉及的关系表

    private String tenant;

    private Boolean notOut;

    private Boolean notIn;

    private String jobId;

    private String jobIdIn;
    @UnCloneable
    private Set<SynchronousSourceField> synchronousSourceFields;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTableCnName() {
        return tableCnName;
    }

    public void setTableCnName(String tableCnName) {
        this.tableCnName = tableCnName;
    }

    public String getTableEnName() {
        return tableEnName;
    }

    public void setTableEnName(String tableEnName) {
        this.tableEnName = tableEnName;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "synchronousSourceTable")
    @Cascade({CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    public Set<SynchronousSourceField> getSynchronousSourceFields() {
        return synchronousSourceFields;
    }

    public void setSynchronousSourceFields(Set<SynchronousSourceField> synchronousSourceFields) {
        this.synchronousSourceFields = synchronousSourceFields;
    }

    public String getDefinitionUuid() {
        return definitionUuid;
    }

    public void setDefinitionUuid(String definitionUuid) {
        this.definitionUuid = definitionUuid;
    }

    public String getJoinTable() {
        return joinTable;
    }

    public void setJoinTable(String joinTable) {
        this.joinTable = joinTable;
    }

    public String getWhereStr() {
        return whereStr;
    }

    public void setWhereStr(String whereStr) {
        this.whereStr = whereStr;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Boolean getIsUserUse() {
        return isUserUse;
    }

    public void setIsUserUse(Boolean isUserUse) {
        this.isUserUse = isUserUse;
    }

    public Boolean getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(Boolean isEnable) {
        this.isEnable = isEnable;
    }

    public String getPreDataUuid() {
        return preDataUuid;
    }

    public void setPreDataUuid(String preDataUuid) {
        this.preDataUuid = preDataUuid;
    }

    public Boolean getIsRelationTable() {
        return isRelationTable;
    }

    public void setIsRelationTable(Boolean isRelationTable) {
        this.isRelationTable = isRelationTable;
    }

    @Column(length = 2000)
    public String getRelationTable() {
        return relationTable;
    }

    public void setRelationTable(String relationTable) {
        this.relationTable = relationTable;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public Date getPreModifyTime() {
        return preModifyTime;
    }

    public void setPreModifyTime(Date preModifyTime) {
        this.preModifyTime = preModifyTime;
    }

    public Boolean getNotOut() {
        return notOut;
    }

    public void setNotOut(Boolean notOut) {
        this.notOut = notOut;
    }

    public Boolean getNotIn() {
        return notIn;
    }

    public void setNotIn(Boolean notIn) {
        this.notIn = notIn;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobIdIn() {
        return jobIdIn;
    }

    public void setJobIdIn(String jobIdIn) {
        this.jobIdIn = jobIdIn;
    }

}
