package com.wellsoft.pt.message.sms.impl;

import com.jasson.im.api.RPTItem;
import com.wellsoft.pt.message.sms.RptType;

public class MasRpt implements RptType {
    public static final int IM_RTP_FAIL = -1;
    public static final int IM_RTP_SUCC = 0;
    private RPTItem rptItem;

    public MasRpt(RPTItem rpt) {
        this.rptItem = rpt;
    }

    @Override
    public String getSmId() {
        return String.valueOf(rptItem.getSmID());
    }

    @Override
    public String getMobile() {
        return rptItem.getMobile();
    }

    @Override
    public String getCode() {
        return String.valueOf(rptItem.getCode());
    }

    @Override
    public String getDesc() {
        return rptItem.getDesc();
    }

    @Override
    public String getMoTime() {
        return rptItem.getRptTime();
    }

    @Override
    public boolean isRtpSUCC() {
        return rptItem.getCode() == IM_RTP_SUCC;
    }

    @Override
    public boolean isRtpFAIL() {
        return rptItem.getCode() == 8 || rptItem.getCode() == 101;
    }

    @Override
    public Object getRpt() {
        return rptItem;
    }
}

