/*
 * @(#)Mar 6, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.listview.web.action;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Mar 6, 2017.1	zhulh		Mar 6, 2017		Create
 * </pre>
 * @date Mar 6, 2017
 */
public interface ListViewActions {
    // 新增
    public static final String ACTION_ADD = "btn_list_view_add";
    // 编辑
    public static final String ACTION_EDIT = "btn_list_view_edit";
    // 查看
    public static final String ACTION_VIEW = "btn_list_view_view";
    // 复制
    public static final String ACTION_COPY = "btn_list_view_copy";
    // 送审批
    public static final String ACTION_SEND_TO_APPROVE = "btn_list_view_send_to_approve";
    // 逻辑删除
    public static final String ACTION_LOGIC_DELETE = "btn_list_view_logic_delete";
    // 表格套打
    public static final String ACTION_TABLE_PRINT = "btn_list_view_table_print";
    // 删除
    public static final String ACTION_DELETE = "btn_list_view_delete";
    // 删除数据的所有版本
    public static final String ACTION_DELETE_ALL_DATA_VERSION = "btn_list_view_delete_all_data_version";
    // 置顶
    public static final String ACTION_STICK = "btn_list_view_stick";
    // 取消置顶
    public static final String ACTION_UNSTICK = "btn_list_view_unstick";
    // 新增关闭
    public static final String ACTION_ADD_CLOSE = "btn_dyform_save_with_close";
}
