package com.wellsoft.pt.dms.ext.docexchanger.web.action;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/5/14
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/14    chenq		2018/5/14		Create
 * </pre>
 */
public interface DocExchangerActions {

    public static final String ACTION_NEW_DOC_EXCHANGER = "btn_list_view_add_doc_exchanger";//文档交换新增

    public static final String ACTION_SAVE_DOC_EXCHANGER = "btn_save_doc_exchanger";//文档交换保存

    public static final String ACTION_SAVE_VALIDATE_DOC_EXCHANGER = "btn_save_and_validate_doc_exchanger";//文档交换保存并校验

    public static final String ACTION_SEND_DOC_EXCHANGER = "btn_send_doc_exchanger";//文档交换发送

    public static final String ACTION_VIEW_DOC_EXCHANGER = "btn_list_view_doc_exchanger";//文档交换列表_查看操作

    public static final String ACTION_EDIT_DOC_EXCHANGER = "btn_list_edit_doc_exchanger";//文档交换列表_编辑操作

    public static final String ACTION_EXTRA_SEND_DOC_EXCHANGER = "btn_extra_send_doc_exchanger"; //补充发送

    public static final String ACTION_REVOKE_DOC_EXCHANGER = "btn_revoke_doc_exchanger"; //撤回

    public static final String ACTION_URGE_DOC_EXCHANGER = "btn_urge_doc_exchanger"; //催办

    public static final String ACTION_FINISH_DOC_EXCHANGER = "btn_finish_doc_exchanger"; //办结

    public static final String ACTION_FORWARD_DOC_EXCHANGER = "btn_forward_doc_exchanger"; //转发

    public static final String ACTION_RETURN_DOC_EXCHANGER = "btn_return_doc_exchanger"; //退回

    public static final String ACTION_BATCH_SIGN_DOC_EXCHANGER = "btn_batch_sign_doc_exchanger"; //签收

    public static final String ACTION_FEEDBACK_DOC_EXCHANGER = "btn_feedback_doc_exchanger"; //反馈意见

    public static final String ACTION_GET_DOC_EXCHANGER = "btn_get_doc_exchanger"; //加载数据

}
