/*
 * @(#)2016-07-24 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.app.design.container.AbstractContainer;
import com.wellsoft.pt.app.entity.AppPageDefinition;
import com.wellsoft.pt.basicdata.selective.support.DataItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-14.1	zhulh		2016-07-24		Create
 * </pre>
 * @date 2016-07-24
 */
@Controller
@RequestMapping(value = "/app/app/product")
public class AppProductMgrController extends BaseController {

    @Autowired
    private List<AbstractContainer> pageContainers;

    /**
     * 打开列表界面
     *
     * @return
     */
    @RequestMapping(value = "/list")
    public String appProductList(Model model) {
        model.addAttribute("appPageDefinition", new AppPageDefinition());
        List<DataItem> pageDesigners = new ArrayList<DataItem>();
        OrderComparator.sort(pageContainers);
        for (AbstractContainer pageContainer : pageContainers) {
            if (pageContainer.getCategory() != null) {
                continue;
            }
            DataItem dataItem = new DataItem();
            dataItem.setLabel(pageContainer.getName());
            dataItem.setValue(pageContainer.getType());
            pageDesigners.add(dataItem);
        }
        model.addAttribute("pageDesigners", pageDesigners);
        return forward("/app/app_product_list");
    }

}
