package com.wellsoft.pt.webmail.facade.api.service;

import java.util.List;

/**
 * 邮件客户端Api
 */
public interface MailClientApiFacade {


    /**
     * 添加用户邮箱账号，返回邮箱地址
     *
     * @param userId
     * @param password
     * @return
     */
    public String addMailUser(String userId, String password);

    /**
     * 更改用户邮箱密码
     *
     * @param userId
     * @param password
     * @return
     */
    public void alterMailUserPassword(String userId, String password);

    /**
     * 如何描述该方法
     *
     * @return
     */
    public Long getUnreadCount(String userId);

    /**
     * 根据用户ID，删除对应的邮箱用户账号
     *
     * @param userId
     */
    public void deleteMailUser(String userId);

    /**
     * 通过系统账号发送邮件
     *
     * @param addresses 接收人邮箱地址/部门、职位、用户、群组id
     * @param subject   主题
     * @param body      内容
     */
    @SuppressWarnings("unchecked")
    public void sendBySystem(String[] addresses, String subject, String body);

    /**
     * 通过系统账号发送邮件
     *
     * @param addresses 接收人邮箱地址/部门、职位、用户、群组id
     * @param subject   主题
     * @param body      内容
     * @param fileIds   MONGO附件ID
     */
    public void sendBySystem(String[] addresses, String subject, String body, List<String> fileIds);

}
