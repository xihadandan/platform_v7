/*
 * @(#)7/25/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.dto;

import com.wellsoft.pt.basicdata.serialnumber.entity.SnSerialNumberRecordEntity;
import io.swagger.annotations.ApiModel;
import org.apache.commons.lang.StringUtils;

/**
 * Description: 流水号记录
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 7/25/22.1	zhulh		7/25/22		Create
 * </pre>
 * @date 7/25/22
 */
@ApiModel("流水号记录")
public class SnSerialNumberRecordDto extends SnSerialNumberRecordEntity implements Comparable<SnSerialNumberRecordDto> {

    // 指针格式化值
    private String pointerFormatValue;

    // 是否跳号记录
    private boolean skip;

    /**
     * @return the pointerFormatValue
     */
    public String getPointerFormatValue() {
        if (StringUtils.isNotBlank(pointerFormatValue)) {
            return pointerFormatValue;
        }
        pointerFormatValue = StringUtils.substringBeforeLast(StringUtils.substringAfter(getSerialNo(), getPrefix()), getSuffix());
        return pointerFormatValue;
    }

    /**
     * @param pointerFormatValue 要设置的pointerFormatValue
     */
    public void setPointerFormatValue(String pointerFormatValue) {
        this.pointerFormatValue = pointerFormatValue;
    }


    /**
     * @return
     */
    public boolean isSkip() {
        return skip;
    }

    /**
     * @param skip
     */
    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    /**
     * @param o
     * @return
     */
    @Override
    public int compareTo(SnSerialNumberRecordDto o) {
        Long pointer1 = this.getPointer();
        Long pointer2 = o.getPointer();
        if (pointer1 == null || pointer2 == null) {
            return 0;
        }
        return pointer1.compareTo(pointer2);
    }
}
