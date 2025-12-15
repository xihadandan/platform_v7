package com.wellsoft.pt.bot.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.bot.entity.BotRuleConfEntity;
import com.wellsoft.pt.bot.service.BotRuleConfService;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/9/14
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/9/14    chenq		2018/9/14		Create
 * </pre>
 */
@Controller
@RequestMapping("/bot/ruleConfig")
public class BotRuleConfigController extends BaseController {

    @Resource
    BotRuleConfService botRuleConfService;

    @RequestMapping("/index")
    public String config(Model model) {
        model.addAttribute("isDebug", super.isDebug());
        return forward("/bot/bot_rule_config");
    }

    @RequestMapping("/isDebug")
    @ResponseBody
    public boolean isDebug() {
        return super.isDebug();
    }

    @RequestMapping("/{id}")
    public String detailById(Model model, @PathVariable("id") String id, HttpServletRequest request) {
        BotRuleConfEntity confEntity = botRuleConfService.getById(id);
        if (confEntity == null) {
            return forward("/pt/common/404");
        }
        return forwardDetail(request, confEntity, model);
    }

    private String forwardDetail(HttpServletRequest request, BotRuleConfEntity confEntity, Model model) {
        model.addAttribute("uuid", confEntity.getUuid());
        boolean editable = BooleanUtils.toBoolean(request.getParameter("editable"));
        model.addAttribute("editable", editable);
        return forward("/bot/bot_rule_config_detail");
    }

    @RequestMapping("/uuid/{uuid}")
    public String detailByUuid(Model model, @PathVariable("uuid") String uuid, HttpServletRequest request) {
        BotRuleConfEntity confEntity = botRuleConfService.getOne(uuid);
        if (confEntity == null) {
            return forward("/pt/common/404");
        }
        return forwardDetail(request, confEntity, model);
    }

}
