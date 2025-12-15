/*
 * @(#)2016年11月1日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.support;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.wellsoft.pt.basicdata.datastore.support.RendererParam;
import com.wellsoft.pt.basicdata.datastore.support.renderer.AbstractDataStoreRenderer;
import com.wellsoft.pt.webmail.bean.WmMailBoxInfoBean;
import com.wellsoft.pt.webmail.service.WmMailboxInfoService;
import com.wellsoft.pt.webmail.service.WmMailboxService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Description: 邮件图标数据渲染，根据邮件的状态等情况展示邮件行的不同图标：已读、未读、是否回复、是否有附件等
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月16日.1	chenqiong		2018年3月16日		Create
 * </pre>
 * @date 2018年3月16日
 */
@Component
public class WebmailIconDataStoreRenderer extends AbstractDataStoreRenderer {

    private static final Map<String, String> ICON_TITLE_MAP = Maps.newHashMap();
    private static final String MAIL_READED_CLASS = "mail_icon mail_readed";
    private static final String MAIL_NOT_READ_CLASS = "mail_icon mail_not_read";
    private static final String MAIL_REPLY_CLASS = "mail_icon mail_reply";
    private static final String MAIL_ATTACHMENT_CLASS = "mail_icon mail_attachment";
    private static final String MAIL_REVOKE_CLASS = "mail_icon mail_revoke";
    private static final String MAIL_SEND_SUCCESS_CLASS = "mail_icon mail_send_success";
    private static final String MAIL_SEND_PROCESS_CLASS = "mail_icon mail_send_process";
    private static final String MAIL_DRAFT_CLASS = "mail_icon mail_draft";
    private static final String MAIL_IMPORTANT = "mail_icon mail_important";
    private static final String MAIL_TIMING_SEND_CLASS = "mail_icon mail_timing_send";

    static {
        ICON_TITLE_MAP.put(MAIL_READED_CLASS, "邮件已读");
        ICON_TITLE_MAP.put(MAIL_NOT_READ_CLASS, "邮件未读");
        ICON_TITLE_MAP.put(MAIL_REPLY_CLASS, "邮件已回复");
        ICON_TITLE_MAP.put(MAIL_ATTACHMENT_CLASS, "邮件附件");
        ICON_TITLE_MAP.put(MAIL_REVOKE_CLASS, "邮件已撤回");
        ICON_TITLE_MAP.put(MAIL_SEND_SUCCESS_CLASS, "邮件投递成功");
        ICON_TITLE_MAP.put(MAIL_SEND_PROCESS_CLASS, "邮件投递中");
        ICON_TITLE_MAP.put(MAIL_DRAFT_CLASS, "草稿");
        ICON_TITLE_MAP.put(MAIL_IMPORTANT, "重要邮件");
        ICON_TITLE_MAP.put(MAIL_TIMING_SEND_CLASS, "定时邮件");
    }

    @Resource
    private WmMailboxService wmMailboxService;
    @Resource
    private WmMailboxInfoService wmMailboxInfoService;

    @Override
    public String getType() {
        return "WebmailIconDataStoreRenderer";
    }

    @Override
    public String getName() {
        return "Webmail图标状态渲染";
    }

    @Override
    public Object doRenderData(String columnIndex, Object value, Map<String, Object> rowData, RendererParam param) {
        String uuid = (String) rowData.get("UUID");
        StringBuilder iconHtmlBuilder = new StringBuilder("");
        try {
            WmMailBoxInfoBean mail = wmMailboxInfoService.getByMailUuid(uuid);
//			WmMailbox mail = wmMailboxService.get(uuid);
            // 是否重要
            iconHtmlBuilder
                    .append(mail.getPriority() == 1 ? createIconHtml(MAIL_IMPORTANT)
                            : "");
            iconHtmlBuilder.append(renderMailPrimaryIconHtml(mail));

            // 2.是否有附件
            iconHtmlBuilder
                    .append(StringUtils.isNotEmpty(mail.getRepoFileUuids()) ? createIconHtml(MAIL_ATTACHMENT_CLASS)
                            : "");

        } catch (Exception ex) {
            logger.error("Webmail图标状态渲染,异常：", Throwables.getStackTraceAsString(ex));
            return value;
        }
        return iconHtmlBuilder.toString();
    }

    /**
     * 主要邮件图标渲染HTML
     */
    private String renderMailPrimaryIconHtml(WmMailBoxInfoBean mail) {

        if (WmWebmailConstants.STATUS_DRAFT.equals(mail.getStatus())) {
            if (mail.getSendStatus() != null
                    && mail.getSendStatus() == 0
                    && mail.getSendTime() != null) {
                return createIconHtml(MAIL_TIMING_SEND_CLASS);
            }
            // 草稿图标
            return createIconHtml(MAIL_DRAFT_CLASS);
        }

        // 1.是否已读未读
        if (WmWebmailConstants.FLAG_READ.equals(mail.getIsRead().toString())) {
            return createIconHtml(MAIL_READED_CLASS);
            // TODO:是否已回复
        } else {
            return createIconHtml(MAIL_NOT_READ_CLASS);
        }
    }

    private String createIconHtml(String iconClassName) {
        return String.format("<div class=\"%s\" title=\"%s\"></div>", iconClassName, ICON_TITLE_MAP.get(iconClassName));
    }

}
