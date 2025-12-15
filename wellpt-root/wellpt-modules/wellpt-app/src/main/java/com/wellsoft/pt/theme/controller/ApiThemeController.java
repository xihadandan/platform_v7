package com.wellsoft.pt.theme.controller;

import com.google.common.collect.Maps;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.theme.controller.request.ThemePackQueryRequest;
import com.wellsoft.pt.theme.dto.ThemePackDto;
import com.wellsoft.pt.theme.dto.ThemeSpecificationDto;
import com.wellsoft.pt.theme.entity.ThemePackEntity;
import com.wellsoft.pt.theme.entity.ThemeTagEntity;
import com.wellsoft.pt.theme.service.ThemePackService;
import com.wellsoft.pt.theme.service.ThemeSpecificationService;
import com.wellsoft.pt.theme.service.ThemeTagService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年06月19日   Qiong	 Create
 * </pre>
 */
@RestController
@RequestMapping("/api/theme")
public class ApiThemeController extends BaseController {

    @Resource
    ThemeSpecificationService themeSpecificationService;

    @Resource
    ThemePackService themePackService;

    @Resource
    ThemeTagService themeTagService;

    @PostMapping("/specify/save")
    public ApiResult<Long> saveSpecify(@RequestBody ThemeSpecificationDto dto) {
        return ApiResult.success(themeSpecificationService.saveSpecify(dto));
    }

    @GetMapping("/specify/getEnabled")
    public ApiResult<ThemeSpecificationDto> getEnableThemeSpecify() {
        return ApiResult.success(themeSpecificationService.getEnableThemeSpecify());
    }

    @GetMapping("/specify/details/{uuid}")
    public ApiResult<ThemeSpecificationDto> getThemeSpecify(@PathVariable Long uuid) {
        return ApiResult.success(themeSpecificationService.getDetails(uuid));
    }

    @GetMapping("/specify/delete/{uuid}")
    public ApiResult<Boolean> deleteThemeSpecify(@PathVariable Long uuid) {
        themeSpecificationService.deleteThemeSpecify(uuid);
        return ApiResult.success(true);
    }

    @GetMapping("/specify/getAll")
    public ApiResult<List<ThemeSpecificationDto>> getAllThemeSpecify() {
        return ApiResult.success(themeSpecificationService.getAll());
    }

    @GetMapping("/pack/details/{uuid}")
    public ApiResult<ThemePackDto> queryThemePack(@PathVariable Long uuid) {
        return ApiResult.success(themePackService.getDetails(uuid));
    }

    @GetMapping("/pack/getAllPublished")
    public ApiResult<List<ThemePackEntity>> getAllPublished(@RequestParam(required = false) ThemePackEntity.Type type) {
        return ApiResult.success(themePackService.getAllPublished(type));
    }

    @PostMapping("/pack/listDetailsByUuids")
    public ApiResult<List<ThemePackEntity>> listDetailsByUuids(@RequestBody List<Long> uuids) {
        return ApiResult.success(themePackService.listDetailsByUuids(uuids));
    }

    @PostMapping("/pack/listDetailsByThemeClasses")
    public ApiResult<List<ThemePackEntity>> listDetailsByThemeClasses(@RequestBody List<String> themeClass) {
        return ApiResult.success(themePackService.listDetailsByThemeClasses(themeClass));
    }

    @PostMapping("/pack/listDetailsIgnoreDefJsonByThemeClasses")
    public ApiResult<List<ThemePackEntity>> listDetailsIgnoreDefJsonByThemeClasses(@RequestBody List<String> themeClass) {
        return ApiResult.success(themePackService.listDetailsIgnoreDefJsonByThemeClasses(themeClass));
    }

    @PostMapping("/pack/query")
    public ApiResult<Map> queryThemePack(@RequestBody ThemePackQueryRequest request) {
        if (request.getPage() != null) {
            request.getPage().setAutoCount(false);
        }
        List<ThemePackDto> dtos = themePackService.query(request.getTagUuids(), request.getType(), request.getStatus(), request.getPage(), request.getKeyword());
        Map<String, Object> result = Maps.newHashMap();
        result.put("data", dtos);
        result.put("page", request.getPage());
        return ApiResult.success(result);
    }


    @GetMapping("/pack/copy/{uuid}")
    public ApiResult<Long> copyThemePack(@PathVariable Long uuid, @RequestParam(required = false) String name, @RequestParam ThemePackEntity.Type type, @RequestParam(required = false) String themeClass) {
        return ApiResult.success(themePackService.copyThemePack(uuid, name, type, themeClass));
    }

    @GetMapping("/pack/getUuidByClass/{themeClass}")
    public ApiResult<Long> getUuidByClass(@PathVariable String themeClass) {
        ThemePackEntity packEntity = themePackService.getByThemeClass(themeClass);
        return ApiResult.success(packEntity != null ? packEntity.getUuid() : null);
    }


    @GetMapping("/pack/delete/{uuid}")
    public ApiResult<Boolean> deleteThemePack(@PathVariable Long uuid) {
        themePackService.deleteThemePack(uuid);
        return ApiResult.success(true);
    }


    @PostMapping("/pack/save")
    public ApiResult<Long> saveThemePack(@RequestBody ThemePackDto dto) {
        return ApiResult.success(themePackService.saveThemePack(dto));
    }

    @GetMapping("/tag/create")
    public ApiResult<Long> createTag(@RequestParam String name) {
        return ApiResult.success(themeTagService.createTag(name));
    }

    @GetMapping("/tag/list")
    public ApiResult<List<ThemeTagEntity>> tagList() {
        return ApiResult.success(themeTagService.listAll());
    }

    @GetMapping("/pack/publish/{uuid}")
    public ApiResult<Boolean> publishThemePack(@PathVariable Long uuid) {
        themePackService.updateStatus(uuid, ThemePackEntity.Status.PUBLISHED);
        return ApiResult.success(true);
    }

    @GetMapping("/pack/unPublish/{uuid}")
    public ApiResult<Boolean> unPublishThemePack(@PathVariable Long uuid) {
        themePackService.updateStatus(uuid, ThemePackEntity.Status.UNPUBLISHED);
        return ApiResult.success(true);
    }

}
