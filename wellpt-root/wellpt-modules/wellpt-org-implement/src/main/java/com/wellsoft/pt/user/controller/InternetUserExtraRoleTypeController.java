package com.wellsoft.pt.user.controller;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.user.facade.service.UserInfoFacadeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 互联网用户额外角色类型controller
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年11月04日   shenhb	 Create
 * </pre>
 */
@Api(tags = {"互联网用户额外角色类型"})
@RestController
@RequestMapping("/api/user/internet")
public class InternetUserExtraRoleTypeController extends BaseController {

    @Autowired
    UserInfoFacadeService userInfoFacadeService;

    /**
     * 根据loginName，添加额外的法人角色
     *
     * @param loginName loginName
     * @return
     */
    @ApiOperation("根据loginName，添加额外的法人角色")
    @PostMapping("{loginName}/extraRoleType")
    public ApiResult addUserExtraRoleType(@PathVariable("loginName") String loginName) {
        ApiResult response = ApiResult.success();
        try {
            userInfoFacadeService.addUserExtraRoleType(loginName);
        } catch (Exception e) {
            logger.error("添加额外的角色异常：", e);
            response.setCode(-1);
            response.setMsg(e.getMessage());
        }
        return response;
    }

    /**
     * 根据loginName，删除额外的法人角色
     *
     * @param loginName loginName
     * @return
     */
    @ApiOperation("根据loginName，删除额外的法人角色")
    @DeleteMapping("{loginName}/extraRoleType")
    public ApiResult deleteUserExtraRoleType(@PathVariable("loginName") String loginName) {
        ApiResult response = ApiResult.success();
        try {
            userInfoFacadeService.deleteUserExtraRoleType(loginName);
        } catch (Exception e) {
            logger.error("删除额外的角色异常：", e);
            response.setCode(-1);
            response.setMsg(e.getMessage());
        }

        return response;
    }

}
