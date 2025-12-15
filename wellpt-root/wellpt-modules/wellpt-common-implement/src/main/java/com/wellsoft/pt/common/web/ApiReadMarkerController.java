package com.wellsoft.pt.common.web;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.common.marker.service.ReadMarkerService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Auther: yt
 * @Date: 2021/4/8 14:28
 * @Description:
 */
@Api(tags = "阅读记录")
@RestController
@RequestMapping("/api/readMarker")
@Validated
public class ApiReadMarkerController extends BaseController {

    @Autowired
    private ReadMarkerService readMarkerService;

    @PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "添加阅读记录", notes = "添加阅读记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tagDataKeys", value = "标记数据唯一id集合多个值,分割", paramType = "query", dataType = "String", required = true)})
    public ApiResult add(@RequestParam(value = "tagDataKeys", required = false) @NotNull(message = "标记数据唯一id集合不能为空") List<String> tagDataKeys) {
        readMarkerService.markReadList(tagDataKeys, SpringSecurityUtils.getCurrentUserId());
        return ApiResult.success();
    }

    @PostMapping(value = "/del", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除阅读记录", notes = "删除阅读记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tagDataKeys", value = "标记数据唯一id集合多个值,分割", paramType = "query", dataType = "String", required = true)})
    public ApiResult del(@RequestParam(value = "tagDataKeys", required = false) @NotNull(message = "标记数据唯一id集合不能为空") List<String> tagDataKeys) {
        readMarkerService.markNewList(tagDataKeys, SpringSecurityUtils.getCurrentUserId());
        return ApiResult.success();
    }

    @PostMapping(value = "/unReadList", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "查询未读数据", notes = "查询未读数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tagDataKeys", value = "标记数据唯一id集合多个值,分割", paramType = "query", dataType = "String", required = true)})
    public ApiResult<List<String>> unReadList(@RequestParam(value = "tagDataKeys", required = false) @NotNull(message = "标记数据唯一id集合不能为空") List<String> tagDataKeys) {
        List<String> uuidList = readMarkerService.getUnReadList(tagDataKeys, SpringSecurityUtils.getCurrentUserId());
        return ApiResult.success(uuidList);
    }

    @GetMapping(value = "/markRead/{entityUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "标记已读", notes = "标记已读")
    public ApiResult<Void> markRead(@PathVariable(value = "entityUuid") String entityUuid) {
        readMarkerService.markRead(entityUuid, SpringSecurityUtils.getCurrentUserId());
        return ApiResult.success();
    }

}
