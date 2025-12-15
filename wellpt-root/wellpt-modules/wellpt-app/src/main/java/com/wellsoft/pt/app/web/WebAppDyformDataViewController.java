/*
 * @(#)2017年9月11日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;

/**
 * Description: 通讯录模块设置控制层
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年9月11日.1	chenqiong		2017年9月11日		Create
 * </pre>
 * @date 2017年9月11日
 */
@Controller
@RequestMapping("/web/dyform")
public class WebAppDyformDataViewController extends BaseController {
    @Resource
    private DyFormFacade dyFormFacade;

    @RequestMapping(value = "/data/viewer")
    public String dyformDataViewer(Model model, String formUuid, String dataUuid,
                                   @RequestParam(required = false, value = "isEdit") String isEdit, @RequestParam(required = false, value = "isSubForm") String isSubForm) {
        model.addAttribute("formUuid", formUuid);
        if (StringUtils.isBlank(dataUuid)) {
            String systemUnitId = SpringSecurityUtils.getCurrentUserUnitId();
            DyFormData data = dyFormFacade.findUniqueData(formUuid, systemUnitId);
            dataUuid = data.getDataUuid();
        }
        // 默认非编辑状态
        if (StringUtils.isBlank(isEdit)) {
            isEdit = "false";
        }
        model.addAttribute("isEdit", isEdit);
        model.addAttribute("dataUuid", dataUuid);
        model.addAttribute("title", "表单数据查看器");
        model.addAttribute("isSubForm", isSubForm);
        return "web/app/app_dyform_data_viewer";
    }
}
