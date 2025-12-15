/*
 * @(#)2018年10月17日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.iexport.acceptor;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataResultSetUtils;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.dms.bean.DmsFolderAssignRoleBean;
import com.wellsoft.pt.dms.entity.DmsFolderConfigurationEntity;
import com.wellsoft.pt.dms.entity.DmsFolderEntity;
import com.wellsoft.pt.dms.entity.DmsRoleEntity;
import com.wellsoft.pt.dms.model.DmsFolderConfiguration;
import com.wellsoft.pt.dms.service.DmsFolderConfigurationService;
import com.wellsoft.pt.dms.service.DmsRoleService;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
 * 2018年10月17日.1	zhulh		2018年10月17日		Create
 * </pre>
 * @date 2018年10月17日
 */
public class DmsFolderIexportData extends IexportData {

    public DmsFolderEntity dmsFolderEntity;

    /**
     * @param dmsFolderEntity
     */
    public DmsFolderIexportData(DmsFolderEntity dmsFolderEntity) {
        this.dmsFolderEntity = dmsFolderEntity;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getUuid()
     */
    @Override
    public String getUuid() {
        return dmsFolderEntity.getUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getName()
     */
    @Override
    public String getName() {
        return "文件夹：" + dmsFolderEntity.getName();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getType()
     */
    @Override
    public String getType() {
        return IexportType.DmsFolder;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getRecVer()
     */
    @Override
    public Integer getRecVer() {
        return dmsFolderEntity.getRecVer();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return IexportDataResultSetUtils.iexportDataResultSet2InputStream(this, dmsFolderEntity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getDependencies()
     */
    @Override
    public List<IexportData> getDependencies() {
        DmsFolderConfigurationService dmsFolderConfigurationService = ApplicationContextHolder
                .getBean(DmsFolderConfigurationService.class);
        DmsRoleService dmsRoleService = ApplicationContextHolder.getBean(DmsRoleService.class);
        DmsFolderConfigurationEntity dmsFolderConfigurationEntity = dmsFolderConfigurationService
                .getByFolderUuid(dmsFolderEntity.getUuid());
        DmsFolderConfiguration dmsFolderConfiguration = JsonUtils.json2Object(
                dmsFolderConfigurationEntity.getConfiguration(), DmsFolderConfiguration.class);
        List<DmsFolderAssignRoleBean> assignRoleBeans = dmsFolderConfiguration.getAssignRoles();
        List<IexportData> dependencies = new ArrayList<IexportData>();
        for (DmsFolderAssignRoleBean dmsFolderAssignRoleBean : assignRoleBeans) {
            if (StringUtils.isNotBlank(dmsFolderAssignRoleBean.getRoleUuid())) {
                DmsRoleEntity dmsRoleEntity = dmsRoleService.get(dmsFolderAssignRoleBean.getRoleUuid());
                if (dmsRoleEntity != null) {
                    dependencies.add(new DmsRoleIexportData(dmsRoleEntity));
                }
            }
        }
        return dependencies;
    }

}
