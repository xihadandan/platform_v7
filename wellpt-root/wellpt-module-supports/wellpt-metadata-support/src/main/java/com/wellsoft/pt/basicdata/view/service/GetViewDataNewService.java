/*
 * @(#)2013-3-14 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.view.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.systemtable.entity.SystemTable;
import com.wellsoft.pt.basicdata.systemtable.entity.SystemTableAttribute;
import com.wellsoft.pt.basicdata.systemtable.entity.SystemTableRelationship;
import com.wellsoft.pt.basicdata.view.bean.ViewDefinitionNewBean;
import com.wellsoft.pt.basicdata.view.entity.ColumnDefinitionNew;
import com.wellsoft.pt.basicdata.view.entity.CustomButtonNew;
import com.wellsoft.pt.basicdata.view.provider.ViewColumnNew;
import com.wellsoft.pt.security.audit.entity.Resource;

import java.util.List;
import java.util.Map;
import java.util.Set;

//import com.wellsoft.pt.dyform.support.FieldDefinition;

/**
 * Description: 如何描述该类
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-14.1	Administrator		2013-3-14		Create
 * </pre>
 * @date 2013-3-14
 */
public interface GetViewDataNewService {

    //获取动态表单的具体字段信息,需要提供formuuid参数
    public abstract List getFormFields(String s, String s1);

    //获取所有的系统表单信息
    public abstract List getForms(String s, String id);

    //获取所有的动态表单信息
    public abstract List getDyForms(String s, String moduleId);

    //获取所有的数据导出模版
    public abstract List getAllExcelExportRules(String s);

    //获取初始值
    public abstract QueryItem getKeyValuePair(String s, String s1);

/*	//根据传入的formUuid来获取表单的字段信息
	public abstract List<FieldDefinition> getFieldByForm(String formUuid);*/

    //根据传入的tableUuid来获取系统表的字段信息
    public abstract List<SystemTableAttribute> getSystemTableColumns(String tableUuid);

    //根据传入的tableUuid来获取主表以及从表属性的集合
    public abstract List<SystemTableRelationship> getAttributesByrelationship(String tableUuid);

    //获得系统表数据
    public abstract SystemTable getSystemTable(String tableUuid);

    //根据传入的模块id获取视图的列字段信息
    public abstract List<ViewColumnNew> getViewColumns(String id);

    //根据传入的模块id获取视图的列字段信息树
    public abstract List<TreeNode> getViewColumnsTree(String s, String id);

    //根据传入的viewUuid来获取视图的列属性信息
    public abstract List<ColumnDefinitionNew> getFieldByView(String viewUuid);

    //获取自定义的按钮
    public abstract List<Resource> getCustomButtonByCode(String code);

    //获取有权限的自定义按钮
    public abstract void setCustomButtonRights(ViewDefinitionNewBean bean, Set<CustomButtonNew> customButtonNew);

    //获取特殊列值的数据集合(固定方法)
    public abstract List<Map<String, Object>> getSpecialFieldValues(String viewUuid, List<String> viewDataUuid,
                                                                    List<Map<String, Object>> viewDataArray);

    //获取特殊列值的数据集合接口(用户需实现该接口才能二次请求加载数据)
    public abstract Map<String, Object> getSpecialFieldValue(Map<String, Object> requestParams, String[] responseParams);
}
