package com.wellsoft.pt.report.web;

import com.wellsoft.context.web.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Description: 统计报表 控制层
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-7-11.1	zhouyq		2013-7-11		Create
 * </pre>
 * @date 2013-7-11
 */
@Controller
@RequestMapping("/report/design")
public class ReportController extends BaseController {

    /**
     * 跳转到报表设计
     *
     * @return
     */
    @RequestMapping(value = "")
    public String report() {
        return forward("/report/report");
    }

    /**
     * 跳转到报表1
     *
     * @return
     */
    @RequestMapping(value = "showReport")
    public String showReport() {
        return forward("/report/showreport");
    }

    /**
     * 跳转到报表2
     *
     * @return
     */
    @RequestMapping(value = "showReport1")
    public String showReport1() {
        return forward("/report/showreport1");
    }

}
