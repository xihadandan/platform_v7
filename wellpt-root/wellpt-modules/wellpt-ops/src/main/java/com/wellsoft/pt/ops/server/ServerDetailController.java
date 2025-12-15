package com.wellsoft.pt.ops.server;

import com.wellsoft.context.web.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/7/31
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/7/31    chenq		2019/7/31		Create
 * </pre>
 */
@Controller
@RequestMapping("/ops/server")
public class ServerDetailController extends BaseController {

    @RequestMapping("/list")
    public String list() {
        return forward("/ops/server/ops_server_details");
    }

}
