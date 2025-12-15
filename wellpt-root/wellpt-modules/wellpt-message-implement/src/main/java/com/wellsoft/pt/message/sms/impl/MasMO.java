package com.wellsoft.pt.message.sms.impl;

import com.jasson.im.api.MOItem;
import com.wellsoft.pt.message.sms.MoType;


public class MasMO implements MoType {
    private MOItem moItem;

    public MasMO(MOItem sms) {
        this.moItem = sms;
    }

    @Override
    public String getSmId() {
        return String.valueOf(moItem.getSmID());
    }

    @Override
    public String getMobile() {
        return moItem.getMobile();
    }

    @Override
    public String getMoContent() {
        return moItem.getContent();
    }

    @Override
    public String getMoTime() {
        return moItem.getMoTime();
    }

    @Override
    public Object getMo() {
        return moItem;
    }
}