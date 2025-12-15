package com.wellsoft.pt.dyform.implement.definition.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年5月21日.1	zhongzh		2019年5月21日		Create
 * </pre>
 * @date 2019年5月21日
 */
@Entity
@Table(name = "DYFORM_COMMON_FIELD_CATEGORY")
@DynamicUpdate
@DynamicInsert
public class FormCommonFieldCategory extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * scope:作用域
     */
    private Integer scope;

    private String moduleId;

    private String categoryName;

    private Integer seq;

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

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }
}
