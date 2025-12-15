/*
 * @(#)2015年9月16日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.facade;

import com.wellsoft.pt.basicdata.datadict.dto.CdDataDictionaryItemDto;
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.basicdata.selective.provider.AbstractSelectiveDataProvider;
import com.wellsoft.pt.basicdata.selective.support.DataItem;
import com.wellsoft.pt.basicdata.selective.support.SelectiveData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年9月16日.1	zhulh		2015年9月16日		Create
 * </pre>
 * @date 2015年9月16日
 */
@Component
public class DataDictionarySelectiveDataProvider extends AbstractSelectiveDataProvider {

    @Autowired
    private BasicDataApiFacade basicDataApiFacade;


    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.selective.provider.SelectiveDataProvider#get(java.lang.String)
     */
    @Override
    public SelectiveData get(String configKey) {
        List<CdDataDictionaryItemDto> dataDictionaries = basicDataApiFacade.listDictionaryItemByDictionaryCode(configKey);
        if (dataDictionaries.isEmpty()) {
            return null;
        }
        List<Object> items = new ArrayList<Object>();
        for (CdDataDictionaryItemDto dataDictionary : dataDictionaries) {
            DataItem item = new DataItem();
            item.setAceId(Objects.toString(dataDictionary.getUuid(), StringUtils.EMPTY));
            item.setLabel(dataDictionary.getLabel());
            item.setValue(dataDictionary.getValue());
            items.add(item);
            if (CollectionUtils.isNotEmpty(dataDictionary.getChildren())) {
                convertChildItems(dataDictionary.getChildren(), items);
            }
        }
        SelectiveData selectiveData = new SelectiveData();
        selectiveData.setConfigKey(configKey);
        selectiveData.setProvider(DataDictionarySelectiveDataProvider.class.getSimpleName());
        selectiveData.setItems(items);
        return selectiveData;
    }

    private void convertChildItems(List<CdDataDictionaryItemDto> children, List<Object> items) {
        for (CdDataDictionaryItemDto dataDictionary : children) {
            DataItem item = new DataItem();
            item.setAceId(Objects.toString(dataDictionary.getUuid(), StringUtils.EMPTY));
            item.setLabel(dataDictionary.getLabel());
            item.setValue(dataDictionary.getValue());
            items.add(item);
            if (CollectionUtils.isNotEmpty(dataDictionary.getChildren())) {
                convertChildItems(dataDictionary.getChildren(), items);
            }
        }
    }

}
