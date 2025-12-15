/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.imgcategory.web;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.app.service.AppDefElementI18nService;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.imgcategory.dto.CdImgCategoryDto;
import com.wellsoft.pt.basicdata.imgcategory.entity.CdImgCategoryEntity;
import com.wellsoft.pt.basicdata.imgcategory.facade.service.CdImgCategoryFacadeService;
import com.wellsoft.pt.basicdata.imgcategory.service.CdImgCategoryService;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.repository.dto.LogicFileInfoExt;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Api(tags = "图片库分类")
@RestController
@RequestMapping("/api/basicdata/img/category")
public class ImgCategoryController extends BaseController {

    @Autowired
    private MongoFileService mongoFileService;

    @Autowired
    private CdImgCategoryService cdImgCategoryService;

    @Autowired
    private AppDefElementI18nService appDefElementI18nService;

    @ResponseBody
    @GetMapping(value = "/{uuid}")
    @ApiOperation(value = "根据UUID查询图片分类", notes = "根据UUID查询图片分类")
    public ApiResult<CdImgCategoryEntity> get(@PathVariable("uuid") String uuid) {
        CdImgCategoryEntity entity = cdImgCategoryService.getOne(uuid);
        entity.setI18ns(appDefElementI18nService.getI18ns(entity.getUuid(), null, new BigDecimal(1.0), IexportType.CdImgCategory));
        return ApiResult.success(entity);
    }

    @ResponseBody
    @DeleteMapping("/{uuid}")
    @ApiOperation(value = "根据UUID删除图片分类", notes = "根据UUID删除图片分类")
    public ApiResult<?> del(@PathVariable("uuid") String uuid) {
        cdImgCategoryService.delete(uuid);
        return ApiResult.success();
    }

    @ResponseBody
    @PostMapping("/save")
    @ApiOperation(value = "保存图片分类", notes = "保存图片分类")
    public ApiResult<CdImgCategoryEntity> save(@RequestBody CdImgCategoryEntity bean) {
        CdImgCategoryEntity entity = cdImgCategoryService.saveBean(bean);
        return ApiResult.success(entity);
    }

    @ResponseBody
    @GetMapping(value = "/queryAllCategory")
    @ApiOperation(value = "查询所有分类", notes = "查询所有分类")
    public ApiResult<List<CdImgCategoryDto>> queryAllCategory() {
        String systemUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        List<CdImgCategoryEntity> entities = cdImgCategoryService.queryAllCatetory(systemUnitId);
        List<CdImgCategoryDto> categories = Lists.newArrayList();
        for (CdImgCategoryEntity entity : entities) {
            CdImgCategoryDto dto = new CdImgCategoryDto();
            BeanUtils.copyProperties(entity, dto);
            categories.add(dto);
            dto.setI18ns(appDefElementI18nService.getI18ns(entity.getUuid(), null, new BigDecimal(1.0), IexportType.CdImgCategory));
            // 获取分类下fileIDs，用于计算总数和选中数量
            List<LogicFileInfo> files = mongoFileService.getNonioFilesFromFolder(entity.getUuid(),
                    CdImgCategoryFacadeService.FILE_PURPOSE);
            if (null != files) {
                dto.setFileIDs(Lists.transform(files, new Function<LogicFileInfo, String>() {

                    @Override
                    public String apply(LogicFileInfo input) {
                        return input.getUuid();
                    }
                }));
            }
        }
        return ApiResult.success(categories);
    }

    @ResponseBody
    @GetMapping(value = "/queryImgs/{uuid}")
    @ApiOperation(value = "根据图片分类UUID查询图片", notes = "根据图片分类UUID查询图片")
    public ApiResult<List<LogicFileInfoExt>> queryImgs(@PathVariable("uuid") String uuid) {
        List<LogicFileInfoExt> files = mongoFileService.getNonioFilesFromFolderExt(uuid,
                CdImgCategoryFacadeService.FILE_PURPOSE);
        return ApiResult.success(files);
    }

    @ResponseBody
    @PostMapping(value = "/delImgs/{uuid}")
    @ApiOperation(value = "分类删除图片", notes = "分类删除图片")
    public ApiResult<?> delImgs(@PathVariable("uuid") String uuid,
                                @ApiParam(value = "附件ID的JSON串", required = true) @RequestParam("fileIDs") String fileIDs) {
        List<String> fileIDs2 = JsonUtils.json2Object(fileIDs, List.class);
        for (String fileID : fileIDs2) {
            mongoFileService.popFileFromFolder(uuid, fileID);
        }
        return ApiResult.success();
    }

    @ResponseBody
    @PostMapping(value = "/addImgs/{uuid}")
    @ApiOperation(value = "分类添加图片", notes = "分类添加图片")
    public ApiResult<?> addImgs(@PathVariable("uuid") String uuid,
                                @ApiParam(value = "附件ID的JSON串", required = true) @RequestParam("fileIDs") String fileIDs) {
        List<String> fileIDs2 = JsonUtils.json2Object(fileIDs, List.class);
        mongoFileService.pushFilesToFolder(uuid, fileIDs2, CdImgCategoryFacadeService.FILE_PURPOSE);
        return ApiResult.success();
    }
}
