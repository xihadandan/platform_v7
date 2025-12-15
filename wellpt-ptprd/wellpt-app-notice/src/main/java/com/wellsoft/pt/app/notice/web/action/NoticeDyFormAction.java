/*
 * @(#)May 22, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.notice.web.action;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.app.notice.support.NoticeConstants;
import com.wellsoft.pt.app.notice.support.NoticeUtils;
import com.wellsoft.pt.dms.core.annotation.Action;
import com.wellsoft.pt.dms.core.annotation.ActionConfig;
import com.wellsoft.pt.dms.core.support.DyFormActionData;
import com.wellsoft.pt.dms.core.support.DyformDocumentData;
import com.wellsoft.pt.dms.core.web.action.ActionResult;
import com.wellsoft.pt.dms.ext.dyform.web.action.DyFormActions;
import com.wellsoft.pt.dms.ext.dyform.web.action.DyFormGetAction;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.template.TemplateEngine;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import com.wellsoft.pt.message.entity.MessageTemplate;
import com.wellsoft.pt.message.facade.service.MessageClientApiFacade;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import ognl.OgnlException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.Map.Entry;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * May 22, 2017.1	zhulh		May 22, 2017		Create
 * </pre>
 * @date May 22, 2017
 */
@Action("平台公告_单据操作")
public class NoticeDyFormAction extends DyFormGetAction {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -726630173040724517L;

    @Autowired
    private OrgApiFacade orgApiFacade;

    @Autowired
    private DyFormFacade dyFormApiFacade;

    @Autowired
    private MessageClientApiFacade messageClientApiFacade;

    /**
     * @return
     */
    @ActionConfig(name = "加载数据", id = "bnt_notice_dyform_get_data")
    @ResponseBody
    public DyformDocumentData getData(@RequestBody DyFormActionData dyFormActionData, HttpServletRequest request) {
        DyformDocumentData documentData = super.actionPerformed(dyFormActionData);
        DyFormData dyFormData = documentData.getDyFormData();
        String epNoticeModel = dyFormActionData.getExtraString("ep_notice_model");
        // 发布状态为1，清空操作按钮
        if (dyFormData.isFieldExist("publish_status") && !StringUtils.equals("1", epNoticeModel)) {
            Object publishStatus = dyFormData.getFieldValue("publish_status");
            if (publishStatus != null && "1".equalsIgnoreCase(publishStatus.toString())) {
                documentData.getActions().clear();

                Object creator = dyFormData.getFieldValue("creator");
                if (SpringSecurityUtils.getCurrentUserId().equals(creator)) {
                    // 可编辑
                    documentData.getActions().add(getActionContext().buildAction("bnt_notice_dyform_eidt"));
                }
                // 添加查看阅读记录
                documentData.getActions().add(getActionContext().buildAction(DyFormActions.ACTION_VIEW_READ_RECORD));

                // 显示为文本
                documentData.putExtra("displayAsLabel", true);
            } else if (publishStatus != null && "-1".equalsIgnoreCase(publishStatus.toString())) {
                // 显示为文本
                documentData.putExtra("displayAsLabel", true);
            }
        }
        // 正式发布后没有保存按钮
        if (dyFormData.isFieldExist("publish_status")) {
            Object publishStatus = dyFormData.getFieldValue("publish_status");
            if (publishStatus != null && StringUtils.equals("1", publishStatus.toString())) {
                getActionContext().removeAction(DyFormActions.ACTION_SAVE);
            }
        }
        return documentData;
    }

    /**
     * @param dyFormActionData
     * @return
     */
    @ActionConfig(name = "发布", validate = true)
    @ResponseBody
    public ActionResult publish(@RequestBody DyFormActionData dyFormActionData) {
        DyFormData dyFormData = dyFormActionData.getDyFormData();
        if (dyFormData.isFieldExist("publish_status")) {
            dyFormData.setFieldValue("publish_status", "1");
        }
        String dataUuid = dyFormApiFacade.saveFormData(dyFormData);
        dyFormActionData.setDataUuid(dataUuid);
        // 短信发送通知
        sendSmsNotice(dyFormActionData);
        ActionResult actionResult = createActionResult();
        actionResult.setMsg("发布成功!");
        actionResult.setClose(true);
        return actionResult;
    }

    /**
     * @param dyFormActionData
     * @return
     */
    @ActionConfig(name = "编辑", id = "bnt_notice_dyform_eidt", executeJsModule = "AppNoticeDmsDyformEditAction")
    @ResponseBody
    public ActionResult edit(@RequestBody DyFormActionData dyFormActionData) {
        ActionResult actionResult = createActionResult();
        actionResult.addAppendUrlParam("ep_notice_model", "1");
        actionResult.setRefresh(true);
        actionResult.setShowMsg(false);
        return actionResult;
    }

