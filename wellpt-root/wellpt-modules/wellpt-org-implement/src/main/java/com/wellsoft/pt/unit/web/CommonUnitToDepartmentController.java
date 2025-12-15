package com.wellsoft.pt.unit.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.mt.mgr.TenantManagerService;
import com.wellsoft.pt.unit.service.CommonUnitTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Description: CommonUnitTreeController action
 *
 * @author liuzq
 * @date 2013-11-5
 */
@Controller
@RequestMapping("/superadmin/unit/commonUnitToDepartment")
public class CommonUnitToDepartmentController extends BaseController {

    @Autowired
    private TenantManagerService tenantManagerService;

    @Autowired
    private CommonUnitTreeService commonUnitTreeService;

    /**
     * 打开单位部门挂接界面
     *
     * @return
     */
    @RequestMapping(value = "/")
    public String unitDepartmentTree(Model model) {
        model.addAttribute("admintype", "superadmin");
        return forward("/superadmin/unit/unit_department_tree");
    }


    /**
     * 打开单位部门挂接界面
     *
     * @return
     */
    @RequestMapping(value = "/departmentTree")
    public String departmentTree(Model model) {
        model.addAttribute("admintype", "superadmin");
        return forward("/superadmin/unit/department_list_tree");
    }
}
