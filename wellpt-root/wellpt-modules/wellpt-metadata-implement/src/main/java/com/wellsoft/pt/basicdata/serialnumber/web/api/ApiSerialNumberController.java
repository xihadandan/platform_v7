package com.wellsoft.pt.basicdata.serialnumber.web.api;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.basicdata.serialnumber.service.ISerialNumberOldDefService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: yt
 * @Date: 2022/5/14 12:31
 * @Description:
 */
@Api(tags = "流水号接口")
@RestController
@RequestMapping("/api/serial/number")
public class ApiSerialNumberController extends BaseController {

    @Autowired
    private ISerialNumberOldDefService serialNumberOldDefService;


    @GetMapping("/oldDataProcess")
    public ApiResult oldDataProcess(@RequestParam(name = "pageSize", required = false) Integer pageSize) {
        if (pageSize == null) {
            pageSize = 200;
        }
        serialNumberOldDefService.oldDataProcess(pageSize);
        return ApiResult.success();
    }


}
