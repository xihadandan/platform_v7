package com.wellsoft.pt.di.entity;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.entity.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/23
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/23    chenq		2019/8/23		Create
 * </pre>
 */
@Entity
@Table(name = "DI_TABLE_DATA_CHANGE")
@DynamicUpdate
@DynamicInsert
public class DiTableDataChangeEntity extends BaseEntity {

    private static final long serialVersionUID = 2502537981924011313L;

    private String uuid;

    private Date createTime;

    private String tableName;

    private String pkUuid;

    private String pkColName;

    private String action;

    private Integer status;

    private Date syncTime;

    private Integer syncDirection;

    private List<DiTableColumnDataChangeEntity> columnDataChangeEntities = Lists.newArrayList();


    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "com.wellsoft.pt.jpa.support.CustomUUIDGenerator")
    public String getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    @Transient
    public Integer getRecVer() {
        return null;
    }

    @Override
    public void setRecVer(Integer recVer) {

    }

    @Override
    @Transient
    public String getCreator() {
        return null;
    }

    @Override
    public void setCreator(String creator) {

    }


    @Override
    @Transient
    public String getModifier() {
        return null;
    }

    @Override
    public void setModifier(String modifier) {

    }

    @Override
    @Transient
    public Date getModifyTime() {
        return null;
    }

    @Override
    public void setModifyTime(Date modifyTime) {

    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getPkUuid() {
        return pkUuid;
    }

    public void setPkUuid(String pkUuid) {
        this.pkUuid = pkUuid;
    }

    public String getPkColName() {
        return pkColName;
    }

    public void setPkColName(String pkColName) {
        this.pkColName = pkColName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(Date syncTime) {
        this.syncTime = syncTime;
    }

    public Integer getSyncDirection() {
        return syncDirection;
    }

    public void setSyncDirection(Integer syncDirection) {
        this.syncDirection = syncDirection;
    }

    @Transient
    public List<DiTableColumnDataChangeEntity> getColumnDataChangeEntities() {
        return columnDataChangeEntities;
    }

    public void setColumnDataChangeEntities(
            List<DiTableColumnDataChangeEntity> columnDataChangeEntities) {
        this.columnDataChangeEntities = columnDataChangeEntities;
    }
}
