/*
 * @(#)2019年9月21日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.cache.web;

import com.wellsoft.context.util.web.ServletUtils;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.cache.FrontCacheUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年9月21日.1	zhongzh		2019年9月21日		Create
 * </pre>
 * @date 2019年9月21日
 */
@Controller
@RequestMapping("/frontcache")
public class FrontCacheController extends BaseController {

    @ResponseBody
    @RequestMapping(value = "/evictall")
    public void evictAll(HttpServletRequest request, HttpServletResponse response) {
        FrontCacheUtils.evictAll();
        FrontCacheUtils.overrideCookie(request, response);
    }

    @ResponseBody
    @RequestMapping(value = "/evict")
    public Long evict(@RequestParam(value = "module", required = false) String module, HttpServletRequest request,
                      HttpServletResponse response) {
        Long timestamp = FrontCacheUtils.evict(module);
        FrontCacheUtils.overrideCookie(request, response);
        return timestamp;
    }

    @ResponseBody
    @RequestMapping(value = "/get")
    public Long get(@RequestParam(value = "module", required = false) String module, HttpServletRequest request,
                    HttpServletResponse response) {
        // 不启用缓存
        ServletUtils.setDisableCacheHeader(response);
        // 更新（覆盖）前端缓存
        FrontCacheUtils.overrideCookie(request, response);
        return FrontCacheUtils.get(module);
    }
}
