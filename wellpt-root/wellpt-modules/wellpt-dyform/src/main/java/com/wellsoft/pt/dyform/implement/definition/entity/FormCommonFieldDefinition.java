package com.wellsoft.pt.dyform.implement.definition.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.json.JSONException;
import org.json.JSONObject;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Description: 表单公共字段字义
 *
 * @author qiufy
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年4月18日.1	qiufy		2019年4月18日		Create
 * </pre>
 * @date 2019年4月18日
 */
@Entity
@Table(name = "DYFORM_COMMON_FIELD_DEFINITION")
@DynamicUpdate
@DynamicInsert
@ApiModel("表单公共字段字义")
public class FormCommonFieldDefinition extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    /**
     * 作用域
     */
    @ApiModelProperty("作用域")
    private Integer scope;
    /**
     * 显示名称
     */
    @ApiModelProperty("显示名称")
    private String displayName;
    /**
     * 字段编码
     */
    @ApiModelProperty("字段编码")
    private String name;
    /**
     * 定义数据
     */
    @ApiModelProperty("定义数据")
    private String definitionJson;
    /**
     * 控件类型
     */
    @ApiModelProperty("控件类型")
    private String controlType;
    /**
     * 控件类型名称
     */
    @ApiModelProperty("控件类型名称")
    private String controlTypeName;
    /**
     * 分类UUID
     */
    @ApiModelProperty("分类UUID")
    private String categoryUuid;
    /**
     * 分类名称
     */
    @ApiModelProperty("分类名称")
    private String categoryName;
    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String notes;
    /**
     * 所属模块
     */
    @ApiModelProperty("所属模块")
    private String moduleId;
    /**
     * 模块名称
     */
    @ApiModelProperty("模块名称")
    private String moduleName;

    @Transient
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private transient JSONObject definitionJsonObject;

    /**
     * @return the moduleName
     */
    public String getModuleName() {
        return moduleName;
    }

    /**
     * @param moduleName 要设置的moduleName
     */
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * @return the notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * @param notes 要设置的notes
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * @return the scope
     */
    public Integer getScope() {
        return scope;
    }

    /**
     * @param scope 要设置的scope
     */
    public void setScope(Integer scope) {
        this.scope = scope;
    }

    /**
     * @return the moduleId
     */
    public String getModuleId() {
        return moduleId;
    }

    /**
     * @param moduleId 要设置的moduleId
     */
    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @param displayName 要设置的displayName
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

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
     * @return the definitionJson
     */
    public String getDefinitionJson() {
        return definitionJson;
    }

    /**
     * @param definitionJson 要设置的definitionJson
     */
    public void setDefinitionJson(String definitionJson) {
        this.definitionJson = definitionJson;
    }

    /**
     * @return the controlType
     */
    public String getControlType() {
        return controlType;
    }

    /**
     * @param controlType 要设置的controlType
     */
    public void setControlType(String controlType) {
        this.controlType = controlType;
    }

    /**
     * @return the categoryUuid
     */
    public String getCategoryUuid() {
        return categoryUuid;
    }

    /**
     * @param categoryUuid 要设置的categoryUuid
     */
    public void setCategoryUuid(String categoryUuid) {
        this.categoryUuid = categoryUuid;
    }

    /**
     * @return the controlTypeName
     */
    public String getControlTypeName() {
        return controlTypeName;
    }

    /**
     * @param controlTypeName 要设置的controlTypeName
     */
    public void setControlTypeName(String controlTypeName) {
        this.controlTypeName = controlTypeName;
    }

    /**
     * @return the categoryName
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * @param categoryName 要设置的categoryName
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * @return the definitionJsonObject
     */
    @Transient
    @JsonIgnore
    public JSONObject getDefinitionJsonObject() {
        if (definitionJsonObject == null && StringUtils.isNotBlank(getDefinitionJson())) {
            try {
                definitionJsonObject = new JSONObject(getDefinitionJson());
            } catch (JSONException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }
        return definitionJsonObject;
    }

    /**
     * @param definitionJsonObject 要设置的definitionJsonObject
     */
    public void setDefinitionJsonObject(JSONObject definitionJsonObject) {
        this.definitionJsonObject = definitionJsonObject;
    }

}
