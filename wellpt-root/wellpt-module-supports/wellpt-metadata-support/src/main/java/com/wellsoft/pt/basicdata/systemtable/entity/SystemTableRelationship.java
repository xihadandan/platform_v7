package com.wellsoft.pt.basicdata.systemtable.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Description: 系统表关系实体类
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-25.1	zhouyq		2013-4-25		Create
 * </pre>
 * @date 2013-4-25
 */
@Entity
@Table(name = "cd_system_table_relationship")
@DynamicUpdate
@DynamicInsert
public class SystemTableRelationship extends IdEntity {
    private static final long serialVersionUID = 2943854786118950658L;

    /**
     * 主表名
     */
    private String mainTableName;
    /**
     * 从表名
     */
    private String secondaryTableName;
    /**
     * 主从表关系
     */
    private String tableRelationship;
    /**
     * 关联属性
     */
    private String associatedAttributes;

    @UnCloneable
    private SystemTable systemTable;

    /**
     * @return the mainTableName
     */
    public String getMainTableName() {
        return mainTableName;
    }

    /**
     * @param mainTableName 要设置的mainTableName
     */
    public void setMainTableName(String mainTableName) {
        this.mainTableName = mainTableName;
    }

    /**
     * @return the secondaryTableName
     */
    public String getSecondaryTableName() {
        return secondaryTableName;
    }

    /**
     * @param secondaryTableName 要设置的secondaryTableName
     */
    public void setSecondaryTableName(String secondaryTableName) {
        this.secondaryTableName = secondaryTableName;
    }

    /**
     * @return the tableRelationship
     */
    public String getTableRelationship() {
        return tableRelationship;
    }

    /**
     * @param tableRelationship 要设置的tableRelationship
     */
    public void setTableRelationship(String tableRelationship) {
        this.tableRelationship = tableRelationship;
    }

    /**
     * @return the associatedAttributes
     */
    public String getAssociatedAttributes() {
        return associatedAttributes;
    }

    /**
     * @param associatedAttributes 要设置的associatedAttributes
     */
    public void setAssociatedAttributes(String associatedAttributes) {
        this.associatedAttributes = associatedAttributes;
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

}
