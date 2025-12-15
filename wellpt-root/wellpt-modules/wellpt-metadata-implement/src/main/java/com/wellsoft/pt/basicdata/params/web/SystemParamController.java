/*
 * @(#)2015年9月22日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.params.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.app.facade.service.AppFacadeService;
import com.wellsoft.pt.basicdata.params.facade.ModuleConfig;
import com.wellsoft.pt.basicdata.params.facade.SysParamItemConfigMgr;
import com.wellsoft.pt.basicdata.params.facade.SystemParams;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年9月22日.1	zhulh		2015年9月22日		Create
 * </pre>
 * @date 2015年9月22日
 */
@Api(tags = "系统参数")
@Controller
@RestController
@RequestMapping(path = {"/basicdata/system/param", "/api/basicdata/system/param"})
public class SystemParamController extends BaseController {

    @Autowired
    private SysParamItemConfigMgr sysParamItemConfigMgr;


    @Autowired
    AppFacadeService appFacadeService;

    /**
     * @param key
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ApiOperation(value = "获取系统参数", notes = "根据key获取系统参数")
    public @ResponseBody
    ResultMessage get(@RequestParam(value = "key") String key) {
        String value = null;
        if (StringUtils.isNotBlank(RequestSystemContextPathResolver.system())) {
            value = appFacadeService.getAppSystemParam(key, RequestSystemContextPathResolver.system());
        }
        return new ResultMessage(null, true, value == null ? SystemParams.getValue(key) : value);
    }

    /**
     * @param keyword
     * @return
     */
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    @ApiOperation(value = "查询系统参数", notes = "根据keyword查询系统参数")
    public @ResponseBody
    ResultMessage query(@RequestParam(value = "keyword", required = false) String keyword) {
        return new ResultMessage(null, true, sysParamItemConfigMgr.query(keyword, null));
    }

    /**
     * @param module
     * @param key
     * @return
     */
    @ApiOperation(value = "获取模块参数")
    @RequestMapping(value = "/getModuleValue", method = RequestMethod.GET)
    public @ResponseBody
    ResultMessage getModuleValue(@RequestParam(value = "module") String module,
                                 @RequestParam(value = "key") String key) {
        return new ResultMessage(null, true, ModuleConfig.getModuleValue(module, key));
    }

    @ApiOperation(value = "获取模块配置")
    @RequestMapping(value = "/getModuleConfig", method = RequestMethod.GET)
    public @ResponseBody
    ResultMessage getModuleConfig(@RequestParam(value = "module") String module) {
        return new ResultMessage(null, true, ModuleConfig.getModuleConfig(module));
    }

    @ApiOperation(value = "保存模块配置")
    @RequestMapping(value = "/saveModuleConfig", method = RequestMethod.POST)
    public @ResponseBody
    ResultMessage saveModuleConfig(@RequestParam(value = "module") String module,
                                   @RequestParam(value = "jsonParams") String jsonParams) {
        JSONObject params = JSONObject.fromObject(jsonParams);
        return new ResultMessage(null, true, ModuleConfig.saveModuleConfig(module, params));
    }
}
