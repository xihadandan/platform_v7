/*
 * @(#)2012-10-23 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.workflow.entity.FlowFormat;
import com.wellsoft.pt.workflow.service.FlowFormatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

/**
 * Description: 信息格式控制器类
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
@RequestMapping("/workflow/define/format")
public class FlowFormatController extends BaseController {
    @Autowired
    private FlowFormatService flowFormatService;

    /**
     * 跳到流程规划界面
     *
     * @return
     */
    @RequestMapping("")
    public String list() {
        return forward("/workflow/define/format");
    }

    /**
     * 跳到流程规划界面
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String createFormatForm(Model model) {
        model.addAttribute("format", new FlowFormat());
        model.addAttribute("action", "create");
        return forward("/workflow/define/format_form");
    }

    /**
     * 跳到流程规划界面
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String createFormat(@Valid FlowFormat format, RedirectAttributes redirectAttributes) {
        flowFormatService.save(format);
        redirectAttributes.addFlashAttribute("message", "保存成功");
        return redirect("/workflow/define/format");
    }

    /**
     * 跳到流程规划界面
     *
     * @return
     */
    @RequestMapping(value = "/update/{uid}", method = RequestMethod.GET)
    public String updateFormatForm(@PathVariable("uid") String uid, Model model) {
        FlowFormat format = flowFormatService.get(uid);
        model.addAttribute("format", format);
        model.addAttribute("action", "update");
        return forward("/workflow/define/format_form");
    }

    /**
     * 跳到流程规划界面
     *
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String updateFormat(@Valid FlowFormat format, RedirectAttributes redirectAttributes) {
        flowFormatService.save(format);
        redirectAttributes.addFlashAttribute("message", "更新成功");
        return redirect("/workflow/define/format");
    }

    @RequestMapping(value = "delete/{uid}")
    public String delete(@PathVariable("uid") String uid, RedirectAttributes redirectAttributes) {
        flowFormatService.removeByPk(uid);
        redirectAttributes.addFlashAttribute("message", "删除分类成功");
        return redirect("/workflow/define/format");
    }
}
