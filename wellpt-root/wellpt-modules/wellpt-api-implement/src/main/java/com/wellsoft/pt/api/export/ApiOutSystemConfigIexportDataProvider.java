/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.export;

import com.wellsoft.pt.api.entity.ApiOutSystemConfigEntity;
import com.wellsoft.pt.api.service.ApiOutSysConfigService;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.iexport.suport.TableMetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description: 消息模板
 *
 * @author linz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-1-19.1	linz		2016-1-19		Create
 * </pre>
 * @date 2016-1-19
 */
@Service
public class ApiOutSystemConfigIexportDataProvider extends AbstractIexportDataProvider<ApiOutSystemConfigEntity, String> {
    static {
        TableMetaData.register(IexportType.ApiSysConfig, "API对接系统配置导出",
                ApiOutSystemConfigEntity.class);
    }

    @Autowired
    ApiOutSysConfigService apiOutSysConfigService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.ApiSysConfig;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getData(String)
     */
    @Override
    public IexportData getData(String uuid) {
        ApiOutSystemConfigEntity confEntity = apiOutSysConfigService.getOne(uuid);
        if (confEntity == null) {
            return new ErrorDataIexportData(IexportType.BotRuleConf, "找不到对应的Api对接系统配置,可能已经被删除",
                    "Api对接系统配置", uuid);
        }
        return new ApiOutSystemConfigIexportData(confEntity);
    }

    @Override
    public String getTreeName(ApiOutSystemConfigEntity apiOutSystemConfigEntity) {
        return new ApiOutSystemConfigIexportData(apiOutSystemConfigEntity).getName();
    }
}
