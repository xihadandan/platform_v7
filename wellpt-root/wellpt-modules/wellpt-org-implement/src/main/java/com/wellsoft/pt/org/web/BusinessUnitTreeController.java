package com.wellsoft.pt.org.web;

import com.wellsoft.context.web.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/org/business")
public class BusinessUnitTreeController extends BaseController {

    /**
     * 打开单位通讯录界面
     *
     * @return
     */
    @RequestMapping(value = "/unit/business_type")
    public String businessType(Model model) {
        return forward("/org/business_type");
    }

    /**
     * 打开单位通讯录界面
     *
     * @return
     */
    @RequestMapping(value = "/unit/business_unit_tree")
    public String businessUnitTree(Model model) {
        return forward("/org/business_unit_tree");
    }

    /**
     * 打开业务管理界面
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/unit/business_manage")
    public String businessManage(Model model) {
        return forward("/org/business_manage");
    }

}
