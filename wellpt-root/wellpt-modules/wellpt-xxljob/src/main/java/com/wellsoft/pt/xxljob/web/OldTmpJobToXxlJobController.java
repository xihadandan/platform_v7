package com.wellsoft.pt.xxljob.web;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.xxljob.service.OldTmpJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Set;

/**
 * @Auther: yt
 * @Date: 2021/1/7 16:19
 * @Description:
 */
@Controller
@RequestMapping("/api/oldTmpJobToXxlJob")
public class OldTmpJobToXxlJobController extends BaseController {


    @Autowired
    private OldTmpJobService oldTmpJobService;

    @RequestMapping()
    @ResponseBody
    public ApiResult oldTmpJobToXxlJob(
            @RequestParam(name = "jobClassNameSet", required = false) Set<String> jobClassNameSet,
            @RequestParam(name = "tablePrefix", required = false) String tablePrefix) {
        oldTmpJobService.oldTmpJobToXxlJob(jobClassNameSet, tablePrefix);
        return ApiResult.success();
    }

}
