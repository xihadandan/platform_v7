package com.wellsoft.pt.multi.org.web.controller;

import com.wellsoft.context.component.tree.OrgNode;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.multi.org.bean.UserNodePy;
import com.wellsoft.pt.multi.org.dto.MultiOrgTreeDialogAsynDto;
import com.wellsoft.pt.multi.org.dto.MultiOrgTreeDialogDto;
import com.wellsoft.pt.multi.org.dto.SmartNameDto;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgTreeDialogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Description: 组织弹出框管理接口
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/3/8.1	    zenghw		2021/3/8		    Create
 * </pre>
 * @date 2021/3/8
 */
@Api(tags = "组织弹出框管理接口")
@RestController
@RequestMapping("/api/org/tree/dialog")
public class MultiOrgTreeDialogController extends BaseController {

    @Autowired
    private MultiOrgTreeDialogService multiOrgTreeDialogService;

    @ApiOperation(value = "按类型获取组织弹出框的数据", notes = "按类型获取组织弹出框的数据，供给 MultiUnitTreeDialog.js里面使用")
    @PostMapping("/queryUnitTreeDialogDataByType")
    public ApiResult<List<TreeNode>> queryUnitTreeDialogDataByType(@RequestBody MultiOrgTreeDialogDto params) {
        List<TreeNode> list = multiOrgTreeDialogService.queryUnitTreeDialogDataByType(params.getType(),
                params.getParams());
        return ApiResult.success(list);
    }

    @ApiOperation(value = "异步加载 子节点", notes = "异步加载 子节点")
    @PostMapping("/children")
    public ApiResult<List<OrgNode>> children(@RequestBody MultiOrgTreeDialogAsynDto params) {
        List<OrgNode> list = multiOrgTreeDialogService.children(params.getType(), params.getParams());
        return ApiResult.success(list);
    }

    @ApiOperation(value = "搜索", notes = "搜索")
    @PostMapping("/search")
    public ApiResult<List<OrgNode>> search(@RequestBody MultiOrgTreeDialogAsynDto params) {
        List<OrgNode> list = multiOrgTreeDialogService.search(params.getType(), params.getParams());
        return ApiResult.success(list);
    }

    @ApiOperation(value = "全部加载", notes = "全部加载")
    @PostMapping("/full")
    public ApiResult<List<OrgNode>> full(@RequestBody MultiOrgTreeDialogAsynDto params) {
        List<OrgNode> list = multiOrgTreeDialogService.full(params.getType(), params.getParams());
        return ApiResult.success(list);
    }

    @ApiOperation(value = "所有用户", notes = "所有用户")
    @PostMapping("/allUserSearch")
    public ApiResult<UserNodePy> allUserSearch(@RequestBody MultiOrgTreeDialogAsynDto params) {
        return ApiResult.success(multiOrgTreeDialogService.allUserSearch(params.getType(), params.getParams()));
    }

    @PostMapping(value = "/smartName")
    @ApiOperation(value = "智能名称", notes = "智能名称")
    public ApiResult<Map<String, OrgNode>> smartName(@RequestBody SmartNameDto smartNameDto) {
        Map<String, OrgNode> map = multiOrgTreeDialogService.smartName(smartNameDto.getNameDisplayMethod(),
                smartNameDto.getNodeIds(), smartNameDto.getNodeNames());
        return ApiResult.success(map);
    }

    @ApiOperation(value = "全名称路径", notes = "全名称路径")
    @PostMapping("/fullNamePath")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgVersionId", value = "组织版本Id", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "eleId", value = "节点id", paramType = "query", dataType = "String", required = true)})
    public ApiResult<String> fullNamePath(@RequestParam(value = "orgVersionId", required = false) String orgVersionId,
                                          @RequestParam("eleId") String eleId) {
        String fullNamePath = multiOrgTreeDialogService.fullNamePath(orgVersionId, eleId);
        return ApiResult.success(fullNamePath);
    }

}
