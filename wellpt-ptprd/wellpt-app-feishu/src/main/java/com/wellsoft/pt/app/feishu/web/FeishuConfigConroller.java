package com.wellsoft.pt.app.feishu.web;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.app.feishu.facade.service.FeishuOrgSyncFacadeService;
import com.wellsoft.pt.app.feishu.model.DepartmentNode;
import com.wellsoft.pt.app.feishu.service.FeishuConfigService;
import com.wellsoft.pt.app.feishu.support.FeishuSyncLoggerHolder;
import com.wellsoft.pt.app.feishu.utils.FeishuApiUtils;
import com.wellsoft.pt.app.feishu.vo.FeishuConfigVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "飞书配置")
@RestController
@RequestMapping("/api/feishu/config")
public class FeishuConfigConroller extends BaseController {

    @Autowired
    private FeishuConfigService feishuConfigService;

    @Autowired
    private FeishuOrgSyncFacadeService feishuOrgSyncFacadeService;

    @ApiOperation(value = "查询当前租户系统飞书配置", notes = "查询当前租户系统飞书配置")
    @GetMapping("/query")
    public ApiResult<FeishuConfigVo> query() {
        FeishuConfigVo feishuConfigVo = feishuConfigService.query();
        return ApiResult.success(feishuConfigVo);
    }

    @ApiOperation(value = "获取已启用的应用ID", notes = "获取已启用的应用ID")
    @GetMapping("/getEnabledAppId")
    public ApiResult<String> getEnabledAppId() {
        FeishuConfigVo feishuConfigVo = feishuConfigService.query();
        return ApiResult.success(feishuConfigVo.getEnabled() ? feishuConfigVo.getAppId() : null);
    }

    @ApiOperation(value = "保存当前租户系统飞书配置", notes = "保存当前租户系统飞书配置")
    @PostMapping("/save")
    public ApiResult<Long> save(@ApiParam(value = "飞书配置信息", required = true) @Valid @RequestBody FeishuConfigVo feishuConfigVo) {
        return ApiResult.success(feishuConfigService.save(feishuConfigVo));
    }

    @ApiOperation(value = "测试生成访问凭证", notes = "测试生成访问凭证")
    @GetMapping("/testCreateToken")
    public ApiResult testCreateToken(@RequestParam(name = "appId") String appId, @RequestParam(name = "appSecret") String appSecret,
                                     @RequestParam(name = "baseUrl", defaultValue = "https://open.feishu.cn") String baseUrl) {
        feishuConfigService.testCreateToken(appId, appSecret, baseUrl);
        return ApiResult.success();
    }

    @ApiOperation(value = "组织同步", notes = "组织同步")
    @PostMapping("/syncOrg")
    public ApiResult<Boolean> syncOrg(@RequestBody FeishuConfigVo feishuConfigVo) {
        boolean result = true;
        try {
            feishuConfigService.save(feishuConfigVo);
            FeishuSyncLoggerHolder.create(feishuConfigVo);
            List<DepartmentNode> departmentNodes = FeishuApiUtils.departments(feishuConfigVo.getAppId(), feishuConfigVo.getAppSecret(), feishuConfigVo.getServiceUri());
            // FeishuApiUtils.printDepartmentTree(departmentNodes, 1);
            feishuOrgSyncFacadeService.syncOrg(departmentNodes, feishuConfigVo);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            FeishuSyncLoggerHolder.error(e.getMessage());
            result = false;
        } finally {
            if (BooleanUtils.isNotTrue(FeishuSyncLoggerHolder.commit())) {
                result = false;
            }
        }
        return ApiResult.success(result);
    }

}
