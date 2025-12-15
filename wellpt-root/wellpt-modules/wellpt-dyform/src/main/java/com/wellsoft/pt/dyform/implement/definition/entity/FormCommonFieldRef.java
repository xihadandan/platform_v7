package com.wellsoft.pt.dyform.implement.definition.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 表单公共字段引用信息
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
@Table(name = "DYFORM_COMMON_FIELD_REF")
@DynamicUpdate
@DynamicInsert
public class FormCommonFieldRef extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 表单名字
     */
    private String name;
    /**
     * 所属模块
     */
    private String formUuid;
    /**
     * 表单显示名字
     */
    private String displayName;
    /**
     * 显示名称
     */
    private String commonFieldUuid;

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
     * @return the commonFieldUuid
     */
    public String getCommonFieldUuid() {
        return commonFieldUuid;
    }

    /**
     * @param commonFieldUuid 要设置的commonFieldUuid
     */
    public void setCommonFieldUuid(String commonFieldUuid) {
        this.commonFieldUuid = commonFieldUuid;
    }

}
