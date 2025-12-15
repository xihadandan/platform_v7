/*
 * @(#)2013-4-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.bean;

import com.wellsoft.pt.org.entity.DutyAgent;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-15.1	zhulh		2013-4-15		Create
 * </pre>
 * @date 2013-4-15
 */
public class DutyAgentBean extends DutyAgent {

    private static final long serialVersionUID = -6945392356450147855L;

    // 开始时间
    @NotBlank
    private String formatedFromTime;

    // 结束时间
    @NotBlank
    private String formatedToTime;

    /**
     * @return the formatedFromTime
     */
    public String getFormatedFromTime() {
        return formatedFromTime;
    }

    /**
     * @param formatedFromTime 要设置的formatedFromTime
     */
    public void setFormatedFromTime(String formatedFromTime) {
        this.formatedFromTime = formatedFromTime;
    }

    /**
     * @return the formatedToTime
     */
    public String getFormatedToTime() {
        return formatedToTime;
    }

    /**
     * @param formatedToTime 要设置的formatedToTime
     */
    public void setFormatedToTime(String formatedToTime) {
        this.formatedToTime = formatedToTime;
    }

}
