package com.wellsoft.pt.webmail.job;

import com.sun.mail.imap.IMAPFolder;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.mail.service.James3MailService;
import com.wellsoft.pt.webmail.entity.WmMailUserEntity;
import com.wellsoft.pt.webmail.facade.service.WmWebmailService;
import com.wellsoft.pt.webmail.service.WmMailUserService;
import com.wellsoft.pt.webmail.service.WmMailboxInfoUserService;
import com.wellsoft.pt.webmail.support.WmMailUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Store;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Description: 默认同步邮件调度实现
 *
 * @author chenq
 * @date 2020/1/15
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2020/1/15    chenq		2020/1/15		Create
 * </pre>
 */
public class DefaultWebmailClientSyncInboxMailJob {
    private static Logger logger = LoggerFactory.getLogger(DefaultWebmailClientSyncInboxMailJob.class);

    private static ConcurrentLinkedQueue<String> lockedList = new ConcurrentLinkedQueue<>();

    public static void startSyncInboxMail(String userId) {
        if (StringUtils.isBlank(userId)) {
            return;
        }
        //已存在跳过
        if (lockedList.contains(userId)) {
            return;
        }
        try {
            lockedList.add(userId);
            syncInboxMail(userId);
        } finally {
            lockedList.remove(userId);
        }
    }

    public static void syncInboxMail(String userId) {
        WmMailUserService wmMailUserService = ApplicationContextHolder.getBean(WmMailUserService.class);
        WmMailUserEntity user = wmMailUserService.getInnerMailUser(userId);
        if (user == null) {
            logger.warn("用户ID={}的邮件账号不存在，忽略同步收件箱的邮件");
            return;
        }
        James3MailService jamesMailService = ApplicationContextHolder.getBean(James3MailService.class);
        int lastUid = jamesMailService.getMailboxLastUid(user.getMailAddress());
        if (lastUid <= user.getSyncMessageNumber()) {
            logger.warn("用户ID={}的邮件账号lastUid={},暂未收到新邮件", userId, lastUid);
            return;
        }
        WmWebmailService wmWebmailService = ApplicationContextHolder.getBean(WmWebmailService.class);
        WmMailboxInfoUserService wmMailboxInfoUserService = ApplicationContextHolder.getBean(WmMailboxInfoUserService.class);
        Store store = null;
        IMAPFolder folder = null;
        Integer mid = user.getSyncMessageNumber();
        try {
            store = WmMailUtils.getImapStore(user);
            folder = WmMailUtils.getImapFolder(store, Folder.READ_WRITE);
            for (int i = mid + 1; i <= folder.getMessageCount(); i++) {
                mid = i;
                if (wmMailboxInfoUserService.isExistMail(mid, user.getUserId(), user.getMailAddress())) {
                    continue;
                }
                Message message = folder.getMessageByUID(mid);
                if (message != null) {
                    wmWebmailService.saveMailMessage(mid, message, user);
                }
            }
        } catch (Exception e) {
            logger.error("同步用户ID={}的mid={}收件异常：", new Object[]{user.getUserId(), mid, e});
            throw new RuntimeException(e);
        } finally {
            WmMailUtils.close(store, folder, true);
        }
    }

}
