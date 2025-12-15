/*
 * @(#)2018年10月17日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.iexport.provider;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.iexport.suport.ProtoDataHql;
import com.wellsoft.pt.basicdata.iexport.suport.TableMetaData;
import com.wellsoft.pt.dms.entity.DmsRoleEntity;
import com.wellsoft.pt.dms.file.iexport.acceptor.DmsRoleIexportData;
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
public class DmsRoleIexportDataProvider extends AbstractIexportDataProvider<DmsRoleEntity, String> {

    static {
        // 18.2 文件操作权限
        TableMetaData.register(IexportType.DmsRole, "文件操作权限定义", DmsRoleEntity.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.DmsRole;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getData(java.lang.String)
     */
    @Override
    public IexportData getData(String uuid) {
        DmsRoleEntity dmsRoleEntity = this.dao.get(DmsRoleEntity.class, uuid);
        if (dmsRoleEntity == null) {
            return new ErrorDataIexportData(IexportType.DmsRole, "找不到对应的文件夹依赖关系,可能已经被删除", "文件操作权限", uuid);
        }
        return new DmsRoleIexportData(dmsRoleEntity);
    }

    @Override
    public String getTreeName(DmsRoleEntity dmsRoleEntity) {
        return new DmsRoleIexportData(dmsRoleEntity).getName();
    }


    @Override
    public Map<String, List<DmsRoleEntity>> getParentMapList(ProtoDataHql protoDataHql) {
        List<DmsRoleEntity> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), DmsRoleEntity.class);
        Map<String, List<DmsRoleEntity>> map = new HashMap<>();
        // 页面或组件定义依赖的文件夹角色配置
        if (protoDataHql.getParentType().equals(IexportType.AppPageDefinition)
                || protoDataHql.getParentType().equals(IexportType.AppWidgetDefinition)) {
            for (DmsRoleEntity dmsRoleEntity : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + protoDataHql.getParams().get("dependencyUuid");
                this.putParentMap(map, dmsRoleEntity, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.DmsFolder)) {
            for (DmsRoleEntity dmsRoleEntity : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + dmsRoleEntity.getUuid();
                this.putParentMap(map, dmsRoleEntity, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.AppFunction)) {
            for (DmsRoleEntity dmsRoleEntity : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + dmsRoleEntity.getUuid();
                this.putParentMap(map, dmsRoleEntity, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;
    }
}
