/*
 * @(#)2020年12月8日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.web;

import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.basicdata.preferences.facade.service.CdUserPreferencesFacadeService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.work.bean.FlowLoadingRules;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Description: 流程分类API接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年12月8日.1	zhulh		2020年12月8日		Create
 * </pre>
 * @date 2020年12月8日
 */
@Api(tags = "流程用户偏好")
@Controller
@RequestMapping("/api/workflow/user/preferences")
public class ApiWorkflowUserPreferencesController extends BaseController {

    @Autowired
    private CdUserPreferencesFacadeService cdUserPreferencesFacadeService;

    /**
     * 根据用户ID获取的流程加载规则
     *
     * @param keyword
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getLoadingRules", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取流程加载规则", notes = "根据用户ID（不传的情况下是当前用户ID）获取的流程加载规则")
    @ApiImplicitParams({@ApiImplicitParam(name = "userId", value = "用户ID（不传的情况下是当前用户ID）", paramType = "query", dataType = "String", required = false)})
    public ApiResult<FlowLoadingRules> getLoadingRules(@RequestParam(name = "userId", required = false) String userId) {
        String currentUserId = userId;
        if (StringUtils.isBlank(currentUserId)) {
            currentUserId = SpringSecurityUtils.getCurrentUserId();
        }
        String loadingRules = cdUserPreferencesFacadeService.getDataValue(ModuleID.WORKFLOW.getValue(), "todo_list",
                currentUserId, "loading_rules");
        FlowLoadingRules flowLoadingRules = null;
        if (StringUtils.isBlank(loadingRules)) {
            flowLoadingRules = new FlowLoadingRules();
        } else {
            flowLoadingRules = JsonUtils.json2Object(loadingRules, FlowLoadingRules.class);
        }
        return ApiResult.success(flowLoadingRules);
    }

    /**
     * 保存流程加载规则
     *
     * @param userId
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/saveLoadingRules", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存流程加载规则", notes = "保存流程加载规则")
    public ApiResult<Void> saveLoadingRules(@RequestBody FlowLoadingRules flowLoadingRules) {
        cdUserPreferencesFacadeService.save(ModuleID.WORKFLOW.getValue(), "todo_list",
                SpringSecurityUtils.getCurrentUserId(), "loading_rules", JsonUtils.object2Json(flowLoadingRules),
                "流程待办列表加载规则");
        return ApiResult.success();
    }

}
