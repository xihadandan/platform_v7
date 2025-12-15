package com.wellsoft.pt.api.web;

import com.wellsoft.context.web.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Description: api日志
 *
 * @author chenq
 * @date 2018/8/13
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/8/13    chenq		2018/8/13		Create
 * </pre>
 */
@Controller
@RequestMapping("/apiLog")
public class ApiLogController extends BaseController {

    @RequestMapping("/list")
    public String list() {
        return forward("/api/api_access_log_list");
    }


   /* @Deprecated
    @RequestMapping(value = "/accessLog")
    public String apiAccessLogList() {
        return forward("/api/api_access_log_list");
    }*/
}
