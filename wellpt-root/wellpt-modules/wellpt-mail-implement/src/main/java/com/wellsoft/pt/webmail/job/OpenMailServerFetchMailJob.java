package com.wellsoft.pt.webmail.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: 第三方开放邮箱的拉取邮件
 *
 * @author chenq
 * @date 2018/6/7
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/6/7    chenq		2018/6/7		Create
 * </pre>
 */
public class OpenMailServerFetchMailJob {
    private static Logger logger = LoggerFactory.getLogger(OpenMailServerFetchMailJob.class);

    public static void syncInBoxMail(String currentUserId) {
        logger.info("开始执行同步第三方开发邮箱的收件箱JOB");
//        WmMailUserService wmMailUserService = ApplicationContextHolder.getBean(
//                WmMailUserService.class);
//        List<WmMailUserEntity> wmMailUserEntityList = wmMailUserService.listOuterMailUser(
//                currentUserId);
//        MailFetchHanlder mailFetchHanlder = ApplicationContextHolder.getBean(
//                MailFetchHanlder.class);
//        for (WmMailUserEntity user : wmMailUserEntityList) {
//            logger.info("开始同步{}的第三方收件箱", user.getMailAddress());
//            try {
//                IgnoreLoginUtils.login(Config.DEFAULT_TENANT, user.getUserId());
//                mailFetchHanlder.fetchMailsUsingPop3Protocol(user, user.getSyncMessageNumber() + 1,
//                        null);
//            } catch (Exception e) {
//                logger.error("同步{}的第三方收件箱异常：", user.getMailAddress(), e);
//            } finally {
//                IgnoreLoginUtils.logout();
//            }
//
//        }
    }
}
