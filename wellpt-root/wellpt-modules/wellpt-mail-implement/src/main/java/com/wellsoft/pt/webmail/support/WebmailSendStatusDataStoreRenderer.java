/*
 * @(#)2016年11月1日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.support;

import com.google.common.base.Throwables;
import com.wellsoft.pt.basicdata.datastore.support.RendererParam;
import com.wellsoft.pt.basicdata.datastore.support.renderer.AbstractDataStoreRenderer;
import com.wellsoft.pt.webmail.facade.service.WmWebmailOutboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Description: 邮件发送状态渲染器
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年7月18日.1	zhongzh		2017年7月18日		Create
 * </pre>
 * @date 2017年7月18日
 */
@Component
public class WebmailSendStatusDataStoreRenderer extends AbstractDataStoreRenderer {

    private static final String CLASS_SENDED = "mail_icon mail_send_process";
    private static final String CLASS_RECEIVED = "mail_icon mail_send_success";
    private static final String CLASS_REVOKE = "mail_icon mail_send_revoke";
    private static final String CLASS_ADDRESS = "mail_icon mail_send_address";
    private static final String CLASS_FAIL = "mail_icon mail_send_fail";
    @Autowired
    private WmWebmailOutboxService wmWebmailOutboxService;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.DataStoreRenderer#getType()
     */
    @Override
    public String getType() {
        return "webmailSendStatusDataStoreRenderer";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.DataStoreRenderer#getName()
     */
    @Override
    public String getName() {
        return "Webmail投递状态渲染";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.AbstractDataStoreRenderer#doRenderData(java.lang.String, java.lang.Object, java.util.Map, com.wellsoft.pt.basicdata.datastore.support.RendererParam)
     */
    @Override
    public Object doRenderData(String columnIndex, Object value, Map<String, Object> rowData,
                               RendererParam param) {
        String webMailUuid = (String) rowData.get("UUID");
        Map<String, Object> sendStatus;
        try {
            sendStatus = wmWebmailOutboxService.querySendStatus(webMailUuid, true);
        } catch (Exception ex) {
            logger.error("Webmail投递状态渲染,异常：", Throwables.getStackTraceAsString(ex));
            return value;
        }
        //撤回
        if (sendStatus != null && sendStatus.get("revokeStatus") != null && Integer.valueOf(sendStatus.get("revokeStatus").toString()) != 0) {
            return "<span class=\"" + CLASS_REVOKE + "\"  title=\"已撤回\"/>" + value;
        }
        //发送状态（0：待发送，1：已发送，2：发送失败）
        if (sendStatus != null && sendStatus.get("sendStatus") != null && Integer.valueOf(sendStatus.get("sendStatus").toString()) == 0) {
            return "<span class=\"" + CLASS_ADDRESS + "\"  title=\"待解析收件地址\"/>" + value;
        }
        if (sendStatus != null && sendStatus.get("sendStatus") != null && Integer.valueOf(sendStatus.get("sendStatus").toString()) == 1) {
            return "<span class=\"" + CLASS_RECEIVED + "\" style=\"color:#0fbb0f\" title=\"投递成功\"/>" + value;
        }
        return "<span class=\"" + CLASS_FAIL + "\"  title=\"投递失败\"/>" + value;
    }
}
