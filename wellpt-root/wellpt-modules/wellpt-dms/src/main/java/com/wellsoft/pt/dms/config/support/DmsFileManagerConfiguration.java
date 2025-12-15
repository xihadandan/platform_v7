/*
 * @(#)Jan 22, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.config.support;

import com.google.common.collect.Lists;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.app.ui.client.widget.configuration.FunctionElement;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.dms.config.component.MobileDataManagementViewerComponent;
import com.wellsoft.pt.dms.config.component.MobileFileManagerComponent;
import com.wellsoft.pt.dms.core.context.ActionContext;
import com.wellsoft.pt.dms.model.DmsFolderDyformDefinition;
import com.wellsoft.pt.dms.service.DmsFileService;
import com.wellsoft.pt.dms.service.DmsFolderConfigurationService;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
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
 * Jan 22, 2018.1	zhulh		Jan 22, 2018		Create
 * </pre>
 * @date Jan 22, 2018
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DmsFileManagerConfiguration extends DmsDataManagementViewerConfiguration {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -4644194815208142876L;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private String belongToFolderUuid;

    private FileStore store;

    private DmsFolderDyformDefinition folderDyformDefinition;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.config.support.DmsDataManagementViewerConfiguration#getStore()
     */
    @Override
    public Store getStore() {
        return this.store;
    }

    /**
     * @return the belongToFolderUuid
     */
    public String getBelongToFolderUuid() {
        return belongToFolderUuid;
    }

    /**
     * @param belongToFolderUuid 要设置的belongToFolderUuid
     */
    public void setBelongToFolderUuid(String belongToFolderUuid) {
        this.belongToFolderUuid = belongToFolderUuid;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.config.support.DmsDataManagementViewerConfiguration#getStore()
     */
    @Override
    public Store getStore(ActionContext actionContext, HttpServletRequest request) {
        if (request == null && this.store != null) {
            return this.store;
        }
        String folderUuid = request.getParameter("fd_id");
        String fileUuid = request.getParameter("doc_id");
        if (StringUtils.isBlank(folderUuid) && StringUtils.isBlank(fileUuid) && this.store != null) {
            return this.store;
        }
        if (StringUtils.equals(folderUuid, belongToFolderUuid) && this.store != null) {
            return store;
        }
        if (StringUtils.isBlank(folderUuid)) {
            folderUuid = belongToFolderUuid;
        }
        if (StringUtils.isBlank(folderUuid) && StringUtils.isNotBlank(fileUuid)) {
            DmsFileService dmsFileService = ApplicationContextHolder.getBean(DmsFileService.class);
            folderUuid = dmsFileService.get(fileUuid).getFolderUuid();
        }
        createStore(folderUuid);
        return store;
    }

    /**
     * @param dmsFolderConfigurationService
     * @param folderUuid
     */
    private void createStore(String folderUuid) {
        DmsFolderConfigurationService dmsFolderConfigurationService = ApplicationContextHolder
                .getBean(DmsFolderConfigurationService.class);
        // 获取夹的表单配置信息
        folderDyformDefinition = dmsFolderConfigurationService.getFolderDyformDefinitionByFolderUuid(folderUuid);
        if (folderDyformDefinition == null) {
            logger.warn("夹[{}]的表单配置为空！", folderUuid);
            return;
        }
        store = new FileStore();
        store.setFolderUuid(folderUuid);
        store.setDataType(folderDyformDefinition.getDataType());
        store.setFormUuid(folderDyformDefinition.getFormUuid());
        store.setFormName(folderDyformDefinition.getFormName());
        String displayFormUuid, displayFormName;
        if (MobileDataManagementViewerComponent.TYPE.equals(getType())
                || MobileFileManagerComponent.TYPE.equals(getType())) {
            displayFormUuid = folderDyformDefinition.getDisplayMFormUuid();
            displayFormName = folderDyformDefinition.getDisplayMFormName();
        } else {
            displayFormUuid = folderDyformDefinition.getDisplayFormUuid();
            displayFormName = folderDyformDefinition.getDisplayFormName();
        }
        store.setDisplayFormUuid(displayFormUuid);
        store.setDisplayFormName(displayFormName);
        store.setStick(folderDyformDefinition.isStick());
        store.setStickStatusField(folderDyformDefinition.getStickStatusField());
        store.setStickTimeField(folderDyformDefinition.getStickTimeField());
        store.setReadRecord(folderDyformDefinition.isReadRecord());
        store.setReadRecordField(folderDyformDefinition.getReadRecordField());
    }

    /**
     * @param folderUuid
     * @return
     */
    public String getFileNameField(String folderUuid) {
        if (folderDyformDefinition != null) {
            return folderDyformDefinition.getFileNameField();
        }
        DmsFolderConfigurationService dmsFolderConfigurationService = ApplicationContextHolder
                .getBean(DmsFolderConfigurationService.class);
        folderDyformDefinition = dmsFolderConfigurationService.getFolderDyformDefinitionByFolderUuid(folderUuid);
        return folderDyformDefinition.getFileNameField();
    }

    /**
     * @param folderUuid
     * @return
     */
    public String getFileStatusField(String folderUuid) {
        if (folderDyformDefinition != null) {
            return folderDyformDefinition.getFileStatusField();
        }
        DmsFolderConfigurationService dmsFolderConfigurationService = ApplicationContextHolder
                .getBean(DmsFolderConfigurationService.class);
        folderDyformDefinition = dmsFolderConfigurationService.getFolderDyformDefinitionByFolderUuid(folderUuid);
        return folderDyformDefinition.getFileStatusField();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.config.support.Configuration#loadStore(java.lang.String)
     */
    public void loadStore(String folderUuid) {
        if (StringUtils.isBlank(folderUuid)) {
            return;
        }
        if (StringUtils.equals(folderUuid, belongToFolderUuid) && getStore() != null) {
            return;
        }
        createStore(folderUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.config.support.DmsDataManagementViewerConfiguration#isEnableStick()
     */
    @Override
    public boolean isEnableStick() {
        if (getStore() == null) {
            loadStore(getBelongToFolderUuid());
        }
        return getStore().isStick();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.config.support.DmsDataManagementViewerConfiguration#getStickStatusField()
     */
    @Override
    public String getStickStatusField() {
        if (getStore() == null) {
            loadStore(getBelongToFolderUuid());
        }
        return getStore().getStickStatusField();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.config.support.DmsDataManagementViewerConfiguration#getStickTimeField()
     */
    @Override
    public String getStickTimeField() {
        if (getStore() == null) {
            loadStore(getBelongToFolderUuid());
        }
        return getStore().getStickTimeField();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.AbstractWidgetConfiguration#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isNotBlank(this.belongToFolderUuid)) {
            createStore(this.belongToFolderUuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.config.support.DmsDataManagementViewerConfiguration#getFunctionElements()
     */
    @Override
    public List<FunctionElement> getFunctionElements() {
        List<FunctionElement> functionElements = Lists.newArrayList();
        if (StringUtils.isNotBlank(belongToFolderUuid)) {
            appendRefFunctionElement(belongToFolderUuid, IexportType.DmsFolder, functionElements);
        }
        return functionElements;
    }

}
