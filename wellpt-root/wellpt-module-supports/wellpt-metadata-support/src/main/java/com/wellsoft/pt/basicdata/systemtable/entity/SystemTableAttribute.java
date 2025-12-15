package com.wellsoft.pt.basicdata.systemtable.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Description: 系统表结构属性实体类
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-21.1	zhouyq		2013-3-21		Create
 * </pre>
 * @date 2013-3-21
 */
@Entity
@Table(name = "cd_system_table_entity_attr")
@DynamicUpdate
@DynamicInsert
public class SystemTableAttribute extends IdEntity {
    private static final long serialVersionUID = 9149516570281079484L;
    /**
     * 列名
     */
    private String attributeName;
    /**
     * 对应数据库字段名
     */
    private String fieldName;
    /**
     * 中文名
     */
    private String chineseName;
    /**
     * 备注
     */
    private String remark;
    /**
     * 列数据类型
     */
    private String columnType;
    /**
     * 列别名
     */
    private String columnAliases;
    /**
     * 数据字典
     */
    private String dataDictionary;
    /**
     * 常量
     */
    private String constant;
    /**
     * 是否同步
     */
    private Boolean isSynchronization;
    /**
     * 是否组织选择框
     */
    private Boolean isOrganizeSelectionBox;
    /**
     * 对应实体类名
     */
    private String entityName;

    @UnCloneable
    private SystemTable systemTable;

    /**
     * @return the attributeName
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * @param attributeName 要设置的attributeName
     */
    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    /**
     * @return the chineseName
     */
    public String getChineseName() {
        return chineseName;
    }

    /**
     * @param chineseName 要设置的chineseName
     */
    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark 要设置的remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * @return the systemTable
     */
    @ManyToOne
    @JoinColumn(name = "system_table_uuid")
    @LazyToOne(LazyToOneOption.PROXY)
    @Fetch(FetchMode.SELECT)
    public SystemTable getSystemTable() {
        return systemTable;
    }

    /**
     * @param systemTable 要设置的systemTable
     */
    public void setSystemTable(SystemTable systemTable) {
        this.systemTable = systemTable;
    }

    /**
     * @return the columnType
     */
    public String getColumnType() {
        return columnType;
    }

    /**
     * @param columnType 要设置的columnType
     */
    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    /**
     * @return the columnAliases
     */
    public String getColumnAliases() {
        return columnAliases;
    }

    /**
     * @param columnAliases 要设置的columnAliases
     */
    public void setColumnAliases(String columnAliases) {
        this.columnAliases = columnAliases;
    }

    /**
     * @return the dataDictionary
     */
    public String getDataDictionary() {
        return dataDictionary;
    }

    /**
     * @param dataDictionary 要设置的dataDictionary
     */
    public void setDataDictionary(String dataDictionary) {
        this.dataDictionary = dataDictionary;
    }

    /**
     * @return the constant
     */
    public String getConstant() {
        return constant;
    }

    /**
     * @param constant 要设置的constant
     */
    public void setConstant(String constant) {
        this.constant = constant;
    }

    /**
     * @return the isOrganizeSelectionBox
     */
    public Boolean getIsOrganizeSelectionBox() {
        return isOrganizeSelectionBox;
    }

    /**
     * @param isOrganizeSelectionBox 要设置的isOrganizeSelectionBox
     */
    public void setIsOrganizeSelectionBox(Boolean isOrganizeSelectionBox) {
        this.isOrganizeSelectionBox = isOrganizeSelectionBox;
    }

    /**
     * @return the entityName
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * @param entityName 要设置的entityName
     */
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Boolean getIsSynchronization() {
        return isSynchronization;
    }

    public void setIsSynchronization(Boolean isSynchronization) {
        this.isSynchronization = isSynchronization;
    }

}
