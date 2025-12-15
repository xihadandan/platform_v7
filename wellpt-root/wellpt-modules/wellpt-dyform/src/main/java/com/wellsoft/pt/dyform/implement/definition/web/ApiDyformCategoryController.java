/*
 * @(#)1/31/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.definition.web;

import com.wellsoft.context.jdbc.entity.JpaEntity;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.dyform.implement.definition.dto.DyformCategoryDto;
import com.wellsoft.pt.dyform.implement.definition.entity.DyformCategoryEntity;
import com.wellsoft.pt.dyform.implement.definition.service.DyformCategoryService;
import com.wellsoft.pt.jpa.util.BeanUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description: 表单分类api
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 1/31/24.1	zhulh		1/31/24		Create
 * </pre>
 * @date 1/31/24
 */
@Api(tags = "表单分类")
@Controller
@RequestMapping("/api/dyform/category")
public class ApiDyformCategoryController extends BaseController {

    @Autowired
    private DyformCategoryService dyformCategoryService;

    /**
     * 表单分类查询
     *
     * @param keyword
     * @param moduleId
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/query", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "表单分类查询", notes = "表单分类查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "查询关键字", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "moduleId", value = "模块ID", paramType = "query", dataType = "String", required = false)})
    public ApiResult<List<DyformCategoryDto>> query(@RequestParam(name = "keyword", required = false) String keyword,
                                                    @RequestParam(name = "moduleId", required = false) String moduleId) {
        List<DyformCategoryEntity> entities = null;
        if (StringUtils.isNotBlank(keyword)) {
            entities = dyformCategoryService.listByKeywordAndModuleId(keyword, moduleId);
        } else if (StringUtils.isNotBlank(moduleId)) {
            entities = dyformCategoryService.listByModuleId(moduleId);
        } else {
            entities = dyformCategoryService.listAllByOrderPage(null, "code asc");
        }
        return ApiResult.success(BeanUtils.copyCollection(entities, DyformCategoryDto.class));
    }

    /**
     * 获取表单分类
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取表单分类", notes = "获取表单分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "表单分类UUID", paramType = "query", dataType = "Long", required = true)})
    public ApiResult<DyformCategoryDto> get(@RequestParam(name = "uuid", required = true) Long uuid) {
        DyformCategoryDto dto = new DyformCategoryDto();
        DyformCategoryEntity entity = dyformCategoryService.getOne(uuid);
        if (entity != null) {
            BeanUtils.copyProperties(entity, dto);
        }
        return ApiResult.success(dto);
    }

    /**
     * 保存表单分类
     *
     * @param dto
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存表单分类", notes = "保存表单分类")
    public ApiResult<Long> save(@RequestBody DyformCategoryDto dto) {
        DyformCategoryEntity entity = new DyformCategoryEntity();
        if (dto.getUuid() != null) {
            entity = dyformCategoryService.getOne(dto.getUuid());
        }
        BeanUtils.copyProperties(dto, entity, JpaEntity.BASE_FIELDS);
        dyformCategoryService.save(entity);
        Long parentUuid = entity.getParentUuid();
        // 上级分类变更为自己的下级节点，下级节点的上级分类设置为空
        if (parentUuid != null) {
            DyformCategoryEntity parentEntity = dyformCategoryService.getOne(parentUuid);
            if (parentEntity != null && entity.getUuid().equals(parentEntity.getParentUuid())) {
                parentEntity.setParentUuid(null);
                dyformCategoryService.save(parentEntity);
            }
        }
        return ApiResult.success(entity.getUuid());
    }

    /**
     * 删除表单分类
     *
     * @param uuids
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/deleteAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除表单分类", notes = "删除表单分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuids", value = "表单分类UUID", paramType = "query", dataType = "List<Long>", required = true)})
    public ApiResult<Void> deleteAll(@RequestParam(name = "uuids", required = true) List<Long> uuids) {
        dyformCategoryService.deleteByUuids(uuids);
        return ApiResult.success();
    }

}
