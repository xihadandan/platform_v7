/*
 * @(#)8/18/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.web;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.app.dto.AppDataDefinitionRefResourceDto;
import com.wellsoft.pt.app.service.AppDataDefinitionRefResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
 * 8/18/23.1	zhulh		8/18/23		Create
 * </pre>
 * @date 8/18/23
 */
@Controller
@RequestMapping(value = "/api/app/datadef")
public class AppDataDefinitionRefResourceController extends BaseController {

    @Autowired
    private AppDataDefinitionRefResourceService dataDefinitionRefResourceService;

    /**
     * 保存数据定义引用资源
     *
     * @return
     */
    @RequestMapping(value = "/saveRefResources/{dataDefUuid}")
    @ResponseBody
    public ApiResult<Void> saveFunctionElements(@PathVariable(name = "dataDefUuid") String dataDefUuid,
                                                @RequestBody List<AppDataDefinitionRefResourceDto> refResourceDtos) {
        dataDefinitionRefResourceService.saveRefResources(dataDefUuid, refResourceDtos);
        return ApiResult.success();
    }
    
}
