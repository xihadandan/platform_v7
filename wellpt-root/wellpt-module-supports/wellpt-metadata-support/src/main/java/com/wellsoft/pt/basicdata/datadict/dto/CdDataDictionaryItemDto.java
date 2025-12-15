/*
 * @(#)8/10/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.dto;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.pt.basicdata.datadict.entity.CdDataDictI18nEntity;
import com.wellsoft.pt.basicdata.datadict.entity.CdDataDictionaryItemEntity;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 8/10/23.1	zhulh		8/10/23		Create
 * </pre>
 * @date 8/10/23
 */
public class CdDataDictionaryItemDto extends CdDataDictionaryItemEntity {

    @ApiModelProperty("子项数据")
    private List<CdDataDictionaryItemDto> children = Lists.newArrayListWithCapacity(0);

    @ApiModelProperty("扩展属性")
    private Map<String, String> attrs = Maps.newLinkedHashMap();

    @ApiModelProperty("是否为父项")
    private boolean isParent;

    /**
     * @return the children
     */
    public List<CdDataDictionaryItemDto> getChildren() {
        return children;
    }

    public List<CdDataDictI18nEntity> i18ns;

    /**
     * @param children 要设置的children
     */
    public void setChildren(List<CdDataDictionaryItemDto> children) {
        this.children = children;
    }

    /**
     * @return the attrs
     */
    public Map<String, String> getAttrs() {
        return attrs;
    }

    /**
     * @param attrs 要设置的attrs
     */
    public void setAttrs(Map<String, String> attrs) {
        this.attrs = attrs;
    }

    /**
     * @return the isParent
     */
    public boolean isParent() {
        return isParent || CollectionUtils.isNotEmpty(children);
    }

    /**
     * @param isParent 要设置的isParent
     */
    public void setParent(boolean parent) {
        isParent = parent;
    }

    public List<CdDataDictI18nEntity> getI18ns() {
        return i18ns;
    }

    public void setI18ns(List<CdDataDictI18nEntity> i18ns) {
        this.i18ns = i18ns;
    }
}
