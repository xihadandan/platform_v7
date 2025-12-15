/*
 * @(#)2019年6月18日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui.client.widget.configuration;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.app.support.AppFunctionType;
import com.wellsoft.pt.app.ui.AbstractWidgetConfiguration;
import com.wellsoft.pt.basicdata.datadict.entity.DataDictionary;
import com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService;
import com.wellsoft.pt.basicdata.datastore.bean.CdDataStoreDefinitionBean;
import com.wellsoft.pt.basicdata.datastore.service.CdDataStoreDefinitionService;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

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
 * 2019年6月18日.1	zhulh		2019年6月18日		Create
 * </pre>
 * @date 2019年6月18日
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppWidgetDefinitionConfiguration extends AbstractWidgetConfiguration {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -5822854867561523756L;

    /**
     * @param dataStoreId
     * @param functionElements
     */
    protected void appendRefDataStoreFunctionElementById(String dataStoreId, List<FunctionElement> functionElements) {
        if (StringUtils.isBlank(dataStoreId)) {
            return;
        }
        CdDataStoreDefinitionService cdDataStoreDefinitionService = ApplicationContextHolder
                .getBean(CdDataStoreDefinitionService.class);
        CdDataStoreDefinitionBean dataStoreDefinitionBean = cdDataStoreDefinitionService.getBeanById(dataStoreId);
        if (dataStoreDefinitionBean != null) {
            appendRefFunctionElement(dataStoreDefinitionBean.getUuid(), AppFunctionType.DataStoreDefinition,
                    functionElements);
        }
    }

    protected void appendRefDataDicFunctionElementByUuid(String uuid, List<FunctionElement> functionElements) {
        if (StringUtils.isBlank(uuid)) {
            return;
        }
        DataDictionaryService dataDictionaryService = ApplicationContextHolder.getBean(DataDictionaryService.class);
        DataDictionary dataDictionary = dataDictionaryService.get(uuid);
        if (dataDictionary != null) {
            appendRefFunctionElement(dataDictionary.getUuid(), AppFunctionType.DataDictionary, functionElements);
        }
    }

    /**
     * @param formUuid
     * @param functionElements
     */
    protected void appendRefDyformFunctionElementByUuid(String formUuid, List<FunctionElement> functionElements) {
        if (StringUtils.isBlank(formUuid)) {
            return;
        }
        appendRefFunctionElement(formUuid, AppFunctionType.DyFormFormDefinition, functionElements);
    }

    /**
     * @param uuid
     * @param functionType
     * @param functionElements
     */
    protected void appendRefFunctionElement(String uuid, String functionType, List<FunctionElement> functionElements) {
        FunctionElement functionElement = new FunctionElement();
        functionElement.setUuid(uuid);
        functionElement.setFunctionType(functionType);
        functionElement.setRef(true);
        functionElements.add(functionElement);
    }

}
