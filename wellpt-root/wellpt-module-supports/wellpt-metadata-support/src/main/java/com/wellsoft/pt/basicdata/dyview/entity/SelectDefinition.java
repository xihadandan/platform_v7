/*
 * @(#)2013-3-22 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.dyview.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Set;

/**
 * Description: 查询定义类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-22.1	wubin		2013-3-22		Create
 * </pre>
 * @date 2013-3-22
 */
@Entity
@Table(name = "dyview_select_definition")
@DynamicUpdate
@DynamicInsert
public class SelectDefinition extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 788898119388427742L;

    private Boolean forCondition; //是否按条件查询

    private Boolean forKeySelect; //是否按关键字查询

    private Boolean vagueKeySelect; //是否全视图模糊关键字查询

    private Boolean exactKeySelect; //是否根据配置的关键字进行精确查询

    private Boolean forTimeSolt;//按时间段查询

    private String searchField;//按时间段查询的时段

    private Boolean selectShow; //查询是否显示定义

    @UnCloneable
    private Set<ConditionType> conditionType; //查询的条件选项

    @UnCloneable
    private Set<ExactKeySelectCol> exactKeySelectCols; //精确查询的关键字

    @UnCloneable
    private ViewDefinition viewDefinition; // 所属的视图

    /**
     * @return the conditionType
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "selectDefinition")
    @Cascade({CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @OrderBy("sortOrder asc")
    public Set<ConditionType> getConditionType() {
        return conditionType;
    }

    /**
     * @param conditionType 要设置的conditionType
     */
    public void setConditionType(Set<ConditionType> conditionType) {
        this.conditionType = conditionType;
    }

    /**
     * @return the exactKeySelectCols
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "selectDefinition")
    @Cascade({CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    public Set<ExactKeySelectCol> getExactKeySelectCols() {
        return exactKeySelectCols;
    }

    /**
     * @param exactKeySelectCols 要设置的exactKeySelectCols
     */
    public void setExactKeySelectCols(Set<ExactKeySelectCol> exactKeySelectCols) {
        this.exactKeySelectCols = exactKeySelectCols;
    }

    /**
     * @return the forCondition
     */
    public Boolean getForCondition() {
        return forCondition;
    }

    /**
     * @param forCondition 要设置的forCondition
     */
    public void setForCondition(Boolean forCondition) {
        this.forCondition = forCondition;
    }

    /**
     * @return the forKeySelect
     */
    public Boolean getForKeySelect() {
        return forKeySelect;
    }

    /**
     * @param forKeySelect 要设置的forKeySelect
     */
    public void setForKeySelect(Boolean forKeySelect) {
        this.forKeySelect = forKeySelect;
    }

    /**
     * @return the vagueKeySelect
     */
    public Boolean getVagueKeySelect() {
        return vagueKeySelect;
    }

    /**
     * @param vagueKeySelect 要设置的vagueKeySelect
     */
    public void setVagueKeySelect(Boolean vagueKeySelect) {
        this.vagueKeySelect = vagueKeySelect;
    }

    /**
     * @return the exactKeySelect
     */
    public Boolean getExactKeySelect() {
        return exactKeySelect;
    }

    /**
     * @param exactKeySelect 要设置的exactKeySelect
     */
    public void setExactKeySelect(Boolean exactKeySelect) {
        this.exactKeySelect = exactKeySelect;
    }

    /**
     * @return the selectShow
     */
    public Boolean getSelectShow() {
        return selectShow;
    }

    /**
     * @param selectShow 要设置的selectShow
     */
    public void setSelectShow(Boolean selectShow) {
        this.selectShow = selectShow;
    }

    /**
     * @return the viewDefinition
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "view_def_uuid", nullable = false)
    public ViewDefinition getViewDefinition() {
        return viewDefinition;
    }

    /**
     * @param viewDefinition 要设置的viewDefinition
     */
    public void setViewDefinition(ViewDefinition viewDefinition) {
        this.viewDefinition = viewDefinition;
    }

    public Boolean getForTimeSolt() {
        return forTimeSolt;
    }

    public void setForTimeSolt(Boolean forTimeSolt) {
        this.forTimeSolt = forTimeSolt;
    }

    public String getSearchField() {
        return searchField;
    }

    public void setSearchField(String searchField) {
        this.searchField = searchField;
    }

}
