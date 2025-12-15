/*
 * @(#)7/27/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.support;

import com.wellsoft.context.base.BaseObject;

import java.util.List;

/**
 * Description: 流水号检测结果
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 7/27/22.1	zhulh		7/27/22		Create
 * </pre>
 * @date 7/27/22
 */
public class SerialNumberCheckResult extends BaseObject {
    // 流水号维护UUID
    private String maintainUuid;

    // 跳过的指针(包含可补的流水号指针)
    private List<Long> skipPointers;

    // 可补的流水号
    private List<String> fillSerialNos;

    /**
     * @return the maintainUuid
     */
    public String getMaintainUuid() {
        return maintainUuid;
    }

    /**
     * @param maintainUuid 要设置的maintainUuid
     */
    public void setMaintainUuid(String maintainUuid) {
        this.maintainUuid = maintainUuid;
    }

    /**
     * @return the skipPointers
     */
    public List<Long> getSkipPointers() {
        return skipPointers;
    }

    /**
     * @param skipPointers 要设置的skipPointers
     */
    public void setSkipPointers(List<Long> skipPointers) {
        this.skipPointers = skipPointers;
    }

    /**
     * @return the fillSerialNos
     */
    public List<String> getFillSerialNos() {
        return fillSerialNos;
    }

    /**
     * @param fillSerialNos 要设置的fillSerialNos
     */
    public void setFillSerialNos(List<String> fillSerialNos) {
        this.fillSerialNos = fillSerialNos;
    }
}
