/*
 * @(#)2012-10-23 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.workflow.bean.FlowCategoryBean;
import com.wellsoft.pt.workflow.service.FlowCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

/**
 * Description: 流程分类的控制器类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-23.1	zhulh		2012-10-23		Create
 * </pre>
 * @date 2012-10-23
 */
@Controller
@RequestMapping("/workflow/define/category")
public class FlowCategoryController extends BaseController {

    @Autowired
    private FlowCategoryService flowCategoryService;

    /**
     * 跳到流程规划界面
     *
     * @return
     */
    @RequestMapping("")
    public String list() {
        return forward("/workflow/define/category");
    }

    /**
     * 跳到流程规划界面
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String createCategorForm(Model model) {
        model.addAttribute("category", new FlowCategoryBean());
        model.addAttribute("action", "create");
        return forward("/workflow/define/category_form");
    }

    /**
     * 跳到流程规划界面
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String createCategory(@Valid FlowCategoryBean bean, RedirectAttributes redirectAttributes) {
        flowCategoryService.save(bean);
        redirectAttributes.addFlashAttribute("message", "保存成功");
        return redirect("/workflow/define/category");
    }

    /**
     * 跳到流程规划界面
     *
     * @return
     */
    @RequestMapping(value = "/update/{uid}", method = RequestMethod.GET)
    public String updateCategoryForm(@PathVariable("uid") String uid, Model model) {
        FlowCategoryBean category = flowCategoryService.getBean(uid);
        model.addAttribute("category", category);
        model.addAttribute("action", "update");
        return forward("/workflow/define/category_form");
    }

    /**
     * 跳到流程规划界面
     *
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String updateCategory(@Valid FlowCategoryBean bean, RedirectAttributes redirectAttributes) {
        flowCategoryService.save(bean);
        redirectAttributes.addFlashAttribute("message", "更新成功");
        return redirect("/workflow/define/category");
    }

    @RequestMapping(value = "delete/{uid}")
    public String delete(@PathVariable("uid") String uid, RedirectAttributes redirectAttributes) {
        flowCategoryService.removeByPk(uid);
        redirectAttributes.addFlashAttribute("message", "删除分类成功");
        return redirect("/workflow/define/category");
    }
}
