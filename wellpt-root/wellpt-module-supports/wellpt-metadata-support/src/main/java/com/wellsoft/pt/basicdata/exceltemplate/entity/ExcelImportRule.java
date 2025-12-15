package com.wellsoft.pt.basicdata.exceltemplate.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

/**
 * Description: excel导入规则实体类
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-22.1	zhouyq		2013-4-22		Create
 * </pre>
 * @date 2013-4-22
 */
@Entity
@Table(name = "cd_excel_import_rule")
@DynamicUpdate
@DynamicInsert
public class ExcelImportRule extends TenantEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    @NotBlank
    private String name;
    /**
     * 排序
     */
    private Integer sortOrder;
    /**
     * 编号
     */
    private String code;
    /**
     * ID
     */
    @NotBlank
    private String id;
    /**
     * 开始行
     */
    @NotNull
    @Digits(fraction = 0, integer = Integer.MAX_VALUE)
    private Integer startRow;
    /**
     * 返回类型 实体(entity) 动态表单(formdefinition)
     */
    private String type;
    /**
     * 对应实体、动态表单UUID
     **/
    private String entity;
    /**
     * 对应实体、动态表单名称
     **/
    private String entityName;
    /**
     * 模版文件的uuid
     **/
    private String fileUuid;
    @UnCloneable
    private Set<ExcelColumnDefinition> excelColumnDefinitions = new HashSet<ExcelColumnDefinition>();

    private String moduleId;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the sortOrder
     */
    public Integer getSortOrder() {
        return sortOrder;
    }

    /**
     * @param sortOrder 要设置的sortOrder
     */
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the startRow
     */
    public Integer getStartRow() {
        return startRow;
    }

    /**
     * @param startRow 要设置的startRow
     */
    public void setStartRow(Integer startRow) {
        this.startRow = startRow;
    }

    /**
     * @return the excelColumnDefinitions
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "excelImportRule")
    @Cascade(value = {CascadeType.ALL})
    public Set<ExcelColumnDefinition> getExcelColumnDefinitions() {
        return excelColumnDefinitions;
    }

    /**
     * @param excelColumnDefinitions 要设置的excelColumnDefinitions
     */
    public void setExcelColumnDefinitions(Set<ExcelColumnDefinition> excelColumnDefinitions) {
        this.excelColumnDefinitions = excelColumnDefinitions;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the fileUuid
     */
    public String getFileUuid() {
        return fileUuid;
    }

    /**
     * @param fileUuid 要设置的fileUuid
     */
    public void setFileUuid(String fileUuid) {
        this.fileUuid = fileUuid;
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

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }
}
