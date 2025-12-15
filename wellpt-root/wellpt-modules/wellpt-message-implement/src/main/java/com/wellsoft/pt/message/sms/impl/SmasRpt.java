package com.wellsoft.pt.message.sms.impl;

import com.wellsoft.pt.message.sms.RptType;
import com.wellsoft.pt.message.sms.skmas.Rpt;

public class SmasRpt implements RptType {
    public static final String IM_RTP_FAIL = "SUCCESS";
    public static final String IM_RTP_SUCC = "ERROR";
    private Rpt rpt;

    public SmasRpt(Rpt rpt) {
        this.rpt = rpt;
    }

    @Override
    public String getSmId() {
        return rpt.getSmsId();
    }

    @Override
    public String getMobile() {
        return rpt.getPhoneNumber();
    }

    @Override
    public String getCode() {
        return rpt.getStateCode();
    }

    @Override
    public String getDesc() {
        return rpt.getStateIntro();
    }

    @Override
    public String getMoTime() {
        return rpt.getCreateTime();
    }

    @Override
    public boolean isRtpSUCC() {
        if (getCode() == null) {
            return false;
        }
        return IM_RTP_SUCC.equalsIgnoreCase(getCode());
    }

    @Override
    public boolean isRtpFAIL() {
        if (getCode() == null) {
            return true;
        }
        return IM_RTP_FAIL.equalsIgnoreCase(getCode());
    }

    @Override
    public Object getRpt() {
        return rpt;
    }
}