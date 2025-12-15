/*
 * @(#)2013-1-28 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.facade;

import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.message.sms.MoType;
import com.wellsoft.pt.message.sms.RptType;
import com.wellsoft.pt.message.sms.SmsClientApiFactory;
import org.springframework.stereotype.Component;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-2-5.1	zhongzh		2015-2-5		Create
 * </pre>
 * @date 2015-2-5
 */
@Component
public class SmsClientApiFacade extends AbstractApiFacade {

    public int sendSM(String mobile, String content, long smID) {
        return sendSM(new String[]{mobile}, content, smID, smID);
    }

    public int sendSM(String[] mobiles, String content, long smID) {
        return sendSM(mobiles, content, smID, smID);
    }

    public int sendSM(String[] mobiles, String content, long smID, long srcID) {
        return sendSM(mobiles, content, smID, srcID, "");
    }

    public int sendSM(String[] mobiles, String content, String sendTime, long smID, long srcID) {
        return sendSM(mobiles, content, smID, srcID, "", sendTime);
    }

    public int sendSM(String mobile, String content, long smID, String url) {
        return sendSM(new String[]{mobile}, content, smID, url);
    }

    public int sendSM(String[] mobiles, String content, long smID, String url) {
        return sendSM(mobiles, content, smID, smID, url);
    }

    public int sendSM(String[] mobiles, String content, long smID, long srcID, String url) {
        return sendSM(mobiles, content, smID, srcID, url, null);
    }

    /**
     * 发送短信
     *
     * @param mobiles  手机号
     * @param content  发送内容
     * @param smID     短信ID
     * @param srcID
     * @param url      短信连接地址(内容)
     * @param sendTime 发送时间
     * @return 是否发送成功，是否需要重发：SmsClientApiFacade.NEED_RESEND
     */
    public int sendSM(String[] mobiles, String content, long smID, long srcID, String url, String sendTime) {
        return SmsClientApiFactory.createSmsClientApi().sendSM(mobiles, content, smID, srcID, url, sendTime);
    }

    /**
     * 默认接收100短信
     *
     * @return 短信内容
     */
    public MoType[] receiveSM() {
        return receiveSM(1L, 100);
    }

    /**
     * 接收短信
     *
     * @param srcID
     * @param amount 接收短信条数
     * @return 短信内容
     */
    public MoType[] receiveSM(long srcID, int amount) {
        return SmsClientApiFactory.createSmsClientApi().receiveSM(srcID, amount);
    }

    /**
     * 默认接收100回执
     *
     * @return 回执内容
     */
    public RptType[] receiveRPT() {
        return receiveRPT(1L, 100);
    }

    /**
     * 接收回执
     *
     * @param smID
     * @param amount 接收回执条数
     * @return 回执内容
     */
    public RptType[] receiveRPT(long smID, int amount) {
        return SmsClientApiFactory.createSmsClientApi().receiveRPT(smID, amount);
    }

    /**
     * 获取短信机消息类型(0 modem; 1 mas机; 2 smas机; 3 云mas)
     */
    public int getType() {
        // TODO 存在不准确定，eg:配置变化了(一般不存在这种情况)
        return SmsClientApiFactory.createSmsClientApi().getType();
    }
}
