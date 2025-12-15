/*
 * @(#)Feb 28, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.dyform.web.action;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Feb 28, 2017.1	zhulh		Feb 28, 2017		Create
 * </pre>
 * @date Feb 28, 2017
 */
public interface DyFormActions {
    // 获取数据
    public static final String ACTION_GET = "btn_dyform_get";

    // 编辑
    public static final String ACTION_EDIT = "btn_dyform_edit";

    // 保存数据
    public static final String ACTION_SAVE = "btn_dyform_save";

    // 保存并验证
    public static final String ACTION_SAVE_WIDTH_VALIDATE = "btn_dyform_save_with_validate";

    // 保存新版本
    public static final String ACTION_SAVE_NEW_VERSION = "btn_dyform_save_new_version";

    // 发布新版本并验证
    public static final String ACTION_SAVE_NEW_VERSION_WIDTH_VALIDATE = "btn_dyform_save_new_version_with_validate";

    // 保存新版本并添加备注
    public static final String ACTION_SAVE_NEW_VERSION_WITH_REMARK = "btn_dyform_save_new_version_with_remark";

    // 查看历史版本
    public static final String ACTION_VIEW_HISTORY_VERSION = "btn_dyform_view_history_version";

    // 送审批
    public static final String ACTION_SEND_TO_APPROVE = "btn_dyform_send_to_approve";

    // 逻辑删除数据
    public static final String ACTION_LOGIC_DELETE = "btn_dyform_logic_delete";

    // 删除数据
    public static final String ACTION_DELETE = "btn_dyform_delete";

    // 删除数据的所有版本
    public static final String ACTION_DELETE_ALL_VERSION = "btn_dyform_delete_all_version";

    // 关闭窗口
    public static final String ACTION_CLOSE = "btn_dyform_close";

    // 查看阅读记录
    public static final String ACTION_VIEW_READ_RECORD = "btn_dyform_view_read_record";

    // 套打
    public static final String ACTION_PRINT = "btn_dyform_print";

}
