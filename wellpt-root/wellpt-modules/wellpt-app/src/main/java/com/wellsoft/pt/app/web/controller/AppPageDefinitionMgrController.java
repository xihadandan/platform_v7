package com.wellsoft.pt.app.web.controller;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.app.bean.AppPageDefinitionPathBean;
import com.wellsoft.pt.app.facade.service.AppPageDefinitionMgr;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Description: 页面定义控制层
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/3/14.1	    zenghw		2021/3/14		    Create
 * </pre>
 * @date 2021/3/14
 */
@Api(tags = "页面定义控制层")
@RestController
@RequestMapping("/api/page/definition")
public class AppPageDefinitionMgrController extends BaseController {

    @Autowired
    private AppPageDefinitionMgr appPageDefinitionMgr;

    @ApiOperation(value = "后台查询用户工作台", notes = "后台查询用户工作台")
    @GetMapping("/listPath")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userUuid", value = "用户uuid", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "source", value = "0：全部，1：用户，2：组织，3：角色，4：默认", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "keyword", value = "关键词", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 10)
    public ApiResult<List<AppPageDefinitionPathBean>> listPath(
            @RequestParam(name = "userUuid", required = false) String userUuid,
            @RequestParam(name = "source", required = false) String source,
            @RequestParam(name = "keyword", required = false) String keyword) {
        List<AppPageDefinitionPathBean> list = appPageDefinitionMgr.listPath(userUuid, source, keyword);
        return ApiResult.success(list);
    }
}
