package com.wellsoft.pt.handover.controller;

import cn.hutool.core.date.DateUtil;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.HandoverUtils;
import com.wellsoft.pt.handover.dto.*;
import com.wellsoft.pt.handover.entity.WhWorkHandoverEntity;
import com.wellsoft.pt.handover.entity.WhWorkSettingsEntity;
import com.wellsoft.pt.handover.enums.HandoverworktimeSettingEnum;
import com.wellsoft.pt.handover.enums.WorkHandoverStatusEnum;
import com.wellsoft.pt.handover.service.WhFlowDatasRecordService;
import com.wellsoft.pt.handover.service.WhWorkHandoverService;
import com.wellsoft.pt.handover.service.WhWorkSettingsService;
import com.wellsoft.pt.handover.service.WhWorkTypeToHandoverService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Description: 工作交接接口
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2022/3/28	    zenghw		2022/3/28		    Create
 * </pre>
 * @date 2022/3/28
 */
@Api(tags = "工作交接接口")
@RestController
@RequestMapping("/api/wh/handover")
public class WorkHandoverController extends BaseController {

    @Autowired
    private WhWorkHandoverService whWorkHandoverService;
    @Autowired
    private WhWorkSettingsService whWorkSettingsService;
    @Autowired
    private WhWorkTypeToHandoverService whWorkTypeToHandoverService;
    @Autowired
    private WhFlowDatasRecordService whFlowDatasRecordService;

