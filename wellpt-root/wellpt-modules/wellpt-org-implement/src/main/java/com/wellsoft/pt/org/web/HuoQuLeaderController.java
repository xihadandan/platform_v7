package com.wellsoft.pt.org.web;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.org.bean.UserBean;
import com.wellsoft.pt.org.service.HuoQuLeaderService;
import com.wellsoft.pt.org.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/org/huoQuLeader")
// 此路径按一定方式,可随自己设定
public class HuoQuLeaderController extends BaseController {

    @Autowired
    private HuoQuLeaderService huoQuLeaderService;

    @Autowired
    private UserService userService;

    /**
     * liyb 2014.12.25
     * userId 用户id
     * 获取上级领导,职位汇报对象,所有领导
     *
     * @param model
     * @param userId
     * @param staffNames
     * @return
     */
    @RequestMapping(value = "/getId")
    public String getLeaderId(Model model, @RequestParam(value = "userId", required = false) String userId,
                              @RequestParam(value = "staffNames", required = false) String staffNames) {

        List<UserBean> list = new ArrayList<UserBean>();

        String[] arrayUid = userId.split(Separator.SEMICOLON.getValue()); // 分割出员工的id(即用户id),组合成数组
        for (String uid : arrayUid) { // 遍历员工的id(即用户id)
            UserBean ub = new UserBean();

            ub.setUserName(userService.getById(uid).getUserName()); // 获取员工姓名
            ub.setLeaderNames(huoQuLeaderService.getSuperLeadName(uid)); // 获取用户上级领导的姓名
            ub.setReportToNames(huoQuLeaderService.getReportLeaderName(uid)); // 获取职位汇报对象的姓名
            ub.setAllLeaderNames(huoQuLeaderService.getAllLeadName(uid)); // 获取所有领导的姓名

            list.add(ub);
        }
        model.addAttribute("list", list);
        model.addAttribute("empName", staffNames);
        model.addAttribute("empNo", userId);

        return "/org/hqld"; // 返回到jsp页面（括号里的是路径）
    }
}
