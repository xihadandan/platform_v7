/*
 * @(#)2018年10月17日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.iexport.provider;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.cfg.DataRelation;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IexportMetaData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.iexport.suport.ProtoDataHql;
import com.wellsoft.pt.basicdata.iexport.suport.TableMetaData;
import com.wellsoft.pt.dms.bean.DmsFolderAssignRoleBean;
import com.wellsoft.pt.dms.entity.DmsFolderConfigurationEntity;
import com.wellsoft.pt.dms.entity.DmsFolderEntity;
import com.wellsoft.pt.dms.file.iexport.acceptor.DmsFolderIexportData;
import com.wellsoft.pt.dms.model.DmsFolderConfiguration;
import com.wellsoft.pt.dms.service.DmsFolderConfigurationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
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
 * 2018年10月17日.1	zhulh		2018年10月17日		Create
 * </pre>
 * @date 2018年10月17日
 */
@Service
@Transactional(readOnly = true)
public class DmsFolderIexportDataProvider extends AbstractIexportDataProvider<DmsFolderEntity, String> {

    static {
        // 18.1 文件夹定义
        TableMetaData.register(IexportType.DmsFolder, "文件夹定义", DmsFolderEntity.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.DmsFolder;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getData(java.lang.String)
     */
    @Override
    public IexportData getData(String uuid) {
        DmsFolderEntity dmsFolderEntity = this.dao.get(DmsFolderEntity.class, uuid);
        if (dmsFolderEntity == null) {
            return new ErrorDataIexportData(IexportType.DmsFolder, "找不到对应的文件夹依赖关系,可能已经被删除", "文件夹", uuid);
        }
        return new DmsFolderIexportData(dmsFolderEntity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider#getMetaData()
     */
    @Override
    public IexportMetaData getMetaData() {
        // 操作权限
        DataRelation dmsRoleDataRelation = new DataRelation();
        dmsRoleDataRelation.setReferencedTableName("DMS_OBJECT_ASSIGN_ROLE");
        dmsRoleDataRelation.setReferencedColumnName("role_uuid");
        dmsRoleDataRelation.setSourceType(IexportType.DmsRole);
        dmsRoleDataRelation.setSourceColumnName(IdEntity.UUID);

        IexportMetaData iexportMetaData = new IexportMetaData();
        List<DataRelation> dataRelations = iexportMetaData.getDataRelations();
        dataRelations.add(dmsRoleDataRelation);
        return iexportMetaData;
    }

    @Override
    public String getTreeName(DmsFolderEntity dmsFolderEntity) {
        return new DmsFolderIexportData(dmsFolderEntity).getName();
    }


    @Override
    public void putChildProtoDataHqlParams(DmsFolderEntity dmsFolderEntity, Map<String, DmsFolderEntity> parentMap, Map<String, ProtoDataHql> hqlMap) {
        String start = getType() + Separator.UNDERLINE.getValue();
        DmsFolderConfigurationService dmsFolderConfigurationService = ApplicationContextHolder.getBean(DmsFolderConfigurationService.class);
        DmsFolderConfigurationEntity dmsFolderConfigurationEntity = dmsFolderConfigurationService.getByFolderUuid(dmsFolderEntity.getUuid());
        if (dmsFolderConfigurationEntity == null || StringUtils.isBlank(dmsFolderConfigurationEntity.getConfiguration())) {
            return;
        }
        DmsFolderConfiguration dmsFolderConfiguration = JsonUtils.json2Object(dmsFolderConfigurationEntity.getConfiguration(), DmsFolderConfiguration.class);
        List<DmsFolderAssignRoleBean> assignRoleBeans = dmsFolderConfiguration.getAssignRoles();
        for (DmsFolderAssignRoleBean dmsFolderAssignRoleBean : assignRoleBeans) {
            if (StringUtils.isNotBlank(dmsFolderAssignRoleBean.getRoleUuid())) {
                parentMap.put(start + dmsFolderAssignRoleBean.getRoleUuid(), dmsFolderEntity);
                if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.DmsRole))) {
                    hqlMap.put(this.getChildHqlKey(IexportType.DmsRole), this.getProtoDataHql("DmsRoleEntity"));
                }
                this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.DmsRole)), dmsFolderAssignRoleBean.getRoleUuid());
            }
        }
    }

    @Override
    public Map<String, List<DmsFolderEntity>> getParentMapList(ProtoDataHql protoDataHql) {
        List<DmsFolderEntity> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), DmsFolderEntity.class);
        Map<String, List<DmsFolderEntity>> map = new HashMap<>();
        // 页面或组件定义依赖的文件夹配置
        if (protoDataHql.getParentType().equals(IexportType.AppPageDefinition)
                || protoDataHql.getParentType().equals(IexportType.AppWidgetDefinition)) {
            for (DmsFolderEntity dmsFolderEntity : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + protoDataHql.getParams().get("dependencyUuid");
                this.putParentMap(map, dmsFolderEntity, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.AppFunction)) {
            for (DmsFolderEntity dmsFolderEntity : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + dmsFolderEntity.getUuid();
                this.putParentMap(map, dmsFolderEntity, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;

    }
}