    @ApiOperation(value = "通过UUID 获取工作交接基本信息", notes = "通过UUID 获取工作交接基本信息")
    @GetMapping("/getWorkHandoverByUuid")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "handoverUuid", value = "工作交接uuid", paramType = "query", dataType = "String", required = true)})
    @ApiOperationSupport(order = 10)
    public ApiResult<GetWorkHandoverByUuidDto> getWorkHandoverByUuid(
            @RequestParam(name = "handoverUuid", required = true) String handoverUuid) {

        try {
            GetWorkHandoverByUuidDto workHandoverByUuidDto = whWorkHandoverService.getWorkHandoverByUuid(handoverUuid);
            return ApiResult.success(workHandoverByUuidDto);
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                return ApiResult.fail(e.getMessage());
            }
            logger.error("获取工作交接基本信息异常", e);
            return ApiResult.fail("网络异常");
        }
    }

    @ApiOperation(value = "删除工作交接", notes = "删除工作交接")
    @PostMapping("/deleteWorkHandover")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "handoverUuid", value = "工作交接uuid", paramType = "query", dataType = "String", required = true)})
    public ApiResult deleteWorkHandover(@RequestParam(name = "handoverUuid", required = true) String handoverUuid) {
        WhWorkHandoverEntity entity = whWorkHandoverService.getOne(handoverUuid);
        if (entity == null) {
            return ApiResult.fail("找不到数据，请检查handoverUuid参数");
        } else if (WorkHandoverStatusEnum.Execution.getValue().equals(entity.getWorkHandoverStatus())
                || WorkHandoverStatusEnum.Completed.getValue().equals(entity.getWorkHandoverStatus())) {
            return ApiResult.fail("执行中和已完成的不可删除");
        }
        whWorkHandoverService.deleteWorkHandover(handoverUuid);
        return ApiResult.success("删除成功");
    }

    @ApiOperation(value = "保存任务执行设置", notes = "保存任务执行设置")
    @PostMapping("/saveWorkSettings")
    public ApiResult saveWorkSettings(@RequestBody WorkSettingsDto saveWorkSettingsDto) {
        if (saveWorkSettingsDto == null || StringUtils.isBlank(saveWorkSettingsDto.getWorkTime())) {
            return ApiResult.fail("默认执行时间值不能为空");
        }
        if (saveWorkSettingsDto.getWorkTime().split(":").length != 2) {
            return ApiResult.fail("默认执行时间值格式不对，格式参考：01:00");
        }
        WhWorkSettingsEntity whWorkSettingsEntity = whWorkSettingsService
                .getDetailByCurrentUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        if (whWorkSettingsEntity == null) {
            whWorkSettingsEntity = new WhWorkSettingsEntity();
        }
        whWorkSettingsEntity.setWorkTime(saveWorkSettingsDto.getWorkTime());
        whWorkSettingsEntity.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        whWorkSettingsService.save(whWorkSettingsEntity);
        return ApiResult.success("保存成功");
    }

    @ApiOperation(value = "获取当前单位的任务执行设置", notes = "获取当前单位的任务执行设置")
    @GetMapping("/getWorkSettings")
    public ApiResult<WorkSettingsDto> getWorkSettings() {
        WhWorkSettingsEntity workSettingsEntity = whWorkSettingsService
                .getDetailByCurrentUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        WorkSettingsDto settingsDto = new WorkSettingsDto();
        if (workSettingsEntity == null) {
            settingsDto.setWorkTime("01:00");
        } else {
            settingsDto.setWorkTime(workSettingsEntity.getWorkTime());
        }

        return ApiResult.success(settingsDto);
    }

    @ApiOperation(value = "校验交接人是否存在尚未完成的委托", notes = "校验交接人是否存在尚未完成的委托 存在返回1，不存在返回0")
    @GetMapping("/checkWorkFlowTaskDelegation")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "handoverPersonId", value = "交接人ID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Integer> checkWorkFlowTaskDelegation(
            @RequestParam(name = "handoverPersonId", required = true) String handoverPersonId) {

        return ApiResult.success(whWorkHandoverService.checkWorkFlowTaskDelegation(handoverPersonId));
    }

    @ApiOperation(value = "工作交接未创建时查询交接结果接口", notes = "工作交接未创建时查询交接结果接口")
    @PostMapping("/getFlowDatasRecords")
    public ApiResult<List<WhWorkTypeToHandoverCountDto>> getFlowDatasRecords(
            @RequestBody GetFlowDatasRecordsDto getFlowDatasRecordsDto) {
        List<WhWorkTypeToHandoverCountDto> countDtoList = whWorkHandoverService
                .getFlowDatasRecords(getFlowDatasRecordsDto);
        return ApiResult.success(countDtoList);
    }

    @ApiOperation(value = "创建工作交接接口", notes = "创建工作交接接口")
    @PostMapping("/saveWorkHandover")
    public ApiResult saveWorkHandover(@RequestBody SaveWhWorkHandoverDto saveWhWorkHandoverDto) {
        try {
            // 校验数据
            checkSaveWorkHandoverData(saveWhWorkHandoverDto);

            if (HandoverworktimeSettingEnum.Free.getValue()
                    .equals(saveWhWorkHandoverDto.getHandoverWorkTimeSetting())) {

                whWorkHandoverService.saveWorkHandoverFree(saveWhWorkHandoverDto);
                WhWorkSettingsEntity workSettingsEntity = whWorkSettingsService
                        .getDetailByCurrentUnitId(SpringSecurityUtils.getCurrentUserUnitId());
                String workTime = "";
                if (workSettingsEntity == null) {
                    workTime = "01:00";
                } else {
                    workTime = workSettingsEntity.getWorkTime();
                }
                Date workDateTime = HandoverUtils.getWorkDateTime(workTime);
                int year = DateUtil.year(new Date());
                String workDateTimeStr = DateUtil.format(workDateTime, "yyyy-MM-dd HH:mm");
                workDateTimeStr = workDateTimeStr.replace(String.valueOf(year) + "-", "");

                return ApiResult.success("交接内容将在" + workDateTimeStr + "移交给接收人");
            } else {
                whWorkHandoverService.saveWorkHandoverNow(saveWhWorkHandoverDto);
                return ApiResult.success("交接内容将立即移交给接收人");
            }
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                return ApiResult.fail(e.getMessage());
            }
            logger.error("工作交接创建异常", e);
            return ApiResult.fail("网络异常");
        }

    }

    /**
     * 校验数据
     *
     * @param saveWhWorkHandoverDto
     **/
    private void checkSaveWorkHandoverData(SaveWhWorkHandoverDto saveWhWorkHandoverDto) {

        if (StringUtils.isBlank(saveWhWorkHandoverDto.getHandoverPersonId())) {
            throw new BusinessException("交接人不能为空！");
        }
        if (StringUtils.isBlank(saveWhWorkHandoverDto.getReceiverId())) {
            throw new BusinessException("接收人不能为空！");
        }
        if (StringUtils.isBlank(saveWhWorkHandoverDto.getHandoverWorkType())) {
            throw new BusinessException("工作类型不能为空！");
        }
        if (saveWhWorkHandoverDto.getWhWorkTypeToHandoverCountItemDtos().size() == 0) {
            throw new BusinessException("交接内容不能为空！");
        }
        if (StringUtils.isBlank(saveWhWorkHandoverDto.getHandoverContentsId())) {
            throw new BusinessException("交接内容（ 流程分类和流程定义）不能为空！");
        }
    }
}
