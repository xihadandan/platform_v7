/*
 * @(#)2014-7-31 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datasource.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.validator.MaxLength;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Description: 数据源定义类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-7-31.1	wubin		2014-7-31		Create
 * </pre>
 * @date 2014-7-31
 */
@Entity
@Table(name = "data_source_definition")
@DynamicUpdate
@DynamicInsert
public class DataSourceDefinition extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 6895067840561307568L;
    //jqgrid行标识
    private String id;
    //数据源的编号
    private String dataSourceNum;
    //数据源的id
    @NotBlank
    private String dataSourceId;
    //数据源的名称
    @NotBlank
    private String dataSourceName;
    //数据源的类型名称
    private String dataSourceTypeName;
    //数据源的类型id
    private String dataSourceTypeId;
    //内部数据源的类别
    private String inDataScope;
    //外部数据源的类别
    private String outDataScope;
    //选择数据的文本(数据库表、数据视图)
    private String chooseDataText;
    //选择数据的id(数据库表、数据视图)
    private String chooseDataId;
    //sql\hql语句
    @MaxLength(max = 2048)
    private String sqlOrHqlText;
    //带ACL的hql语句
    private String aclHqlDataText;
    //默认搜索条件
    private String searchCondition;
    //选择的外部数据源的名字
    private String outDataSourceName;
    //选择的外部数据源的Id
    private String outDataSourceId;
    //数据接口名字
    private String dataInterfaceName;
    //数据接口Id
    private String dataInterfaceId;
    // 角色类型（个人，群组，群组人员，所有）
    private String roleValue;
    // 角色值（如角色为待办，则对应数据库字典中的一个值）
    private String roleType;
    //角色显示名
    private String roleName;

    @UnCloneable
    private Set<DataSourceColumn> dataSourceColumns = new LinkedHashSet<DataSourceColumn>();

    /**
     * @return the roleValue
     */
    public String getRoleValue() {
        return roleValue;
    }

    /**
     * @param roleValue 要设置的roleValue
     */
    public void setRoleValue(String roleValue) {
        this.roleValue = roleValue;
    }

    /**
     * @return the roleType
     */
    public String getRoleType() {
        return roleType;
    }

    /**
     * @param roleType 要设置的roleType
     */
    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    /**
     * @return the roleName
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * @param roleName 要设置的roleName
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    /**
     * @return the inDataScope
     */
    public String getInDataScope() {
        return inDataScope;
    }

    /**
     * @param inDataScope 要设置的inDataScope
     */
    public void setInDataScope(String inDataScope) {
        this.inDataScope = inDataScope;
    }

    /**
     * @return the outDataScope
     */
    public String getOutDataScope() {
        return outDataScope;
    }

    /**
     * @param outDataScope 要设置的outDataScope
     */
    public void setOutDataScope(String outDataScope) {
        this.outDataScope = outDataScope;
    }

    /**
     * @return the dataInterfaceName
     */
    public String getDataInterfaceName() {
        return dataInterfaceName;
    }

    /**
     * @param dataInterfaceName 要设置的dataInterfaceName
     */
    public void setDataInterfaceName(String dataInterfaceName) {
        this.dataInterfaceName = dataInterfaceName;
    }

    /**
     * @return the dataInterfaceId
     */
    public String getDataInterfaceId() {
        return dataInterfaceId;
    }

    /**
     * @param dataInterfaceId 要设置的dataInterfaceId
     */
    public void setDataInterfaceId(String dataInterfaceId) {
        this.dataInterfaceId = dataInterfaceId;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the dataSourceNum
     */
    public String getDataSourceNum() {
        return dataSourceNum;
    }

    /**
     * @param dataSourceNum 要设置的dataSourceNum
     */
    public void setDataSourceNum(String dataSourceNum) {
        this.dataSourceNum = dataSourceNum;
    }

    /**
     * @return the dataSourceId
     */
    public String getDataSourceId() {
        return dataSourceId;
    }

    /**
     * @param dataSourceId 要设置的dataSourceId
     */
    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    /**
     * @return the dataSourceName
     */
    public String getDataSourceName() {
        return dataSourceName;
    }

    /**
     * @param dataSourceName 要设置的dataSourceName
     */
    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    /**
     * @return the dataSourceTypeName
     */
    public String getDataSourceTypeName() {
        return dataSourceTypeName;
    }

    /**
     * @param dataSourceTypeName 要设置的dataSourceTypeName
     */
    public void setDataSourceTypeName(String dataSourceTypeName) {
        this.dataSourceTypeName = dataSourceTypeName;
    }

    /**
     * @return the dataSourceTypeId
     */
    public String getDataSourceTypeId() {
        return dataSourceTypeId;
    }

    /**
     * @param dataSourceTypeId 要设置的dataSourceTypeId
     */
    public void setDataSourceTypeId(String dataSourceTypeId) {
        this.dataSourceTypeId = dataSourceTypeId;
    }

    /**
     * @return the chooseDataText
     */
    public String getChooseDataText() {
        return chooseDataText;
    }

    /**
     * @param chooseDataText 要设置的chooseDataText
     */
    public void setChooseDataText(String chooseDataText) {
        this.chooseDataText = chooseDataText;
    }

    /**
     * @return the chooseDataId
     */
    public String getChooseDataId() {
        return chooseDataId;
    }

    /**
     * @param chooseDataId 要设置的chooseDataId
     */
    public void setChooseDataId(String chooseDataId) {
        this.chooseDataId = chooseDataId;
    }

    /**
     * @return the sqlOrHqlText
     */
    public String getSqlOrHqlText() {
        return sqlOrHqlText;
    }

    /**
     * @param sqlOrHqlText 要设置的sqlOrHqlText
     */
    public void setSqlOrHqlText(String sqlOrHqlText) {
        this.sqlOrHqlText = sqlOrHqlText;
    }

    /**
     * @return the aclHqlDataText
     */
    public String getAclHqlDataText() {
        return aclHqlDataText;
    }

    /**
     * @param aclHqlDataText 要设置的aclHqlDataText
     */
    public void setAclHqlDataText(String aclHqlDataText) {
        this.aclHqlDataText = aclHqlDataText;
    }

    /**
     * @return the searchCondition
     */
    public String getSearchCondition() {
        return searchCondition;
    }

    /**
     * @param searchCondition 要设置的searchCondition
     */
    public void setSearchCondition(String searchCondition) {
        this.searchCondition = searchCondition;
    }

    /**
     * @return the outDataSourceName
     */
    public String getOutDataSourceName() {
        return outDataSourceName;
    }

    /**
     * @param outDataSourceName 要设置的outDataSourceName
     */
    public void setOutDataSourceName(String outDataSourceName) {
        this.outDataSourceName = outDataSourceName;
    }

    /**
     * @return the outDataSourceId
     */
    public String getOutDataSourceId() {
        return outDataSourceId;
    }

    /**
     * @param outDataSourceId 要设置的outDataSourceId
     */
    public void setOutDataSourceId(String outDataSourceId) {
        this.outDataSourceId = outDataSourceId;
    }

    /**
     * @return the dataSourceColumns
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "dataSourceDefinition")
    @Cascade({CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    public Set<DataSourceColumn> getDataSourceColumns() {
        return dataSourceColumns;
    }

    /**
     * @param dataSourceColumns 要设置的dataSourceColumns
     */
    public void setDataSourceColumns(Set<DataSourceColumn> dataSourceColumns) {
        this.dataSourceColumns = dataSourceColumns;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        DataSourceDefinition other = (DataSourceDefinition) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
