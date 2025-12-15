package com.wellsoft.pt.basicdata.serialnumber.web;

import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.basicdata.serialnumber.service.SerialNumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Description: 流水号定义控制层
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-5.1	zhouyq		2013-3-5		Create
 * </pre>
 * @date 2013-3-5
 */

@Controller
@RequestMapping("/basicdata/serialnumber")
public class SerialNumberController extends BaseController {
    @Autowired
    private SerialNumberService serialNumberService;

    /**
     * 跳转到流水号定义界面
     *
     * @return
     */
    @RequestMapping(value = "")
    public String serialNumber() {
        return forward("/basicdata/serialnumber/serialnumber");
    }

    /**
     * 显示流水号定义列表
     *
     * @param queryInfo
     * @return
     */
    @RequestMapping(value = "/list")
    public @ResponseBody
    JqGridQueryData listAsJson(JqGridQueryInfo queryInfo) {
        JqGridQueryData queryData = serialNumberService.query(queryInfo);
        return queryData;
    }
}
