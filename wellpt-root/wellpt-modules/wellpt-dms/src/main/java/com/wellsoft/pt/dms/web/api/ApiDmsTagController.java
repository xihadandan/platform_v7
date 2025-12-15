/*
 * @(#)11/20/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.web.api;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.dms.entity.DmsTagEntity;
import com.wellsoft.pt.dms.service.DmsTagService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 11/20/25.1	    zhulh		11/20/25		    Create
 * </pre>
 * @date 11/20/25
 */
@Api(tags = "标签接口")
@RestController
@RequestMapping("/api/dms/tag")
public class ApiDmsTagController extends BaseController {
    @Autowired
    private DmsTagService tagService;

    /**
     * 保存标签
     *
     * @param tagEntity
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存标签", notes = "保存标签")
    public ApiResult<String> save(@RequestBody DmsTagEntity tagEntity) {
        if (tagEntity.getUuid() == null) {
            tagEntity.setSystem(RequestSystemContextPathResolver.system());
        }
        tagService.save(tagEntity);
        return ApiResult.success(tagEntity.getUuid());
    }

    @ResponseBody
    @GetMapping(value = "/listRootTag", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存标签", notes = "保存标签")
    public ApiResult<List<DmsTagEntity>> listRootTag() {
        return ApiResult.success(tagService.listRootTagBySystem(RequestSystemContextPathResolver.system()));
    }

    /**
     * 删除标签
     *
     * @param uuids
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/deleteAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除标签", notes = "删除标签")
    public ApiResult<Void> deleteAll(@RequestParam(name = "uuids") List<String> uuids) {
        tagService.deleteByUuids(uuids);
        return ApiResult.success();
    }

}
