/*
 * @(#)2018年6月5日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.calendar.bean;

import java.io.Serializable;

/**
 * Description: 如何描述该类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年6月5日.1	zyguo		2018年6月5日		Create
 * </pre>
 * @date 2018年6月5日
 */
public class CalendarRights implements Serializable {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -528602151104327691L;
    private Boolean isAdd = false;
    private Boolean isEdit = false;
    private Boolean isDel = false;
    private Boolean isStatus = false;

    /**
     * @return the isAdd
     */
    public Boolean getIsAdd() {
        return isAdd;
    }

    /**
     * @param isAdd 要设置的isAdd
     */
    public void setIsAdd(Boolean isAdd) {
        this.isAdd = isAdd;
    }

    /**
     * @return the isEdit
     */
    public Boolean getIsEdit() {
        return isEdit;
    }

    /**
     * @param isEdit 要设置的isEdit
     */
    public void setIsEdit(Boolean isEdit) {
        this.isEdit = isEdit;
    }

    /**
     * @return the isDel
     */
    public Boolean getIsDel() {
        return isDel;
    }

    /**
     * @param isDel 要设置的isDel
     */
    public void setIsDel(Boolean isDel) {
        this.isDel = isDel;
    }

    /**
     * @return the isStatus
     */
    public Boolean getIsStatus() {
        return isStatus;
    }

    /**
     * @param isStatus 要设置的isStatus
     */
    public void setIsStatus(Boolean isStatus) {
        this.isStatus = isStatus;
    }

}
