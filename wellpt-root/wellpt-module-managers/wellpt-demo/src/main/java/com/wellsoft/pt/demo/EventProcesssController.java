package com.wellsoft.pt.demo;

import com.wellsoft.context.annotation.Description;
import com.wellsoft.context.web.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Description:
 *
 * @author hongjz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年3月25日.1	hongjz		2020年3月25日		Create
 * </pre>
 * @date 2020年3月25日
 */
@Controller
@RequestMapping("/demo")
public class EventProcesssController extends BaseController {

    /**
     * 跳转到baidu
     *
     * @return
     */
    @RequestMapping("/")
    @Description("跳转到baidu")
    public String toBaidu() {
        return redirect("http://www.baidu.com");
    }

    /**
     * 打印问候信息
     *
     * @return
     */
    @RequestMapping("/greeting")
    @Description("打印问候信息")
    @ResponseBody
    public String sayHello(String greeting, String name) {
        System.out.println();
        return name + ":" + greeting;
    }
}
