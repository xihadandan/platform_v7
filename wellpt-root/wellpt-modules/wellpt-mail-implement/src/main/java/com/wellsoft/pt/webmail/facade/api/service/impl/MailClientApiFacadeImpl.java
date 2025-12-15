/*
 * @(#)2016年6月7日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.facade.api.service.impl;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.webmail.bean.WmWebmailBean;
import com.wellsoft.pt.webmail.facade.api.service.MailClientApiFacade;
import com.wellsoft.pt.webmail.facade.service.WmWebmailService;
import com.wellsoft.pt.webmail.service.WmMailUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Description: 邮件客户端Api
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年6月7日.1	zhulh		2016年6月7日		Create
 * </pre>
 * @date 2016年6月7日
 */
@Service(value = "mailClientApiFacade")
public class MailClientApiFacadeImpl extends AbstractApiFacade implements MailClientApiFacade {

    @Autowired
    private OrgApiFacade orgApiFacade;

    @Autowired
    private WmMailUserService wmMailUserService;

    @Autowired
    private WmWebmailService wmWebmailService;

    /**
     * 添加用户邮箱账号，返回邮箱地址
     *
     * @param userId
     * @param password
     * @return
     */
    public String addMailUser(String userId, String password) {
        return wmMailUserService.addMailUser(userId, password);
    }

    /**
     * 更改用户邮箱密码
     *
     * @param userId
     * @param password
     * @return
     */
    public void alterMailUserPassword(String userId, String password) {
        wmMailUserService.alterMailUserPassword(userId, password);
    }

    /**
     * 如何描述该方法
     *
     * @return
     */
    public Long getUnreadCount(String userId) {
        return wmWebmailService.getUnreadCount(userId);
    }

    /**
     * 根据用户ID，删除对应的邮箱用户账号
     *
     * @param userId
     */
    public void deleteMailUser(String userId) {
        wmMailUserService.deleteMailUser(userId);
    }

    /**
     * 通过系统账号发送邮件
     *
     * @param addresses 接收人邮箱地址/部门、职位、用户、群组id
     * @param subject   主题
     * @param body      内容
     */
    @SuppressWarnings("unchecked")
    public void sendBySystem(String[] addresses, String subject, String body) {
        this.sendBySystem(addresses, subject, body, Collections.EMPTY_LIST);
    }

    /**
     * 通过系统账号发送邮件
     *
     * @param addresses 接收人邮箱地址/部门、职位、用户、群组id
     * @param subject   主题
     * @param body      内容
     * @param fileIds   MONGO附件ID
     */
    public void sendBySystem(String[] addresses, String subject, String body,
                             List<String> fileIds) {
        // 获取系统管理员ID
        List<String> adminIds = orgApiFacade.queryAllAdminIdsByUnitId(
                SpringSecurityUtils.getCurrentUserUnitId());
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("addresses", addresses);
        map.put("subject", subject);
        map.put("body", body);
        map.put("fileIds", fileIds);
        if (adminIds.isEmpty()) {
            logger.error("系统管理员账号为空，无法发送邮件: " + JsonUtils.object2Json(map));
            return;
        }

        if (addresses == null || addresses.length == 0) {
            return;
        }
        if (StringUtils.isBlank(body) || StringUtils.isBlank(subject)) {
            logger.error("系统账号发送邮件失败，邮件内容={},主题={}", body, subject);
            return;
        }

        String adminId = adminIds.get(0);
        String tenantId = SpringSecurityUtils.getCurrentTenantId();
        try {
            IgnoreLoginUtils.login(tenantId, adminId);

            WmWebmailBean webmailBean = new WmWebmailBean();
            //获取系统管理员账号的内部邮件地址
            webmailBean.setFromMailAddress(
                    wmMailUserService.getInnerMailUser(adminId).getMailAddress());
            webmailBean.setToMailAddress(
                    StringUtils.join(addresses, Separator.SEMICOLON.getValue()));
            List<String> userNames = new ArrayList<>();
            for (String addres : addresses) {
                userNames.add(wmMailUserService.getInnerMailUser(addres).getUserName());
            }
            webmailBean.setToUserName(StringUtils.join(userNames, Separator.SEMICOLON.getValue()));
            webmailBean.setCcUserName("");
            webmailBean.setSubject(subject);
            webmailBean.setContent(body);
            webmailBean.setRepoFileUuids(StringUtils.join(fileIds, Separator.SEMICOLON.getValue()));
            wmWebmailService.send(webmailBean);
        } catch (Exception e) {
            logger.error("邮件发送失败: " + JsonUtils.object2Json(map), e);
        } finally {
            IgnoreLoginUtils.logout();
        }
    }

}
