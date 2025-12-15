/*
 * @(#)2/28/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.iexport;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.app.entity.AppThemeDefinitionEntity;
import com.wellsoft.pt.app.iexport.acceptor.AppThemeDefinitionIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.iexport.suport.ProtoDataHql;
import com.wellsoft.pt.basicdata.iexport.suport.TableMetaData;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 * 2/28/23.1	zhulh		2/28/23		Create
 * </pre>
 * @date 2/28/23
 */
@Service
@Transactional(readOnly = true)
public class AppThemeDefinitionIexportDataProvider extends AbstractIexportDataProvider<AppThemeDefinitionEntity, String> {
    static {
        TableMetaData.register(IexportType.AppThemeDefinition, "主题", AppThemeDefinitionEntity.class);
    }

    @Override
    public String getType() {
        return IexportType.AppThemeDefinition;
    }

    @Override
    public IexportData getData(String uuid) {
        AppThemeDefinitionEntity themeDefinitionEntity = this.dao.get(AppThemeDefinitionEntity.class, uuid);
        if (themeDefinitionEntity == null) {
            return new ErrorDataIexportData(getType(), "找不到对应的主题依赖关系", "主题定义", uuid);
        }
        return new AppThemeDefinitionIexportData(themeDefinitionEntity);
    }

    /**
     * 获取treeName
     *
     * @param appThemeDefinitionEntity
     * @return
     */
    @Override
    public String getTreeName(AppThemeDefinitionEntity appThemeDefinitionEntity) {
        return new AppThemeDefinitionIexportData(appThemeDefinitionEntity).getName();
    }

    @Override
    public void putChildProtoDataHqlParams(AppThemeDefinitionEntity themeDefinitionEntity, Map<String, AppThemeDefinitionEntity> parentMap,
                                           Map<String, ProtoDataHql> hqlMap) {
        String start = getType() + Separator.UNDERLINE.getValue();
        String definitionJsonUuid = themeDefinitionEntity.getDefinitionJsonUuid();
        if (StringUtils.isNotBlank(definitionJsonUuid)) {
            parentMap.put(start + "appThemeDefinitionJson" + Separator.UNDERLINE.getValue() + themeDefinitionEntity.getUuid(),
                    themeDefinitionEntity);
            if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.AppThemeDefinitionJson))) {
                hqlMap.put(this.getChildHqlKey(IexportType.AppThemeDefinitionJson), this.getProtoDataHql("AppThemeDefinitionJsonEntity"));
            }
            this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.AppThemeDefinitionJson)), definitionJsonUuid);
        }
    }
}
