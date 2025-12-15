/*
 * @(#)2014-7-31 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datasource.web;

import com.google.common.collect.Maps;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.basicdata.datastore.entity.DbLinkConfigEntity;
import com.wellsoft.pt.basicdata.datastore.service.DbLinkConfigService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.DatabaseMetaData;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/basicdata/datasource")
public class ApiDataSourceController extends BaseController {

    @Resource
    DbLinkConfigService dbLinkConfigService;

    @PostMapping("/dblink/saveDbLinkConfig")
    public ApiResult<DbLinkConfigEntity> saveDbLinkConfig(@RequestBody DbLinkConfigEntity body) {
        return ApiResult.success(dbLinkConfigService.saveDbLinkConfig(body));
    }

    @PostMapping("/dblink/deleteDblinks")
    public ApiResult<Void> deleteDblinks(@RequestBody List<Long> uuids) {
        dbLinkConfigService.deleteByUuids(uuids);
        return ApiResult.success();
    }

    @GetMapping("/dblink/getDblinkConfig")
    public ApiResult<DbLinkConfigEntity> getDblinkConfig(@RequestParam Long uuid) {
        return ApiResult.success(dbLinkConfigService.getOne(uuid));
    }


    @GetMapping("/dblink/getAllDblinkConfig")
    public ApiResult<List<DbLinkConfigEntity>> getDblinkConfigUnderSystem(@RequestParam(required = false) String system) {
        return ApiResult.success(dbLinkConfigService.getDbLinksBySystem(StringUtils.defaultIfBlank(system, RequestSystemContextPathResolver.system())));
    }

    @PostMapping("/dblink/testConnect")
    public ApiResult<Map> testConnect(@RequestBody DbLinkConfigEntity body) {
        Map<String, Object> result = Maps.newHashMap();
        StopWatch timer = new StopWatch();
        timer.start("testConnect");
        DatabaseMetaData metaData = null;
        try {
            metaData = dbLinkConfigService.testConnection(body);
            timer.stop();
            result.put("connectSpendTime", timer.getTotalTimeMillis());
            result.put("driver", metaData.getDriverName() + " " + metaData.getDriverVersion());
            result.put("database", metaData.getDatabaseProductVersion());
        } catch (Exception e) {
            logger.error("获取数据库连接信息异常", e);
            return ApiResult.fail(e.getCause().getMessage());
        }

        return metaData != null ? ApiResult.success(result) : ApiResult.fail(null);
    }
}
