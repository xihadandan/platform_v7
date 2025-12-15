package com.wellsoft.pt.multi.org.web.controller;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.multi.org.service.MultiOrgOptionService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: 组织选择项管理接口
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/3/11.1	    zenghw		2021/3/11		    Create
 * </pre>
 * @date 2021/3/11
 */
@Api(tags = "组织选择项管理接口")
@RestController
@RequestMapping("/api/org/option")
public class MultiOrgOptionController extends BaseController {

    @Autowired
    private MultiOrgOptionService multiOrgOptionService;

    @ApiOperation(value = "获取选项样式", notes = "获取选项样式", tags = {"组织选择项", "系统单位管理-->组织选择项"})
    @GetMapping("/getOptionStyle")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "选项uuid", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 10)
    public ApiResult<String> getOptionStyle(@RequestParam(name = "id", required = false) String id) {
        return ApiResult.success(multiOrgOptionService.getOptionStyle(id));
    }
}
