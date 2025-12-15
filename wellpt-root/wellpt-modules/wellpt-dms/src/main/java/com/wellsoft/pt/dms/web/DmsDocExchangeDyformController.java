package com.wellsoft.pt.dms.web;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.dms.dto.DmsDocExchangeDyformDto;
import com.wellsoft.pt.dms.entity.DmsDocExchangeDyformEntity;
import com.wellsoft.pt.dms.service.DmsDocExchangeDyformService;
import com.wellsoft.pt.jpa.util.BeanUtils;
import io.swagger.annotations.*;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Auther: yt
 * @Date: 2021/7/13 13:36
 * @Description:
 */
@Api(tags = "公文交换文档展示单据")
@RestController
@RequestMapping(value = "/api/dms/doc/exc/dyform", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Validated
public class DmsDocExchangeDyformController extends BaseController {

    @Autowired
    private DmsDocExchangeDyformService dmsDocExchangeDyformService;

    @GetMapping(value = "/getOne")
    @ApiOperation(value = "根据主键uuid查询数据", notes = "根据主键uuid查询数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "主键uuid", paramType = "query", dataType = "String", required = false)})
    public ApiResult<DmsDocExchangeDyformDto> getOne(@NotBlank(message = "uuid不能为空") @RequestParam(name = "uuid") String uuid) {
        DmsDocExchangeDyformEntity dyformEntity = dmsDocExchangeDyformService.getOne(uuid);
        if (dyformEntity == null) {
            throw new RuntimeException("资源不存在");
        }
        DmsDocExchangeDyformDto dyformDto = new DmsDocExchangeDyformDto();
        BeanUtils.copyProperties(dyformEntity, dyformDto);
        return ApiResult.success(dyformDto);
    }


    @PostMapping(value = "/saveOrUpdate")
    @ApiOperation(value = "保存或更新", notes = "保存或更新")
    public ApiResult<String> saveOrUpdate(@ApiParam(value = "公文交换文档展示单据") @Validated @RequestBody DmsDocExchangeDyformDto dyformDto) {
        String uuid = dmsDocExchangeDyformService.saveOrUpdate(dyformDto);
        return ApiResult.success(uuid);
    }

}
