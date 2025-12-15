package com.wellsoft.pt.basicdata.layoutdocument.web;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.pt.basicdata.layoutdocument.dto.LayoutDocumentServiceConfDto;
import com.wellsoft.pt.basicdata.layoutdocument.entity.LayoutDocumentServiceConfEntity;
import com.wellsoft.pt.basicdata.layoutdocument.service.LayoutDocumentServiceConfService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "版式文档配置")
@Controller
@RestController
@RequestMapping("/api/basicdata/layoutDocumentServiceConf")
public class LayoutDocumentServiceConfController {

    @Resource
    private LayoutDocumentServiceConfService layoutDocumentServiceConfService;

    /**
     * @param uuid uuid
     * @return ApiResult
     */
    @GetMapping(value = "/getByUuid")
    @ApiOperation(value = "获取系统参数", notes = "根据key获取系统参数")
    public ApiResult<LayoutDocumentServiceConfEntity> getByUuid(@RequestParam(value = "uuid") String uuid) {
        return ApiResult.success(layoutDocumentServiceConfService.getOne(uuid));
    }


    /**
     * 删除版式文档配置
     *
     * @param uuids uuids
     * @return ApiResult
     */
    @PostMapping(value = "/deleteByUuids", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除版式文档配置", notes = "删除版式文档配置")
    public ApiResult<Void> deleteByUuids(@RequestParam(name = "uuids") List<String> uuids) {
        layoutDocumentServiceConfService.deleteByUuids(uuids);
        return ApiResult.success();
    }


    /**
     * 删除版式文档配置
     *
     * @param layoutDocumentServiceConfDto layoutDocumentServiceConfDto
     * @return ApiResult
     */
    @PostMapping(value = "/saveBean", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存版式文档配置", notes = "保存版式文档配置")
    public ApiResult<Void> saveBean(@RequestBody LayoutDocumentServiceConfDto layoutDocumentServiceConfDto) {
        layoutDocumentServiceConfService.saveBean(layoutDocumentServiceConfDto);
        return ApiResult.success();
    }

    /**
     * 启用版式文档配置
     *
     * @return ApiResult  enableLayoutDocumentConfig
     */
    @PostMapping(value = "/changeLayoutDocumentConfigStatus", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "启用版式文档配置", notes = "启用版式文档配置")
    public ApiResult<Boolean> enableLayoutDocumentConfig(@RequestParam(name = "uuid") String uuid, @RequestParam(name = "status") String status) {
        layoutDocumentServiceConfService.changeLayoutDocumentConfigStatus(uuid, status);
        return ApiResult.success();
    }

    /**
     * 获取启用的配置
     *
     * @return ApiResult
     */
    @GetMapping(value = "/beforeEnableLayoutDocumentConfig", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "before启用版式文档配置", notes = "before启用版式文档配置")
    public ApiResult<String> beforeEnableLayoutDocumentConfig(@RequestParam(name = "uuid") String uuid) {
        return ApiResult.success(layoutDocumentServiceConfService.beforeEnableLayoutDocumentConfig(uuid));
    }

    /**
     * 获取启用的配置
     *
     * @return ApiResult
     */
    @GetMapping(value = "/getEnableConfigList", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取启用的配置", notes = "获取启用的配置")
    public ApiResult<List<LayoutDocumentServiceConfEntity>> getEnableConfigList() {
        LayoutDocumentServiceConfEntity queryLayoutDocumentServiceConfEntity = new LayoutDocumentServiceConfEntity();
        queryLayoutDocumentServiceConfEntity.setStatus("1");
        return ApiResult.success(layoutDocumentServiceConfService.listByEntity(queryLayoutDocumentServiceConfEntity));
    }

}
