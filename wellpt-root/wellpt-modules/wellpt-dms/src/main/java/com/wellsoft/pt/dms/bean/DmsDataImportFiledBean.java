/*
 * @(#)2018年9月5日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.bean;

import java.io.Serializable;

/**
 * Description: 如何描述该类
 *
 * @author {zhongwd}
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年9月5日.1	{zhongwd}		2018年9月5日		Create
 * </pre>
 * @date 2018年9月5日
 */
public class DmsDataImportFiledBean implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 5936108723861580831L;
    private String uuid;
    //字段名称
    private String name;
    //第几列
    private String cell;
    //校验规则
    private DmsDataImportFiledRuleBean rule;
    //默认值
    private DmsDataImportFiledDefaultsBean defaults;
    //是否选中
    private boolean checked;

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid 要设置的uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
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
     * @return the cell
     */
    public String getCell() {
        return cell;
    }

    /**
     * @param cell 要设置的cell
     */
    public void setCell(String cell) {
        this.cell = cell;
    }

    /**
     * @return the rule
     */
    public DmsDataImportFiledRuleBean getRule() {
        return rule;
    }

    /**
     * @param rule 要设置的rule
     */
    public void setRule(DmsDataImportFiledRuleBean rule) {
        this.rule = rule;
    }

    /**
     * @return the defaults
     */
    public DmsDataImportFiledDefaultsBean getDefaults() {
        return defaults;
    }

    /**
     * @param defaults 要设置的defaults
     */
    public void setDefaults(DmsDataImportFiledDefaultsBean defaults) {
        this.defaults = defaults;
    }

    /**
     * @return the checked
     */
    public boolean getChecked() {
        return checked;
    }

    /**
     * @param checked 要设置的checked
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

}