    /**
     * @param dyFormActionData
     * @return
     */
    @ActionConfig(name = "逻辑删除")
    @ResponseBody
    public ActionResult logicDelete(@RequestBody DyFormActionData dyFormActionData) {
        DyFormData dyFormData = dyFormActionData.getDyFormData();
        // 删除权限检查
        if (!NoticeUtils.isNoticeCreatorOrAdmin(dyFormData)) {
            throw new RuntimeException("您不是公告的发布人或公告管理员，不能删除公告!");
        }

        if (dyFormData.isFieldExist("publish_status")) {
            dyFormData.setFieldValue("publish_status", "-1");
        }
        dyFormApiFacade.saveFormData(dyFormData);

        ActionResult actionResult = createActionResult();
        actionResult.setMsg("删除成功!");
        actionResult.setClose(true);
        return actionResult;
    }

    /**
     * @param dyFormActionData
     * @return
     */
    @ActionConfig(name = "逻辑删除_还原")
    @ResponseBody
    public ActionResult restore(@RequestBody DyFormActionData dyFormActionData) {
        DyFormData dyFormData = dyFormActionData.getDyFormData();
        if (dyFormData.isFieldExist("publish_status")) {
            dyFormData.setFieldValue("publish_status", "1");
        }
        String dataUuid = dyFormApiFacade.saveFormData(dyFormData);
        dyFormActionData.setDataUuid(dataUuid);
        ActionResult actionResult = createActionResult();
        actionResult.setMsg("还原成功!");
        actionResult.setClose(true);
        return actionResult;
    }

    /**
     * @param dyFormActionData
     * @return
     * @throws OgnlException
     */
    @ActionConfig(name = "发送短信", validate = true)
    @ResponseBody
    public ActionResult sendSmsNotice(@RequestBody DyFormActionData dyFormActionData) {
        // 判断是否配置有短信发送
        String templateId = Config.getValue(NoticeConstants.SMS_TEMPLATE_ID_KEY);
        try {
            DyFormData dyFormData = dyFormActionData.getDyFormData();
            Map<String, Object> rootObject = new HashMap<String, Object>();
            rootObject.put("dyform", dyFormData.getFormDataOfMainform());
            TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();
            templateId = templateEngine.process(templateId, rootObject);
        } catch (Exception ex) {
            templateId = null;
            logger.warn(ex.getMessage(), ex);
        }
        if (StringUtils.isBlank(templateId)) {
            return null;
        }
        // 判断是否需要发送（发布对象和只通知一次）
        String formUuid = dyFormActionData.getFormUuid();
        String dataUuid = dyFormActionData.getDataUuid();
        DyFormData dyFormData = dyFormApiFacade.getDyFormData(formUuid, dataUuid);
        String noticeValue = (String) dyFormData.getFieldValue("notice_object");// DYFORM_FIELD_NOTICE_VALUE
        String noticeContent = (String) dyFormData.getFieldValue("notice_content");
        String hiddenInput = "data-sended-object=\"" + noticeValue + "\"";// 已经发送
        if (StringUtils.isBlank(noticeValue) || StringUtils.isBlank(noticeContent)) {
            return null;
        }
        boolean sendOnce = Boolean.valueOf(Config.getValue(NoticeConstants.SMS_SEND_ONCE_KEY));
        if (sendOnce && noticeContent.contains(hiddenInput)) {
            return null;
        } else if (sendOnce) {
            hiddenInput = "<p " + hiddenInput + " style=\"display:none;\"></p>";
            dyFormData.setFieldValue("notice_content", noticeContent + hiddenInput);
        }
        // 保存推送标志位,防止多次推送。需要重新推送时,在源码编辑中去掉hiddenInput
        dyFormApiFacade.saveFormData(dyFormData);
        // 解析动态表单的字段值，包括部门、用户ID
        String[] values = noticeValue.split(Separator.SEMICOLON.getValue());
        HashMap<String, String> userMap = orgApiFacade.getUsersByOrgIds(Arrays.asList(values));
        Collection<String> userIds = new HashSet<String>();
        for (Entry<String, String> entry : userMap.entrySet()) {
            userIds.add(entry.getValue());
        }
        if (userIds.isEmpty()) {
            return null;
        }
        Map<Object, Object> dytableMap = (Map) dyFormData.getFormDataOfMainform();
        MessageTemplate entity = new MessageTemplate();
        messageClientApiFacade.send(templateId, entity, dytableMap, userIds);
        ActionResult actionResult = createActionResult();
        actionResult.setMsg("短信发送成功");
        actionResult.setClose(false);
        actionResult.setShowMsg(true);
        actionResult.setRefresh(false);
        return actionResult;
    }
}
