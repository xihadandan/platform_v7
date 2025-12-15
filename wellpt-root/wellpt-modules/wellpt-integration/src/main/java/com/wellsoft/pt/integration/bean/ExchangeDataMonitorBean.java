package com.wellsoft.pt.integration.bean;

import com.wellsoft.pt.integration.entity.ExchangeDataMonitor;

/**
 * Description: 如何描述该类
 *
 * @author wangbx
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-6.1	wangbx		2013-12-6		Create
 * </pre>
 * @date 2013-12-6
 */
public class ExchangeDataMonitorBean extends ExchangeDataMonitor {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -2215611517716252028L;

    private String unitName;

    private String fromUnitName;

    private String reservedText1;

    private String reservedText2;

    private String fromId;

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getFromUnitName() {
        return fromUnitName;
    }

    public void setFromUnitName(String fromUnitName) {
        this.fromUnitName = fromUnitName;
    }

    public String getReservedText1() {
        return reservedText1;
    }

    public void setReservedText1(String reservedText1) {
        this.reservedText1 = reservedText1;
    }

    public String getReservedText2() {
        return reservedText2;
    }

    public void setReservedText2(String reservedText2) {
        this.reservedText2 = reservedText2;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

}
