/*
 * @(#)2016-06-03 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.webmail.bean.WmMailboxBean;
import com.wellsoft.pt.webmail.facade.service.WmMailboxViewMaintain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Description: 邮箱视图维护控制器
 *
 * @author t
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-06-03.1	t		2016-06-03		Create
 * </pre>
 * @date 2016-06-03
 */
@Controller
@RequestMapping(value = "/webmail/wm/mailbox")
public class WmMailboxViewMaintainController extends BaseController {

    @Autowired
    private WmMailboxViewMaintain wmMailboxViewMaintain;

    /**
     * @param model
     * @return
     */
    @RequestMapping(value = "/new")
    public String openNewDialog(Model model) {
        // 绑定下拉列表等数据
        bindSelectData(model);

        model.addAttribute("wmMailboxBean", new WmMailboxBean());
        return forward("/webmail/wm_mailbox_new");
    }

    /**
     * @param model
     * @return
     */
    @RequestMapping(value = "/edit")
    public String openEditDialog(Model model) {
        // 绑定下拉列表等数据
        bindSelectData(model);

        model.addAttribute("wmMailboxBean", new WmMailboxBean());
        return forward("/webmail/wm_mailbox_edit");
    }

    /**
     * 如何描述该方法
     *
     * @param model
     */
    private void bindSelectData(Model model) {
		/*// 1、获取职级（数据字典）
		List<String> levelList = orgDutyExpandService.getDutyLevelList();
		Map<String, Object> levelMap = new LinkedHashMap<String, Object>();
		for (String level : levelList) {
			levelMap.put(level, level);
		}
		model.addAttribute("levels", levelMap);

		// 2、 职位序列（数据字典）
		List<Map<String, Object>> serieses = orgDutyExpandService.getDutySeriesList();
		Map<String, Object> serieseMap = new LinkedHashMap<String, Object>();
		for (Map<String, Object> map : serieses) {
			serieseMap.put(map.get("uuid").toString(), map.get("name"));
		}
		model.addAttribute("dutySeries", serieseMap);

		// 3、 获取直间接分类
		List<DataDictionary> list = orgDutyExpandService.getDirIndClassifyList();
		Map<String, Object> classifyMap = new LinkedHashMap<String, Object>();
		for (DataDictionary dataDict : list) {
			String name = dataDict.getName();
			String code = dataDict.getCode();
			classifyMap.put(code, name);
		}
		model.addAttribute("classifies", classifyMap);

		// 4、 是否启用
		Map<String, Object> isActiveMap = new LinkedHashMap<String, Object>();
		isActiveMap.put("1", "启用");
		isActiveMap.put("0", "停用");
		model.addAttribute("isActives", isActiveMap);*/
    }

}
