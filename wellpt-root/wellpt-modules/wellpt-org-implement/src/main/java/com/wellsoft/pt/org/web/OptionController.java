/*
 * @(#)2013-1-14 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.org.bean.OptionBean;
import com.wellsoft.pt.org.service.OptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-14.1	zhulh		2013-1-14		Create
 * </pre>
 * @date 2013-1-14
 */
@Controller
@RequestMapping(value = "/org/option")
public class OptionController extends BaseController {
    @Autowired
    private OptionService optionService;

    /**
     * 打开组织选择项列表界面
     *
     * @return
     */
    @RequestMapping(value = "/list")
    public String optionList() {
        return forward("/org/option");
    }

    @RequestMapping(value = "/get")
    public @ResponseBody
    ResultMessage get(@RequestBody OptionBean option) {
        ResultMessage resultMessage = new ResultMessage();
        OptionBean bean = optionService.getBean(option.getUuid());
        bean.setId(bean.getId());
        resultMessage.setData(bean);
        return resultMessage;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public @ResponseBody
    ResultMessage save(@RequestBody OptionBean bean) {
        ResultMessage resultMessage = new ResultMessage();
        optionService.saveBean(bean);
        resultMessage.setData(bean);
        return resultMessage;
    }

    @RequestMapping(value = "/delete")
    public @ResponseBody
    ResultMessage delete(@RequestBody OptionBean bean) {
        ResultMessage resultMessage = new ResultMessage();
        optionService.remove(bean.getUuid());
        resultMessage.setData(bean);
        return resultMessage;
    }

}
