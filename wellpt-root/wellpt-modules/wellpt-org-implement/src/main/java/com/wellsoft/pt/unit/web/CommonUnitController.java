package com.wellsoft.pt.unit.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.mt.mgr.TenantManagerService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.unit.bean.CommonUnitBean;
import com.wellsoft.pt.unit.entity.CommonUnit;
import com.wellsoft.pt.unit.service.CommonUnitService;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: CommonUnitController action
 *
 * @author liuzq
 * @date 2013-11-5
 */
@Controller
@RequestMapping("/superadmin/unit/commonUnit")
public class CommonUnitController extends BaseController {

    @Autowired
    private TenantManagerService tenantManagerService;

    @Autowired
    private CommonUnitService commonUnitService;

    /**
     * 打开单位通讯录界面
     *
     * @return
     */
    @RequestMapping(value = "")
    public String unit(Model model) {
        model.addAttribute("tenantList", tenantManagerService.getNormalTenants());
        return forward("/superadmin/unit/common_unit");
    }

    @RequestMapping(value = "/tenantUnitList", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage getCommonUnitList() {
        ResultMessage resultMessage = new ResultMessage();
        try {
            UserDetails userDetail = SpringSecurityUtils.getCurrentUser();
            String tenantId = userDetail.getTenantId();
            List<CommonUnit> unitList = commonUnitService.getListByTenantId(tenantId);
            List<CommonUnitBean> list = new ArrayList<CommonUnitBean>();
            for (CommonUnit c : unitList) {
                CommonUnitBean cu = new CommonUnitBean();
                cu.setId(c.getId());
                cu.setName(c.getName());
                list.add(cu);
            }
            resultMessage.setData(list);
            resultMessage.setSuccess(true);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            resultMessage.setSuccess(false);
        }
        return resultMessage;
    }
}
