package com.wellsoft.pt.basicdata.business.web;

import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.basicdata.business.service.BusinessCategoryOrgService;
import com.wellsoft.pt.basicdata.business.service.BusinessCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/basicdata/business")
public class BusinessController extends BaseController {

    @Autowired
    private BusinessCategoryService businessCategoryService;

    @Autowired
    private BusinessCategoryOrgService businessCategoryOrgService;

    /**
     * @return
     */
    @RequestMapping(value = "/category")
    public String category() {
        return forward("/basicdata/business/category");
    }

    /**
     * @param queryInfo
     * @return
     */
    @RequestMapping(value = "/category/list")
    public @ResponseBody
    JqGridQueryData listAsJson(JqGridQueryInfo queryInfo) {
        return businessCategoryService.query(queryInfo);
    }

    /**
     * @return
     */
    @RequestMapping(value = "/book")
    public String book() {
        return forward("/basicdata/business/book");
    }

    /**
     * @return
     */
    @RequestMapping(value = "/manage")
    public String manage() {
        return forward("/basicdata/business/manage");
    }

    /**
     * @param queryInfo
     * @return
     */
    @RequestMapping(value = "/manage/list")
    public @ResponseBody
    JqGridQueryData listByManage(JqGridQueryInfo queryInfo, HttpServletRequest request) {
        return businessCategoryOrgService.queryByManage(queryInfo, request.getParameter("value"));
    }


    /**
     * @return
     */
    @RequestMapping(value = "/application")
    public String application() {
        return forward("/basicdata/business/application");
    }


    /**
     * @param queryInfo
     * @return
     */
    @RequestMapping(value = "/category/listByApplication")
    public @ResponseBody
    JqGridQueryData listByApplication(JqGridQueryInfo queryInfo, HttpServletRequest request) {
        return businessCategoryService.queryByApplication(queryInfo, request.getParameter("value"));
    }
}
