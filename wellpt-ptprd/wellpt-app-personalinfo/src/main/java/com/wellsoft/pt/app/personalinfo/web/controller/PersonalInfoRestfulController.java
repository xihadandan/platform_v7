package com.wellsoft.pt.app.personalinfo.web.controller;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.app.personalinfo.facade.service.PersonalInfoService;
import com.wellsoft.pt.multi.org.dto.EncryptChangePasswordByIdDto;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: 用户信息管理接口
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/3/26.1	    zenghw		2021/3/26		    Create
 * </pre>
 * @date 2021/3/26
 */
@Api(tags = "用户信息管理接口")
@RestController
@RequestMapping("/api/personalinforest")
public class PersonalInfoRestfulController extends BaseController {

    @Autowired
    private PersonalInfoService personalInfoService;

    @ApiOperation(value = "修改当前用户密码-加密传输", notes = "修改当前用户密码-加密传输")
    @PostMapping("/modifyCurrentUserPasswordEncrypt")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oldPwd", value = "旧密码", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "newPwd", value = "新密码", paramType = "query", dataType = "String", required = true)})
    @ApiOperationSupport(order = 10)
    public ApiResult<EncryptChangePasswordByIdDto> modifyCurrentUserPasswordEncrypt(
            @RequestParam(name = "oldPwd", required = true) String oldPwd,
            @RequestParam(name = "newPwd", required = true) String newPwd) {
        EncryptChangePasswordByIdDto dto = personalInfoService.modifyCurrentUserPasswordEncrypt(oldPwd, newPwd);
        return ApiResult.success(dto);
    }
}
