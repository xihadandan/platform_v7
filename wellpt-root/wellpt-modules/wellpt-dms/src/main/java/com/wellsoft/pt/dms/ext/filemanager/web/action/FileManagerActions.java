/*
 * @(#)Jan 23, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.filemanager.web.action;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Jan 23, 2018.1	zhulh		Jan 23, 2018		Create
 * </pre>
 * @date Jan 23, 2018
 */
public interface FileManagerActions {

    // 表单操作
    // 新建文档
    public static final String DYFORM_ACTION_CREATE = "btn_file_manager_dyform_create";

    // 获取表单文档数据
    public static final String DYFORM_ACTION_GET = "btn_file_manager_dyform_get";

    // 编辑
    public static final String DYFORM_ACTION_EDIT = "btn_file_manager_dyform_edit";

    // 保存正稿
    public static final String DYFORM_ACTION_SAVE_AS_NORMAL = "btn_file_manager_dyform_save_as_normal";

    // 保存草稿
    public static final String DYFORM_ACTION_SAVE_AS_DRAFT = "btn_file_manager_dyform_save_as_draft";

    // 套打
    public static final String DYFORM_ACTION_AS_PRINT = "btn_file_manager_dyform_as_print";

    // 删除
    public static final String DYFORM_ACTION_DELETE = "btn_file_manager_dyform_delete";

    // 还原
    public static final String DYFORM_ACTION_RESTORE = "btn_file_manager_dyform_restore";

    // 永久删除
    public static final String DYFORM_ACTION_PHYSICAL_DELETE = "btn_file_manager_dyform_physical_delete";

    // 视图列表操作
    // 批量套打
    public static final String LIST_VIEW_ACTION__AS_PRINTS = "btn_file_manager_list_view_as_prints";
    // 新建文档
    public static final String LIST_VIEW_ACTION_CREATE_DOCUMENT = "btn_file_manager_list_view_create_document";
    // 通过数据UUID删除
    public static final String LIST_VIEW_ACTION_LOGICAL_DELETE_BY_DATA_UUID = "btn_file_manager_list_view_logical_delete_by_data_uuid";
    // 通过数据UUID还原
    public static final String LIST_VIEW_ACTION_RESTORE_BY_DATA_UUID = "btn_file_manager_list_view_restore_by_data_uuid";
    // 通过数据UUID永久删除
    public static final String LIST_VIEW_ACTION_PHYSICAL_DELETE_BY_DATA_UUID = "btn_file_manager_list_view_physical_delete_by_data_uuid";

    // 删除
    public static final String LIST_VIEW_ACTION_LOGICAL_DELETE = "btn_file_manager_list_view_logical_delete";
    // 还原
    public static final String LIST_VIEW_ACTION_RESTORE = "btn_file_manager_list_view_restore";
    // 永久删除
    public static final String LIST_VIEW_ACTION_PHYSICAL_DELETE = "btn_file_manager_list_view_physical_delete";

}
