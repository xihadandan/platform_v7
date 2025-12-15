package com.wellsoft.pt.message.sms.impl;

import com.wellsoft.pt.message.sms.MoType;
import com.wellsoft.pt.message.sms.RptType;
import com.wellsoft.pt.message.sms.SmsClientApi;

public class NoOpeSmsClientApiImpl extends SmsClientApi {
    public NoOpeSmsClientApiImpl() {
    }

    public NoOpeSmsClientApiImpl(String url, String apiCode, String username, String password,
                                 String dbname) {
        super(url, apiCode, username, password, dbname, "");
    }

    @Override
    public int sendSM(String[] mobiles, String content, long smID, long srcID, String url,
                      String sendTime) {
        logger.info("No Operate for sendSM");
        // 没有开启短信机发送失败
        return IM_SEND_FAIL;
    }

    @Override
    public MoType[] receiveSM(long srcID, int amount) {
        logger.info("No Operate for receiveSM");
        return null;
    }

    @Override
    public RptType[] receiveRPT(long smID, int amount) {
        logger.info("No Operate for receiveRPT");
        return null;
    }

    @Override
    public int getType() {
        return MSG_TYPE_NONE;
    }
}
