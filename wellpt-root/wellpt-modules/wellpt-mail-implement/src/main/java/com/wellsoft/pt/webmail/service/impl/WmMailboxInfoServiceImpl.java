package com.wellsoft.pt.webmail.service.impl;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.webmail.bean.WmMailBoxInfoBean;
import com.wellsoft.pt.webmail.dao.WmMailboxInfoDao;
import com.wellsoft.pt.webmail.entity.WmMailboxInfo;
import com.wellsoft.pt.webmail.entity.WmMailboxInfoUser;
import com.wellsoft.pt.webmail.service.WmMailboxInfoService;
import com.wellsoft.pt.webmail.service.WmMailboxInfoUserService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Auther: yt
 * @Date: 2022/2/12 16:32
 * @Description:
 */
@Service
public class WmMailboxInfoServiceImpl extends AbstractJpaServiceImpl<WmMailboxInfo, WmMailboxInfoDao, String> implements WmMailboxInfoService {

    public List<WmMailBoxInfoBean> queryAll(String sql, Map<String, Object> params) {
        return queryByPage(sql, params, null);
    }

    public List<WmMailBoxInfoBean> queryByPage(String sql, Map<String, Object> params, PagingInfo pagingInfo) {
        List<WmMailBoxInfoBean> result = new ArrayList<>();
        List<QueryItem> list = this.dao.listQueryItemBySQL(sql, params, pagingInfo);
        if (CollectionUtils.isNotEmpty(list)) {
            for (QueryItem item : list) {
                result.add(convertQueryItem2WmMailBoxInfoBean(item));
            }
        }
        return result;
    }

    public WmMailBoxInfoBean getByMailUuid(String uuid) {
        WmMailboxInfoUser mailboxInfoUser = ApplicationContextHolder.getBean(WmMailboxInfoUserService.class).getOne(uuid);
        if (mailboxInfoUser == null) {
            return null;
        }
        WmMailboxInfo mailboxInfo = this.getOne(mailboxInfoUser.getMailInfoUuid());
        WmMailBoxInfoBean infoBean = new WmMailBoxInfoBean();
        BeanUtils.copyProperties(mailboxInfo, infoBean);
        BeanUtils.copyProperties(mailboxInfoUser, infoBean);
        infoBean.setMailInfoUuid(uuid);
        return infoBean;
    }

    private WmMailBoxInfoBean convertQueryItem2WmMailBoxInfoBean(QueryItem item) {
        WmMailBoxInfoBean wmMailbox = new WmMailBoxInfoBean();
        // WmMailBoxInfoUser
        wmMailbox.setUuid(item.getString(QueryItem.getKey("uuid")));
        wmMailbox.setUserId(item.getString(QueryItem.getKey("user_id")));
        wmMailbox.setUserName(item.getString(QueryItem.getKey("user_name")));
        wmMailbox.setUserReadReceiptStatus(QueryItem.getKey("read_receipt_status") == null ? null : item.getInt(QueryItem.getKey("read_receipt_status")));
        wmMailbox.setSendStatus(QueryItem.getKey("send_status") == null ? null : item.getInt(QueryItem.getKey("send_status")));
        wmMailbox.setMailboxName(item.getString(QueryItem.getKey("mailbox_name")));
        wmMailbox.setIsRead(QueryItem.getKey("is_read") == null ? null : item.getInt(QueryItem.getKey("is_read")));
        wmMailbox.setStatus(QueryItem.getKey("status") == null ? null : item.getInt(QueryItem.getKey("status")));
        wmMailbox.setRevokeStatus(QueryItem.getKey("revoke_status") == null ? null : item.getInt(QueryItem.getKey("revoke_status")));
        wmMailbox.setSystemUnitId(item.getString(QueryItem.getKey("system_unit_id")));
        wmMailbox.setMailInfoUuid(item.getString(QueryItem.getKey("mail_info_uuid")));
        wmMailbox.setSendCount(QueryItem.getKey("send_count") == null ? null : item.getInt(QueryItem.getKey("send_count")));
        wmMailbox.setFailMsg(item.getString(QueryItem.getKey("fail_msg")));
        wmMailbox.setNextSendTime(item.getDate(QueryItem.getKey("next_send_time")));

        // WmMailBoxInfo
        wmMailbox.setFromUserName(item.getString(QueryItem.getKey("from_user_name")));
        wmMailbox.setFromMailAddress(item.getString(QueryItem.getKey("from_mail_address")));
        wmMailbox.setToUserName(item.getString(QueryItem.getKey("to_user_name")));
        wmMailbox.setToMailAddress(item.getString(QueryItem.getKey("to_mail_address")));
        wmMailbox.setCcUserName(item.getString(QueryItem.getKey("cc_user_name")));
        wmMailbox.setCcMailAddress(item.getString(QueryItem.getKey("cc_mail_address")));
        wmMailbox.setBccUserName(item.getString(QueryItem.getKey("bcc_user_name")));
        wmMailbox.setBccMailAddress(item.getString(QueryItem.getKey("bcc_mail_address")));
        wmMailbox.setSubject(item.getString(QueryItem.getKey("subject")));
        wmMailbox.setContent(item.getString(QueryItem.getKey("content")));
        wmMailbox.setRepoFileNames(item.getString(QueryItem.getKey("repo_file_names")));
        wmMailbox.setRepoFileUuids(item.getString(QueryItem.getKey("repo_file_uuids")));
        wmMailbox.setMailSize(item.getLong(QueryItem.getKey("mail_size")));
        wmMailbox.setReadReceiptStatus(QueryItem.getKey("mailReadReceiptStatus") == null ? null : item.getInt(QueryItem.getKey("mailReadReceiptStatus")));
        wmMailbox.setPriority(QueryItem.getKey("priority") == null ? null : item.getInt(QueryItem.getKey("priority")));
        wmMailbox.setSendTime(item.getDate(QueryItem.getKey("send_time")));
        wmMailbox.setActualToMailAddress(item.getString(QueryItem.getKey("actual_to_mail_address")));
        wmMailbox.setActualCcMailAddress(item.getString(QueryItem.getKey("actual_cc_mail_address")));
        wmMailbox.setActualBccMailAddress(item.getString(QueryItem.getKey("actual_bcc_mail_address")));

        return wmMailbox;
    }

}
