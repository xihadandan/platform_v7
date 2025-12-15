package com.wellsoft.pt.workflow.web;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.workflow.service.FlowEmergencyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: 流程应急处理接口
 *
 * @author zenghw  emergency
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/8/19.1	    zenghw		2021/8/19		    Create
 * </pre>
 * @date 2021/8/19
 */
@Api(tags = "流程应急处理接口")
@RestController
@RequestMapping("/api/workflow/emergency")
public class FlowEmergencyController extends BaseController {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FlowEmergencyService flowEmergencyService;

    @ApiOperation(value = "已办权限丢失找回接口", notes = "指定时间区间内，已办权限丢失找回接口——应急处理 只需要调用一次")
    @GetMapping("/addDonePermission")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startDateTime", value = "开始时间，不能为空，格式：2022-01-21", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "endDateTime", value = "结束时间，不能为空,格式：2022-01-28", paramType = "query", dataType = "String", required = true)})
    public ApiResult addDonePermission(@RequestParam(name = "startDateTime", required = true) String startDateTime,
                                       @RequestParam(name = "endDateTime", required = true) String endDateTime) {

        try {
            flowEmergencyService.addDonePermission(startDateTime, endDateTime);
            return ApiResult.success("处理成功");
        } catch (Exception e) {
            logger.error("已办权限丢失找回接口 异常：", e);
            return ApiResult.fail("处理异常，请联系管理员");
        }

    }
}
