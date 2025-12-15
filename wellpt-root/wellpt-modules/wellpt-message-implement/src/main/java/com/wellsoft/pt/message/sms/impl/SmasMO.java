package com.wellsoft.pt.message.sms.impl;

import com.wellsoft.pt.message.sms.MoType;
import com.wellsoft.pt.message.sms.skmas.Sms;

public class SmasMO implements MoType {
    private Sms sms;

    public SmasMO(Sms sms) {
        this.sms = sms;
    }

    @Override
    public String getSmId() {
        return sms.getSmsId();
    }

    @Override
    public String getMobile() {
        return sms.getPhoneNumber();
    }

    @Override
    public String getMoContent() {
        return sms.getContent();
    }

    @Override
    public String getMoTime() {
        return sms.getCreateTime();
    }

    @Override
    public Object getMo() {
        return sms;
    }

}
