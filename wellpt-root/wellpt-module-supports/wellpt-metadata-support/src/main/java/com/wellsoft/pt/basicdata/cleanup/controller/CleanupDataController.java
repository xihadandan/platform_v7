package com.wellsoft.pt.basicdata.cleanup.controller;

import com.wellsoft.context.util.web.ServletUtils;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.pt.basicdata.cleanup.DataCleanupProvider;
import com.wellsoft.pt.basicdata.cleanup.service.DataCleanupService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年04月23日   chenq	 Create
 * </pre>
 */
@Api(tags = "清理数据")
@RestController
@RequestMapping("/api/cleanupData")
public class CleanupDataController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DataCleanupService dataCleanupService;


    @ApiOperation(value = "查询指定类型的数据清理器数据", notes = "查询指定类型的数据清理器数据")
    @PostMapping("/expectCleanupRows/{type}")
    public ApiResult<DataCleanupProvider.ExpectCleanupResult> expectCleanupRows(@PathVariable("type") String type,
                                                                                @RequestBody DataCleanupProvider.Params params,
                                                                                HttpServletResponse response) {
        return ApiResult.success(dataCleanupService.expectCleanupRows(type, params));

    }

    @ApiOperation(value = "删除指定类型的数据清理器的数据", notes = "删除指定类型的数据清理器的数据")
    @PostMapping("/cleanup/{type}")
    public ApiResult<DataCleanupProvider.ExpectCleanupResult> cleanup(@PathVariable("type") String type,
                                                                      @RequestBody DataCleanupProvider.Params params,
                                                                      HttpServletRequest request,
                                                                      HttpServletResponse response) {
        logger.info("用户: {} , ip: {} , 操作清理数据: {} , {} ", new Object[]{SpringSecurityUtils.getCurrentUserName(), ServletUtils.getRemoteAddr(request), type, params});
        return ApiResult.success(dataCleanupService.cleanup(type, params));
    }
}
