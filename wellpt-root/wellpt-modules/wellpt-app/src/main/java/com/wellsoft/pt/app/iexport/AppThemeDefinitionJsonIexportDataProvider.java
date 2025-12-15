/*
 * @(#)2/28/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.iexport;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.app.entity.AppThemeDefinitionEntity;
import com.wellsoft.pt.app.entity.AppThemeDefinitionJsonEntity;
import com.wellsoft.pt.app.iexport.acceptor.AppThemeDefinitionJsonIexportData;
import com.wellsoft.pt.app.service.AppThemeDefinitionService;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.iexport.suport.ProtoDataHql;
import com.wellsoft.pt.basicdata.iexport.suport.TableMetaData;
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
 * 2/28/23.1	zhulh		2/28/23		Create
 * </pre>
 * @date 2/28/23
 */
@Service
@Transactional(readOnly = true)
public class AppThemeDefinitionJsonIexportDataProvider extends AbstractIexportDataProvider<AppThemeDefinitionJsonEntity, String> {
    static {
        TableMetaData.register(IexportType.AppThemeDefinitionJson, "主题定义JSON", AppThemeDefinitionJsonEntity.class);
    }

    @Override
    public String getType() {
        return IexportType.AppThemeDefinitionJson;
    }

    @Override
    public IexportData getData(String uuid) {
        AppThemeDefinitionJsonEntity themeDefinitionJsonEntity = this.dao.get(AppThemeDefinitionJsonEntity.class, uuid);
        if (themeDefinitionJsonEntity == null) {
            return new ErrorDataIexportData(getType(), "找不到对应的主题依赖关系", "主题定义JSON", uuid);
        }
        return new AppThemeDefinitionJsonIexportData(themeDefinitionJsonEntity);
    }

    /**
     * 获取treeName
     *
     * @param appThemeDefinitionJsonEntity
     * @return
     */
    @Override
    public String getTreeName(AppThemeDefinitionJsonEntity appThemeDefinitionJsonEntity) {
        return new AppThemeDefinitionJsonIexportData(appThemeDefinitionJsonEntity).getName();
    }


    @Override
    public Map<String, List<AppThemeDefinitionJsonEntity>> getParentMapList(ProtoDataHql protoDataHql) {
        List<AppThemeDefinitionJsonEntity> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), AppThemeDefinitionJsonEntity.class);
        Map<String, List<AppThemeDefinitionJsonEntity>> map = new HashMap<>();
        if (protoDataHql.getParentType().equals(IexportType.AppThemeDefinition)) {
            AppThemeDefinitionService themeDefinitionService = ApplicationContextHolder.getBean(AppThemeDefinitionService.class);
            for (AppThemeDefinitionJsonEntity themeDefinitionJsonEntity : list) {
                AppThemeDefinitionEntity themeDefinitionEntity = themeDefinitionService.getByDefinitionJsonUuid(themeDefinitionJsonEntity.getUuid());
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + "appThemeDefinitionJson"
                        + Separator.UNDERLINE.getValue() + themeDefinitionEntity != null ? themeDefinitionEntity.getUuid() : StringUtils.EMPTY;
                this.putParentMap(map, themeDefinitionJsonEntity, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;
    }
}
