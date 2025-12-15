/*
 * @(#)7/21/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.support;


import com.wellsoft.context.base.BaseObject;
import org.apache.commons.lang.StringUtils;

import java.util.Objects;

/**
 * Description: 流水号信息
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 7/21/22.1	zhulh		7/21/22		Create
 * </pre>
 * @date 7/21/22
 */
public class SerialNumberInfo extends BaseObject {
    // 前缀
    private String prefix;
    // 指针
    private Long pointer;
    // 指针格式化值
    private String pointerFormatValue;
    // 后缀
    private String suffix;
    // 流水号维护UUID
    private String maintainUuid;
    // 是否补号
    private boolean isFill;
    // 流水号记录UUID
    private String recordUuid;

    /**
     * @param prefix
     * @param pointer
     * @param pointerFormatValue
     * @param suffix
     * @param maintainUuid
     */
    public SerialNumberInfo(String prefix, Long pointer, String pointerFormatValue, String suffix, String maintainUuid) {
        this.prefix = prefix;
        this.pointer = pointer;
        this.pointerFormatValue = pointerFormatValue;
        this.suffix = suffix;
        this.maintainUuid = maintainUuid;
    }

    /**
     * @param prefix
     * @param pointer
     * @param pointerFormatValue
     * @param suffix
     * @param maintainUuid
     * @param isFill
     * @param recordUuid
     */
    public SerialNumberInfo(String prefix, Long pointer, String pointerFormatValue, String suffix, String maintainUuid, boolean isFill, String recordUuid) {
        this.prefix = prefix;
        this.pointer = pointer;
        this.pointerFormatValue = pointerFormatValue;
        this.suffix = suffix;
        this.maintainUuid = maintainUuid;
        this.isFill = isFill;
        this.recordUuid = recordUuid;
    }

    /**
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @return the pointer
     */
    public Long getPointer() {
        return pointer;
    }

    /**
     * @return the pointerFormatValue
     */
    public String getPointerFormatValue() {
        return pointerFormatValue;
    }

    /**
     * @return the suffix
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * @return the maintainUuid
     */
    public String getMaintainUuid() {
        return maintainUuid;
    }

    /**
     * @return the suffix
     */
    public boolean isFill() {
        return isFill;
    }

    /**
     * @return the recordUuid
     */
    public String getRecordUuid() {
        return recordUuid;
    }

    /**
     * @param recordUuid 要设置的recordUuid
     */
    public void setRecordUuid(String recordUuid) {
        this.recordUuid = recordUuid;
    }

    /**
     * 返回流水号
     *
     * @return
     */
    public String getSerialNo() {
        return Objects.toString(prefix, StringUtils.EMPTY) + pointerFormatValue + Objects.toString(suffix, StringUtils.EMPTY);
    }

}
