package com.wellsoft.pt.webmail.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.webmail.dao.WmMailboxInfoUserDao;
import com.wellsoft.pt.webmail.entity.WmMailboxInfoUser;

import java.util.List;

/**
 * @Auther: yt
 * @Date: 2022/2/12 16:32
 * @Description:
 */
public interface WmMailboxInfoUserService extends JpaService<WmMailboxInfoUser, WmMailboxInfoUserDao, String> {

    /**
     * 处理 接收失败（空间不足的）
     *
     * @param userId
     * @return
     */
    int receiveFailMail(String userId);

    /**
     * 获取用户的未阅数量
     *
     * @param userId
     * @return
     */
    Long getUnreadCount(String userId);

    /**
     * 查询邮件是否存在
     *
     * @param mid
     * @param userId
     * @param mailAddress
     * @return
     */
    boolean isExistMail(int mid, String userId, String mailAddress);


    /**
     * 查询邮件是否存在
     *
     * @param pid
     * @param userId
     * @param mailAddress
     * @return
     */
    boolean isExistMailPid(String pid, String userId, String mailAddress);

    /**
     * 查询收件人列表
     *
     * @param mailInfoUuid
     * @return
     */
    List<WmMailboxInfoUser> getInboxList(String mailInfoUuid);


    /**
     * 发送邮件
     *
     * @param sendMailUuid
     */
    void asyncSendMail(String sendMailUuid);

    void syncSendMail(String sendMailUuid);

    /**
     * 计算使用的容量
     *
     * @param userId
     * @param mailbox
     * @return
     */
    long capacityUsed(String userId, String mailbox);

    /**
     * 重新发送
     *
     * @param mailboxUuid
     */
    void resend(String mailboxUuid);
}
