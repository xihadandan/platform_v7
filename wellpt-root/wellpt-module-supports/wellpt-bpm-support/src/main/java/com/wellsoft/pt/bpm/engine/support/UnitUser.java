/*
 * @(#)2015-4-7 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 组织选择框办理人
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-4-7.1	zhulh		2015-4-7		Create
 * </pre>
 * @date 2015-4-7
 */
public class UnitUser implements Serializable {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 2951279034367392119L;

    // 限制的人员ID
    private List<String> limitIds = new ArrayList<String>(0);
    // 限制的人员名称
    private List<String> limitNames = new ArrayList<String>(0);

    // 仅一个目标域值时，以;分割的文本值，当为多个目标域值时则为与psTargetNames一一对应的数组成员值。
    private List<String> initNames = new ArrayList<String>(0);
    // 仅一个目标域值时，以;分割的文本值，当为多个目标域值时则为与psTargetNames一一对应的数组成员值。
    private List<String> initIds = new ArrayList<String>(0);
    // 标题
    private String title;
    // 是否多选，默认为true
    private boolean multiple = true;
    // 限制用户只能选择的节点类型(默认"1")；0-都不能选择，1-都可以选择，2-仅允许选择部门，4-仅允许选择人员，8-表示仅允许选择公共群组，32-标识仅允许选择职位 其他值为0/1/2/4/8/16/32相加组合。
    private String selectType = "4";
    // 是否显示类型选择下拉框。默认显示。
    private boolean showType = false;
    // 备选值：集团|All;我的单位|MyUnit;我的部门|MyDept;我的领导|MyLeader;我的下属|MyUnderling;公共群组|PublicGroup;个人群组|PrivateGroup;上级部门|MyParentDept;在线人员|LoginUser;部门树|Dept也可以是节点值（可以多值，且要求支持路径值或者id），用来选择此节点树下的值。默认为MyUnit.，当值为Dept，不显示姓氏下拉框。
    private String type = "MyUnit";

    /**
     * @return the limitIds
     */
    public List<String> getLimitIds() {
        return limitIds;
    }

    /**
     * @param limitIds 要设置的limitIds
     */
    public void setLimitIds(List<String> limitIds) {
        this.limitIds = limitIds;
    }

    /**
     * @return the limitNames
     */
    public List<String> getLimitNames() {
        return limitNames;
    }

    /**
     * @param limitNames 要设置的limitNames
     */
    public void setLimitNames(List<String> limitNames) {
        this.limitNames = limitNames;
    }

    /**
     * @return the initNames
     */
    public List<String> getInitNames() {
        return initNames;
    }

    /**
     * @param initNames 要设置的initNames
     */
    public void setInitNames(List<String> initNames) {
        this.initNames = initNames;
    }

    /**
     * @return the initIds
     */
    public List<String> getInitIds() {
        return initIds;
    }

    /**
     * @param initIds 要设置的initIds
     */
    public void setInitIds(List<String> initIds) {
        this.initIds = initIds;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title 要设置的title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the multiple
     */
    public boolean isMultiple() {
        return multiple;
    }

    /**
     * @param multiple 要设置的multiple
     */
    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    /**
     * @return the selectType
     */
    public String getSelectType() {
        return selectType;
    }

    /**
     * @param selectType 要设置的selectType
     */
    public void setSelectType(String selectType) {
        this.selectType = selectType;
    }

    /**
     * @return the showType
     */
    public boolean isShowType() {
        return showType;
    }

    /**
     * @param showType 要设置的showType
     */
    public void setShowType(boolean showType) {
        this.showType = showType;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(String type) {
        this.type = type;
    }

}
