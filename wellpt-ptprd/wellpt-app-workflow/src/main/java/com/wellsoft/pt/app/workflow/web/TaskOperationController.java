package com.wellsoft.pt.app.workflow.web;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.app.workflow.dto.TaskOperationDto;
import com.wellsoft.pt.bpm.engine.entity.TaskOperation;
import com.wellsoft.pt.bpm.engine.service.TaskOperationService;
import com.wellsoft.pt.jpa.util.BeanUtils;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Description: 环节操作接口
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/6/16.1	    zenghw		2021/6/16		    Create
 * </pre>
 * @date 2021/6/16
 */
@Api(tags = "环节操作接口")
@RestController
@RequestMapping("/api/task/operation")
public class TaskOperationController extends BaseController {

    @Autowired
    private TaskOperationService taskOperationService;

    @ApiOperation(value = "获取指定流程实例撤回后之前的环节操作", notes = "获取指定流程实例撤回后之前的环节操作")
    @GetMapping("/getLastestCancelAfterByFlowInstUuid")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flowInstUuid", value = "指定流程实例uuid", paramType = "query", dataType = "String", required = true)})
    @ApiOperationSupport(order = 10)
    public ApiResult<TaskOperationDto> getLastestCancelAfterByFlowInstUuid(
            @RequestParam(name = "flowInstUuid", required = true) String flowInstUuid) {
        TaskOperation taskOperation = taskOperationService.getLastestCancelAfterByFlowInstUuid(flowInstUuid);
        if (taskOperation == null) {
            return ApiResult.success(null);
        }
        TaskOperationDto dto = new TaskOperationDto();
        BeanUtils.copyProperties(taskOperation, dto);
        return ApiResult.success(dto);
    }

    @ApiOperation(value = "获取指定流程实例撤回后之前的环节操作", notes = "获取指定流程实例撤回后之前的环节操作")
    @GetMapping("/getLastestCancelAfterByFlowInstUuid/{flowInstUuid}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flowInstUuid", value = "指定流程实例uuid", paramType = "query", dataType = "String", required = true)})
    @ApiOperationSupport(order = 10)
    public ApiResult<TaskOperationDto> getLastestCancelAfterByFlowInstUuidPathVariable(
            @PathVariable(name = "flowInstUuid", required = true) String flowInstUuid) {
        TaskOperation taskOperation = taskOperationService.getLastestCancelAfterByFlowInstUuid(flowInstUuid);
        if (taskOperation == null) {
            return ApiResult.success(null);
        }
        TaskOperationDto dto = new TaskOperationDto();
        BeanUtils.copyProperties(taskOperation, dto);
        return ApiResult.success(dto);
    }

}
