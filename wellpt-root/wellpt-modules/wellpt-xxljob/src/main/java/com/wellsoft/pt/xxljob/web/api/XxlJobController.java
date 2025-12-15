package com.wellsoft.pt.xxljob.web.api;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.xxljob.dto.AddTmpStartJobDto;
import com.wellsoft.pt.xxljob.model.ExecutionParam;
import com.wellsoft.pt.xxljob.service.XxlJobService;
import com.xxl.job.core.well.model.TmpJobParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * Description: 任务管理接口
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/10/20.1	    zenghw		2021/10/20		    Create
 * </pre>
 * @date 2021/10/20
 */
@Api(tags = "任务管理接口")
@RestController
@RequestMapping("/api/xxlJob/")
public class XxlJobController extends BaseController {

    @Autowired
    private XxlJobService xxlJobService;

    @ApiOperation(value = "添加并启动临时任务", notes = "添加并启动临时任务 \n 返回 List<String> :xxlJobId集合")
    @PostMapping("/addTmpStart")
    public ApiResult<List<String>> addTmpStart(@RequestBody AddTmpStartJobDto addTmpStartJobDto) {

        // xxlJob执行需要的参数
        ExecutionParam executionParam = null;
        if (StringUtils.isNotBlank(addTmpStartJobDto.getBusinessKey())) {
            executionParam = new ExecutionParam().setTenantId(SpringSecurityUtils.getCurrentTenantId())
                    .setUserId(addTmpStartJobDto.getUserId())
                    .putKeyVal(addTmpStartJobDto.getBusinessKey(), addTmpStartJobDto.getBusinessValue());
        } else {
            executionParam = new ExecutionParam().setTenantId(SpringSecurityUtils.getCurrentTenantId())
                    .setUserId(addTmpStartJobDto.getUserId());
        }
        String param = executionParam.toJson();
        // xxlJob定义
        TmpJobParam.Builder builder = TmpJobParam.toBuilder().setJobDesc(addTmpStartJobDto.getJobDesc())
                .setExecutorHandler(addTmpStartJobDto.getTempJobName());
        // xxlJob执行时间+参数
        if (addTmpStartJobDto.getWorkingTimeList() == null || addTmpStartJobDto.getWorkingTimeList().size() == 0) {
            return ApiResult.fail("WorkingTimeList参数不能为空");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (String dateStr : addTmpStartJobDto.getWorkingTimeList()) {
            try {
                LocalDateTime localDateTime = LocalDateTime.parse(dateStr, formatter);
                Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
                builder.addExecutionTimeParams(date, param);
            } catch (Exception e) {
                return ApiResult.fail("日期格式不对，请检查");
            }
        }
        List<String> xxlJobIdList = xxlJobService.addTmpStart(builder.build());
        return ApiResult.success(xxlJobIdList);
    }

    @ApiOperation(value = "根据JobIdList 批量删除任务", notes = "根据JobIdList 批量删除任务")
    @PostMapping("/remove")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobIdList", value = "任务Id集合", paramType = "query", dataType = "Array", required = true)})
    public ApiResult remove(@RequestParam(name = "jobIdList", required = true) List<String> jobIdList) {
        try {
            xxlJobService.remove(jobIdList);
            return ApiResult.success();
        } catch (Exception e) {
            return ApiResult.fail(e.getMessage());
        }
    }
}
