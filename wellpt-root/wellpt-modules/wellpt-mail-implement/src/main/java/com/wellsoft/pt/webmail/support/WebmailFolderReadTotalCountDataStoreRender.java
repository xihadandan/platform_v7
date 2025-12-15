package com.wellsoft.pt.webmail.support;

import com.wellsoft.pt.basicdata.datastore.support.RendererParam;
import com.wellsoft.pt.basicdata.datastore.support.renderer.AbstractDataStoreRenderer;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.webmail.entity.WmMailFolderEntity;
import com.wellsoft.pt.webmail.service.WmMailboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Description: 文件夹读取总计数数据存储渲染
 *
 * @author chenq
 * @date 2019/7/19
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/7/19    chenq		2019/7/19		Create
 * </pre>
 */
@Component
public class WebmailFolderReadTotalCountDataStoreRender extends AbstractDataStoreRenderer {


    @Autowired
    WmMailboxService wmMailboxService;

    @Override
    public String getType() {
        return "WebmailFolderUnreadAndTotalMail";
    }

    @Override
    public String getName() {
        return "平台邮件_邮件文件夹的邮件未读与总邮件数";
    }

    @Override
    public Object doRenderData(String columnIndex, Object value, Map<String, Object> rowData,
                               RendererParam param) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        String code = rowData.get("code").toString();
        Long unread = 0L;
        Long total = 0L;
        Integer status = 0;
        String mailbox = code;
        if (WmWebmailConstants.OUTBOX.equals(code)) {
            status = WmWebmailConstants.STATUS_SEND_SUCCESS;
        } else if (WmWebmailConstants.DRAFT.equals(code)) {
            status = WmWebmailConstants.STATUS_DRAFT;
            mailbox = WmWebmailConstants.OUTBOX;
        } else if (WmWebmailConstants.RECYCLE.equals(code)) {
            status = WmWebmailConstants.STATUS_DELETE;
            mailbox = null;
        } else if (WmWebmailConstants.INBOX.equals(code)) {
            status = WmWebmailConstants.STATUS_RECEIVE_SUCCESS;
        } else if (code.startsWith(WmMailFolderEntity.FOLDER_CODE_PREFIX)) {
            status = -3;//表示非删除状态
        }
        total = wmMailboxService.countMailSum(userId, mailbox, null, status);
        unread = wmMailboxService.countMailSum(userId, mailbox, WmWebmailConstants.FLAG_UNREAD,
                status);
        return unread + " / " + total;
    }
}
