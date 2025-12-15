/*
 * @(#)11/28/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.web.api;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.dms.bean.DmsFolderBean;
import com.wellsoft.pt.dms.facade.service.DmsFolderMgr;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 11/28/25.1	    zhulh		11/28/25		    Create
 * </pre>
 * @date 11/28/25
 */
@Api(tags = "库及文件夹接口")
@RestController
@RequestMapping("/api/dms/folder")
public class ApiDmsFolderController extends BaseController {

    @Autowired
    private DmsFolderMgr folderMgr;

    /**
     * 获取夹配置
     *
     * @return
     */
    @GetMapping(value = "/config/get", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取库分类", notes = "获取库分类")
    public ApiResult<DmsFolderBean> getConfig(@RequestParam("uuid") String uuid) {
        return ApiResult.success(folderMgr.getBean(uuid));
    }

    /**
     * 保存夹配置
     *
     * @param bean
     * @return
     */
    @PostMapping(value = "/config/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存夹配置", notes = "保存夹配置")
    public ApiResult<Long> save(@RequestBody DmsFolderBean bean) {
        folderMgr.saveBean(bean);
        return ApiResult.success();
    }

}
