package com.wellsoft.pt.security.audit.web.controller;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.security.audit.bean.ResourceBean;
import com.wellsoft.pt.security.audit.bean.ResourceDto;
import com.wellsoft.pt.security.audit.facade.service.ResourceFacadeService;
import com.wellsoft.pt.security.audit.service.ResourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description:
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/9/29   Create
 * </pre>
 */
@Api(tags = "资源维护控制")
@RestController
@RequestMapping("/api/security/resource")
public class ResourceRestfulController extends BaseController {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceFacadeService resourceFacadeService;

    @ApiOperation(value = "保存资源", notes = "保存资源")
    @PostMapping("/saveBean")
    public ApiResult<String> saveBean(@RequestBody ResourceBean bean) {
        String result = resourceService.saveBean(bean);
        return ApiResult.success(result);
    }

    @ApiOperation(value = "根基uuid获取资源", notes = "根基uuid获取资源")
    @GetMapping("/getBean/{uuid}")
    public ApiResult<ResourceBean> getBean(@PathVariable("uuid") String uuid) {
        ResourceBean result = resourceService.getBean(uuid);
        return ApiResult.success(result);
    }

    @ApiOperation(value = "根基id删除资源", notes = "根基id删除资源")
    @DeleteMapping("/remove/{id}")
    public ApiResult remove(@PathVariable("id") String id) {
        resourceService.remove(id);
        return ApiResult.success();
    }

    @ApiOperation(value = "获取菜单资源", notes = "获取菜单资源")
    @PostMapping("/getModuleMenuAsTree")
    public ApiResult<TreeNode> getModuleMenuAsTree(@ApiParam(value = "节点uuid", required = true, type = "String") @RequestParam String uuid, @ApiParam(value = "模块ID") @RequestParam String moduleId) {
        TreeNode menu = resourceFacadeService.getModuleMenuAsTree(uuid, moduleId);
        return ApiResult.success(menu);
    }

    @GetMapping("/getModuleMenuResources")
    public ApiResult<List<ResourceDto>> getModuleMenuResources(@RequestParam String moduleId) {
        return ApiResult.success(resourceService.getModuleMenuResources(moduleId));
    }


    @ApiOperation(value = "获取按钮资源", notes = "获取按钮资源")
    @PostMapping("/getResourceButtonTree")
    public ApiResult<List<TreeNode>> getResourceButtonTree(@RequestParam String treeNodeId, @RequestParam String excludeUuid) {
        List<TreeNode> buttonTree = resourceFacadeService.getResourceButtonTree(treeNodeId, excludeUuid);
        return ApiResult.success(buttonTree);
    }
}
