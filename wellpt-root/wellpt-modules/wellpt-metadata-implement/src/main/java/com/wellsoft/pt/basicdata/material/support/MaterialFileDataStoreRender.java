/*
 * @(#)5/6/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.material.support;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.datastore.support.RendererParam;
import com.wellsoft.pt.basicdata.datastore.support.renderer.AbstractDataStoreRenderer;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 5/6/23.1	zhulh		5/6/23		Create
 * </pre>
 * @date 5/6/23
 */
@Component
public class MaterialFileDataStoreRender extends AbstractDataStoreRenderer {

    @Autowired
    private MongoFileService mongoFileService;

    /**
     * 渲染器名称
     *
     * @return
     */
    @Override
    public String getName() {
        return "材料文件数据仓库渲染器";
    }

    /**
     * 渲染器类型
     *
     * @return
     */
    @Override
    public String getType() {
        return "materialFileDataStoreRender";
    }

    /**
     * @param columnIndex
     * @param value
     * @param rowData
     * @param param
     * @return
     */
    @Override
    public Object doRenderData(String columnIndex, Object value, Map<String, Object> rowData, RendererParam param) {
        String repoFileUuids = Objects.toString(value, StringUtils.EMPTY);
        if (StringUtils.isEmpty(repoFileUuids)) {
            return Collections.emptyList();
        }
        List<LogicFileInfo> logicFileInfos = Lists.newArrayList();
        List<String> fileIds = Arrays.asList(StringUtils.split(repoFileUuids, Separator.SEMICOLON.getValue()));
        fileIds.forEach(fileId -> {
            LogicFileInfo logicFileInfo = mongoFileService.getLogicFileInfo(fileId);
            if (logicFileInfo != null) {
                logicFileInfos.add(logicFileInfo);
            }
        });
        rowData.put("logicFileInfos", logicFileInfos);
        return value;
    }

}
