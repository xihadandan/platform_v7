/*
 * @(#)8/9/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.dto;

import com.wellsoft.pt.basicdata.datadict.entity.CdDataDictionaryEntity;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 8/9/23.1	zhulh		8/9/23		Create
 * </pre>
 * @date 8/9/23
 */
public class CdDataDictionaryDto extends CdDataDictionaryEntity {

    @ApiModelProperty("字典数据项")
    private List<CdDataDictionaryItemDto> items;

    /**
     * @return the items
     */
    public List<CdDataDictionaryItemDto> getItems() {
        return items;
    }

    /**
     * @param items 要设置的items
     */
    public void setItems(List<CdDataDictionaryItemDto> items) {
        this.items = items;
    }
    
}
