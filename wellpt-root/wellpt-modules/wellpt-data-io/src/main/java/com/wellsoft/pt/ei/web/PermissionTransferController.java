package com.wellsoft.pt.ei.web;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.ei.service.PermissionTransferService;
import com.wellsoft.pt.security.acl.service.AclTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: 环节权限迁移接口
 *
 * @author liuxj
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2024/12/30.1	    liuxj		2024/12/30		    Create
 * </pre>
 * @date 2024/12/30
 */
@Api(tags = "环节权限迁移接口")
@RestController
@RequestMapping("/api/permission")
public class PermissionTransferController extends BaseController {

    @Autowired
    private PermissionTransferService permissionTransferService;

    @Autowired
    private AclTaskService aclTaskService;


    @ApiOperation(value = "迁移旧权限", notes = "迁移旧权限,将AclEntry 环节实例迁移到AclTaskEntry")
    @GetMapping("/transferData")
    @ApiOperationSupport(order = 10)
    public ApiResult transferData() {
        permissionTransferService.transferData();
        return ApiResult.success();
    }


    @GetMapping("/deletePermission")
    @ApiOperationSupport(order = 10)
    public ApiResult deletePermission() {
        aclTaskService.removeDonePermission("U_167935632826433536","189069193524543488");
        return ApiResult.success();
    }


}
