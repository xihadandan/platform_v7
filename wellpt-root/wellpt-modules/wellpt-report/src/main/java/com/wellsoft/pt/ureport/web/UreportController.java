package com.wellsoft.pt.ureport.web;

import com.wellsoft.context.web.controller.BaseController;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/9/26
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/9/26    chenq		2018/9/26		Create
 * </pre>
 */
@Controller
@RequestMapping("/wellUreport")
public class UreportController extends BaseController {


    @RequestMapping("/list")
    public String reportList(Model model) {
        return forward("/wellUreport/report_list");
    }


    @RequestMapping("/design")
    public String reportDesign(Model model, @RequestParam(value = "_u") String reportFile) {
        model.addAttribute("reportFile", reportFile);
        return forward("/wellUreport/report_design");
    }

    @RequestMapping("/output")
    public String outputReport(Model model, @RequestParam(value = "_u") String reportFile,
                               @RequestParam(value = "_t", required = false) String operations) {
        model.addAttribute("reportFile", reportFile);
        if (StringUtils.isNotBlank(operations)) {
            model.addAttribute("operations", operations);
        }
        return forward("/wellUreport/output_report");
    }

}
