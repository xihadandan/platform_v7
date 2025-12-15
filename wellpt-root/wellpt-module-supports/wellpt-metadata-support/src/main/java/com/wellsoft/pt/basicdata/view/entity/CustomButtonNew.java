/*
 * @(#)2013-4-16 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.view.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Description: 如何描述该类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-16.1	wubin		2013-4-16		Create
 * </pre>
 * @date 2013-4-16
 */
@Deprecated
//@Entity
//@Table(name = "view_view_custom_button")
//@DynamicUpdate
//@DynamicInsert
public class CustomButtonNew extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 7347656012953278703L;

    private String name;//按钮名称

    private String code;//按钮ID

    private String jsContent;

    private String place;//按钮所在位置

    private String buttonGroup;

    private String resaultName;//资源名称

    private String constraint;//约束条件

    @UnCloneable
    private ViewDefinitionNew viewDefinitionNew; // 所属的视图

    public String getConstraint() {
        return constraint;
    }

    public void setConstraint(String constraint) {
        this.constraint = constraint;
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
     * @return the viewDefinitionNew
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "view_def_uuid", nullable = false)
    @LazyToOne(LazyToOneOption.PROXY)
    @Fetch(FetchMode.SELECT)
    public ViewDefinitionNew getViewDefinitionNew() {
        return viewDefinitionNew;
    }

    /**
     * @param viewDefinitionNew 要设置的viewDefinitionNew
     */
    public void setViewDefinitionNew(ViewDefinitionNew viewDefinitionNew) {
        this.viewDefinitionNew = viewDefinitionNew;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(String code) {
        this.code = code;
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
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        CustomButtonNew other = (CustomButtonNew) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    /**
     * @return the jsContent
     */
    public String getJsContent() {
        return jsContent;
    }

    /**
     * @param jsContent 要设置的jsContent
     */
    public void setJsContent(String jsContent) {
        this.jsContent = jsContent;
    }

    /**
     * @return the place
     */
    public String getPlace() {
        return place;
    }

    /**
     * @param place 要设置的place
     */
    public void setPlace(String place) {
        this.place = place;
    }

    /**
     * @return the buttonGroup
     */
    public String getButtonGroup() {
        return buttonGroup;
    }

    /**
     * @param buttonGroup 要设置的buttonGroup
     */
    public void setButtonGroup(String buttonGroup) {
        this.buttonGroup = buttonGroup;
    }

    public String getResaultName() {
        return resaultName;
    }

    public void setResaultName(String resaultName) {
        this.resaultName = resaultName;
    }

}
