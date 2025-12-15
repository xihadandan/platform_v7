package com.wellsoft.pt.dms.web;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.dms.dto.DmsDocExchangeFieldVerDto;
import com.wellsoft.pt.dms.service.DmsDocExchangeFieldVerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: yt
 * @Date: 2021/7/22 09:54
 * @Description:
 */
@Api(tags = "公文交换-字段版本")
@RestController
@RequestMapping(value = "/api/dms/doc/exc/config", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Validated
public class DmsDocExchangeFieldVerController extends BaseController {


    @Autowired
    private DmsDocExchangeFieldVerService dmsDocExchangeFieldVerService;

    @PostMapping(value = "/recordRead")
    @ApiOperation(value = "记录已读", notes = "记录已读")
    public ApiResult<String> recordRead(@ApiParam(value = "公文交换-字段版本") @RequestBody DmsDocExchangeFieldVerDto fieldVerDto) {
        dmsDocExchangeFieldVerService.recordRead(fieldVerDto.getUserId(), fieldVerDto.getVersion());
        return ApiResult.success();
    }

    @PostMapping(value = "/isRead")
    @ApiOperation(value = "是否已读", notes = "是否已读")
    public ApiResult<Boolean> isRead(@ApiParam(value = "公文交换-字段版本") @RequestBody DmsDocExchangeFieldVerDto fieldVerDto) {
        boolean flg = dmsDocExchangeFieldVerService.isRead(fieldVerDto.getUserId(), fieldVerDto.getVersion());
        return ApiResult.success(flg);
    }
}
