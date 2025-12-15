/*
 * @(#)2012-11-16 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.element;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-16.1	zhulh		2012-11-16		Create
 * </pre>
 * @date 2012-11-16
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MonitorElement implements Serializable {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -413822951513436774L;

    private UnitElement unit;

    public UnitElement getUnit() {
        return unit;
    }

    public void setUnit(UnitElement unit) {
        this.unit = unit;
    }
}
