package com.wellsoft.pt.app.workflow.web;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.workflow.dto.OpinionRuleDto;
import com.wellsoft.pt.workflow.dto.OpinionRuleIncludeItemDto;
import com.wellsoft.pt.workflow.dto.SaveOpinionRuleDto;
import com.wellsoft.pt.workflow.facade.service.OpinionRuleFacadeService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description: 流程签署意见规则接口
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/6/3.1	    zenghw		2021/6/3		    Create
 * </pre>
 * @date 2021/6/3
 */
@Api(tags = "流程签署意见规则接口")
@RestController
@RequestMapping("/api/opinion/rule")
public class FlowOpinionRuleController extends BaseController {

    @Autowired
    private OpinionRuleFacadeService opinionRuleFacadeService;

    @ApiOperation(value = "保存流程签署意见规则", notes = "保存流程签署意见规则")
    @PostMapping("/saveOpinionRule")
    @ApiImplicitParams({@ApiImplicitParam(name = "orgIds", value = "组织ID集合", required = false)})
    @ApiOperationSupport(order = 40)
    public ApiResult saveOpinionRule(
            @RequestBody SaveOpinionRuleDto saveOpinionRuleDto) {
        try {
            opinionRuleFacadeService.saveOpinionRule(saveOpinionRuleDto);
            return ApiResult.success();
        } catch (Exception e) {
            logger.error("保存流程签署意见规则 异常：", e);
            return ApiResult.fail();
        }
    }

    @ApiOperation(value = "根据主键uuid集合删除意见规则", notes = "根据主键uuid集合删除意见规则")
    @PostMapping("/delete")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuids", value = "主键uuid集合", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 50)
    public ApiResult<Boolean> delete(
            @RequestParam(name = "uuids", required = false) List<String> uuids) {
        try {
            opinionRuleFacadeService.delete(uuids);
            return ApiResult.success();
        } catch (Exception e) {
            logger.error("根据主键uuid集合删除意见规则 异常：", e);
            return ApiResult.fail();
        }
    }


    @ApiOperation(value = "获取流程签署意见规则详情", notes = "获取流程签署意见规则详情")
    @GetMapping("/getOpinionRuleDetail")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "流程签署意见规则uuid", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 10)
    public ApiResult<OpinionRuleIncludeItemDto> getOpinionRuleDetail(
            @RequestParam(name = "uuid", required = false) String uuid) {
        return ApiResult.success(opinionRuleFacadeService.getOpinionRuleDetail(uuid));
    }

    @ApiOperation(value = "判断意见规则是否被引用", notes = "判断意见规则是否被引用")
    @PostMapping("/isReferencedByOpinionRuleUuids")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "opinionRuleUuids", value = "opinionRuleUuids", paramType = "query", dataType = "String", required = true)})
    @ApiOperationSupport(order = 10)
    public ApiResult<Boolean> isReferencedByOpinionRuleUuids(
            @RequestParam(name = "opinionRuleUuids", required = true) List<String> opinionRuleUuids) {
        return ApiResult.success(opinionRuleFacadeService.isReferencedByOpinionRuleUuids(opinionRuleUuids));
    }

    @ApiOperation(value = "获取所有校验规则列表接口", notes = "获取所有校验规则列表接口:没有单位的流程，获取超管创建的规则；有单位的流程，获取超管创建的规则和对应单位的规则")
    @GetMapping("/getCurrentUserBelongOpinionRuleList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "eleIds", value = "组织元素ID集合", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 10)
    public ApiResult<List<OpinionRuleDto>> getCurrentUserBelongOpinionRuleList(
            @RequestParam(name = "flowId", required = false) String flowId) {
        List<OpinionRuleDto> list = opinionRuleFacadeService.getCurrentUserBelongOpinionRuleList(flowId);
        return ApiResult.success(list);
    }

}
