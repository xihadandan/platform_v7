package com.wellsoft.pt.app.web;

import com.wellsoft.pt.app.facade.service.AppContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年10月14日   chenq	 Create
 * </pre>
 */
@Controller
@RequestMapping("/js")
public class JavascriptController {

    @Autowired
    AppContextService appContextService;

    @GetMapping("/requirejsConfigScript.js")
    public @ResponseBody
    String requirejsConfigScript(@RequestParam("ids") ArrayList<String> ids) {
        return appContextService.getJavaScriptModuleConfigScript(ids);
    }
}
