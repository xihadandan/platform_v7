/*
 * @(#)2016年8月2日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.function.ext;

import com.wellsoft.pt.app.function.AbstractAppFunctionSourceLoader;
import com.wellsoft.pt.app.function.AppFunctionSource;
import com.wellsoft.pt.app.function.AppFunctionSourceLoader;
import com.wellsoft.pt.app.function.SimpleAppFunctionSource;
import com.wellsoft.pt.app.support.AppFunctionType;
import com.wellsoft.pt.basicdata.datadict.entity.CdDataDictionaryEntity;
import com.wellsoft.pt.basicdata.datadict.service.CdDataDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
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
 * 2016年8月2日.1	zhulh		2016年8月2日		Create
 * </pre>
 * @date 2016年8月2日
 */
@Service
@Transactional(readOnly = true)
public class CdDataDictionaryAppFunctionSourceLoader extends AbstractAppFunctionSourceLoader {

    @Autowired
    private CdDataDictionaryService cdDataDictionaryService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionType()
     */
    @Override
    public String getAppFunctionType() {
        return AppFunctionType.CdDataDictionary;
    }

    @Override
    public List<AppFunctionSource> getAppFunctionSourceByUuid(String uuid) {
        List<AppFunctionSource> appFunctionSources = new ArrayList<AppFunctionSource>();
        CdDataDictionaryEntity DataDictionary = cdDataDictionaryService.getOne(Long.parseLong(uuid));
        if (DataDictionary != null) {
            appFunctionSources.add(convert2AppFunctionSource(DataDictionary));
        }
        return appFunctionSources;
    }

    /**
     * (non-Javadoc)
     *
     * @param id
     * @see AppFunctionSourceLoader#getAppFunctionSourceByUuid(String)
     */
    @Override
    public List<AppFunctionSource> getAppFunctionSourceById(String id) {
        List<AppFunctionSource> appFunctionSources = new ArrayList<AppFunctionSource>();
        CdDataDictionaryEntity dataDictionary = cdDataDictionaryService.getByCode(id);
        if (dataDictionary != null) {
            appFunctionSources.add(convert2AppFunctionSource(dataDictionary));
        }
        return appFunctionSources;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionSources()
     */
    @Override
    public List<AppFunctionSource> getAppFunctionSources() {
        List<AppFunctionSource> appFunctionSources = new ArrayList<AppFunctionSource>();
        List<CdDataDictionaryEntity> dataDictionaries = cdDataDictionaryService.listAll();
        for (CdDataDictionaryEntity dataDictionary : dataDictionaries) {
            appFunctionSources.add(convert2AppFunctionSource(dataDictionary));
        }
        return appFunctionSources;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#convert2AppFunctionSource(Serializable)
     */
    @Override
    public <ITEM extends Serializable> AppFunctionSource convert2AppFunctionSource(ITEM item) {
        CdDataDictionaryEntity dataDictionary = (CdDataDictionaryEntity) item;
        String uuid = Objects.toString(dataDictionary.getUuid());
        String fullName = dataDictionary.getName() + "_" + dataDictionary.getCode();
        String name = "数据字典_" + dataDictionary.getName();
        String id = dataDictionary.getCode();
        String code = dataDictionary.getCode();
        String category = getAppFunctionType();
        return new SimpleAppFunctionSource(uuid, fullName, name, id, code, null, null, category, true, category, true,
                null);
    }

}
