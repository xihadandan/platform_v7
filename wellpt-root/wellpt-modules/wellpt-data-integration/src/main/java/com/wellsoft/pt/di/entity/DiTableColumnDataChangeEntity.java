package com.wellsoft.pt.di.entity;

import com.wellsoft.context.jdbc.entity.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Blob;
import java.sql.Clob;
import java.util.Date;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/26
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/26    chenq		2019/8/26		Create
 * </pre>
 */
@Entity
@Table(name = "DI_TABLE_COLUMN_DATA_CHANGE")
@DynamicUpdate
@DynamicInsert
@IdClass(DiTableColumnDataChangePk.class)
public class DiTableColumnDataChangeEntity extends BaseEntity {

    private static final long serialVersionUID = 2064711938994532371L;

    private String uuid;

    private String columnName;

    private Date createTime;

    private String dataType;

    private String dataBasicValue;

    private String dataTextValue;

    private Clob dataClobValue;

    private Blob dataBlobValue;

    private long lobLength = 0L;

    @Id
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

    @Id
    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataBasicValue() {
        return dataBasicValue;
    }

    public void setDataBasicValue(String dataBasicValue) {
        this.dataBasicValue = dataBasicValue;
    }

    public String getDataTextValue() {
        return dataTextValue;
    }

    public void setDataTextValue(String dataTextValue) {
        this.dataTextValue = dataTextValue;
    }

    public Clob getDataClobValue() {
        return dataClobValue;
    }

    public void setDataClobValue(Clob dataClobValue) {
        this.dataClobValue = dataClobValue;
    }

    public Blob getDataBlobValue() {
        return dataBlobValue;
    }

    public void setDataBlobValue(Blob dataBlobValue) {
        this.dataBlobValue = dataBlobValue;
    }

    @Transient
    public long getLobLength() {
        return lobLength;
    }

    public void setLobLength(long lobLength) {
        this.lobLength = lobLength;
    }


}
