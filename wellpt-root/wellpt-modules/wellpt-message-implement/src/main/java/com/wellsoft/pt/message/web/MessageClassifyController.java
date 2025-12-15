package com.wellsoft.pt.message.web;


import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.pt.message.dto.MessageClassifyDto;
import com.wellsoft.pt.message.entity.MessageClassify;
import com.wellsoft.pt.message.service.MessageClassifyService;
import io.swagger.annotations.*;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 消息分类contoller
 *
 * @author shenhb
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 20-10-22.1	shenhb		20-10-22		Create
 * </pre>
 * @date 20-10-22
 */
@Api(tags = "消息分类")
@RestController
@RequestMapping(value = "/api/message/classify", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Validated
public class MessageClassifyController {

    @Resource
    private MessageClassifyService messageClassifyService;

    @GetMapping(value = "/getOne")
    @ApiOperation(value = "根据主键uuid查询数据", notes = "根据主键uuid查询数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "主键uuid", paramType = "query", dataType = "String", required = false)})
    public ApiResult<MessageClassify> getOne(@RequestParam(name = "uuid") String uuid) {
        MessageClassify messageClassify = messageClassifyService.getOne(uuid);
        return ApiResult.success(messageClassify);
    }


    @GetMapping(value = "/queryList")
    @ApiOperation(value = "流程分类查询", notes = "流程分类查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "名称", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "systemUnitId", value = "系统单位Id", paramType = "query", dataType = "String", required = false)})
    public ApiResult<List<MessageClassify>> queryList(@RequestParam(name = "name", required = false) String name,
                                                      @RequestParam(name = "systemUnitId", required = false) String systemUnitId) {
        List<MessageClassify> messageClassifyList = messageClassifyService.queryList(name, systemUnitId);
        return ApiResult.success(messageClassifyList);
    }

    @GetMapping(value = "/facadeQueryList")
    @ApiOperation(value = "前端查询分类", notes = "前端查询分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "名称", paramType = "query", dataType = "String", required = false)})
    public ApiResult<List<MessageClassifyDto>> facadeQueryList(@RequestParam(name = "name", required = false) String name) {
        List<MessageClassifyDto> messageClassifyDtoList = messageClassifyService.facadeQueryList(name);
        return ApiResult.success(messageClassifyDtoList);
    }

    @PostMapping(value = "/saveOrupdateClassify")
    @ApiOperation(value = "保存或更新", notes = "保存或更新")
    public ApiResult saveOrupdateClassify(@RequestBody MessageClassify classify) {
        messageClassifyService.saveOrupdateClassify(classify);
        return ApiResult.success();
    }


    @PostMapping(value = "/delClassifys")
    @ApiOperation(value = "删除", notes = "删除")
    public ApiResult delClassifys(@ApiParam(value = "消息分类uuids") @RequestBody List<String> uuids) {
        messageClassifyService.delClassifys(uuids);
        return ApiResult.success();
    }


}
