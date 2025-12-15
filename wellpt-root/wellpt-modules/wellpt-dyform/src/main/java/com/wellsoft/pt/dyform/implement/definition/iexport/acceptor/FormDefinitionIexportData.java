/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.definition.iexport.acceptor;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.reflection.ReflectionUtils;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataProviderFactory;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataResultSetUtils;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.dyform.implement.definition.entity.FormDefinition;
import com.wellsoft.pt.dyform.implement.definition.service.FormDefinitionService;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 表单定义
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-16.1	zhongzh		2015-6-16		Create
 * </pre>
 * @date 2015-6-16
 */
public class FormDefinitionIexportData extends IexportData {
    public FormDefinition dyFormDefinition;

    public FormDefinitionIexportData(FormDefinition dyFormDefinition) {
        this.dyFormDefinition = dyFormDefinition;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getUuid()
     */
    @Override
    public String getUuid() {
        return dyFormDefinition.getUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.Entry#getName()
     */
    @Override
    public String getName() {
        return "表单：" + dyFormDefinition.getName();
    }

    @Override
    public Integer getRecVer() {
        return dyFormDefinition.getRecVer();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.Entry#getType()
     */
    @Override
    public String getType() {
        return IexportType.FormDefinition;
    }

    /**
     * (non-Javadoc)
     *
     * @throws IOException
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        ReflectionUtils.setFieldValue(dyFormDefinition, "formDefinitionHandler", null);
        return IexportDataResultSetUtils.iexportDataResultSet2InputStream(this,
                dyFormDefinition);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getDependencies()
     */
    @Override
    public List<IexportData> getDependencies() {
        List<IexportData> dependencies = new ArrayList<IexportData>();
        FormDefinitionService dyFormApiFacade = ApplicationContextHolder
                .getBean(FormDefinitionService.class);
        Map<String, Set<String>> dyformDependencies = dyFormApiFacade
                .getResource(dyFormDefinition);
        for (String key : dyformDependencies.keySet()) {
            for (String dataUuid : dyformDependencies.get(key)) {
                if (StringUtils.equals(IexportType.FormDefinition, key) && StringUtils.equals(getUuid(), dataUuid)) {
                    continue;
                }
                dependencies.add(IexportDataProviderFactory
                        .getDataProvider(key).getData(dataUuid));
            }
        }
        return dependencies;
    }

}
