package com.wellsoft.pt.multi.org.web.controller;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.basicdata.params.facade.SystemParams;
import com.wellsoft.pt.multi.org.dto.MultiOrgPwdSettingDto;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgPwdSettingFacadeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Description: 密码配置管理接口
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/3/25.1	    zenghw		2021/3/25		    Create
 * </pre>
 * @date 2021/3/25
 */
@Api(tags = "密码配置管理接口")
@RestController
@RequestMapping("/api/pwd/setting")
public class MultiOrgPwdSettingController extends BaseController {

    @Autowired
    private MultiOrgPwdSettingFacadeService multiOrgPwdSettingFacadeService;

    @ApiOperation(value = "保存密码配置", notes = "保存密码配置")
    @PostMapping("/saveMultiOrgPwdSetting")
    @ApiOperationSupport(order = 10)
    public ApiResult saveMultiOrgPwdSetting(@RequestBody MultiOrgPwdSettingDto vo) {
        try {
            multiOrgPwdSettingFacadeService.saveMultiOrgPwdSetting(vo);
            return ApiResult.success();
        } catch (Exception e) {
            logger.error("保存密码配置异常：", e);
            return ApiResult.fail();
        }

    }

    @ApiOperation(value = "获取密码配置", notes = "获取密码配置")
    @GetMapping("/getMultiOrgPwdSetting")
    @ApiOperationSupport(order = 20)
    public ApiResult<MultiOrgPwdSettingDto> getMultiOrgPwdSetting() {
        return ApiResult.success(multiOrgPwdSettingFacadeService.getMultiOrgPwdSetting());
    }

    @ApiOperation(value = "获取密码相关的定时操作开关(已过期)", notes = "获取密码相关的定时操作开关 定时任务：TIMED 消息队列 ：MQ 其他值：不做定时操作处理")
    @GetMapping("/getSystemParamsPwdTiming")
    @ApiOperationSupport(order = 30)
    @Deprecated
    public ApiResult<String> getGroupByUuid() {
        return ApiResult.success(SystemParams.getValue("pwd.timing", "TIMED"));
    }

    @ApiOperation(value = "密码规则配置-获取定时操作实现方式管理地址", notes = "MQ管理打开MQ的管理页面地址 任务管理打开定时任务的管理页面地址")
    @GetMapping("/getPwdSettingTimerPageUrl")
    @ApiOperationSupport(order = 40)
    public ApiResult<String> getPwdSettingTimerPageUrl() {
        MultiOrgPwdSettingDto multiOrgPwdSettingDto = multiOrgPwdSettingFacadeService.getMultiOrgPwdSetting();
        if (StringUtils.isBlank(multiOrgPwdSettingDto.getUuid())) {
            return ApiResult.success(Config.getValue("xxl.job.admin.addresses"));
        }
        return ApiResult.success(Config.getValue("pwdsetting.timing.url"));
    }

}
