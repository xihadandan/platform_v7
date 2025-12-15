package com.wellsoft.pt.basicdata.params.web;

import com.wellsoft.context.web.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/pi/paramItems")
public class ParamItemsController extends BaseController {

    @RequestMapping("")
    public String paramItems() {
        return forward("/param/sys_param_item");
    }

}
