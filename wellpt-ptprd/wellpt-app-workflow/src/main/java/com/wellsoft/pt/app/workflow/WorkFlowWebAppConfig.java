/*
 * @(#)2017-01-20 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow;

import com.wellsoft.pt.app.context.AppContextConfigurerAdapter;
import com.wellsoft.pt.app.context.config.PropertiesRegistry;
import com.wellsoft.pt.app.workflow.ext.dms.file.viewer.WorkflowMediaType;
import com.wellsoft.pt.dms.file.registry.DmsFileTypeRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-01-20.1	zhulh		2017-01-20		Create
 * </pre>
 * @date 2017-01-20
 */
@Configuration
public class WorkFlowWebAppConfig extends AppContextConfigurerAdapter {

    @Autowired
    private DmsFileTypeRegistry dmsFileTypeRegistry;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContextConfigurerAdapter#addProperties(java.lang.String)
     */
    @Override
    public void addProperties(PropertiesRegistry propertiesRegistry) {

        // 注册工作流数据的文件类型
        dmsFileTypeRegistry.register(WorkflowMediaType.APPLICATION_WORKFLOW_VALUE,
                WorkflowMediaType.APPLICATION_WORKFLOW_NAME, false);
        dmsFileTypeRegistry.register(WorkflowMediaType.APPLICATION_WORKFLOW_DYFORM_VALUE,
                WorkflowMediaType.APPLICATION_WORKFLOW_DYFORM_NAME, false);
    }

}
