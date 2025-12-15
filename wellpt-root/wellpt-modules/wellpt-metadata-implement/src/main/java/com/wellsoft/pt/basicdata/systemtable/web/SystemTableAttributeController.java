package com.wellsoft.pt.basicdata.systemtable.web;

import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.basicdata.systemtable.service.SystemTableAttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Description: 系统表结构属性控制层
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-21.1	zhouyq		2013-3-21		Create
 * </pre>
 * @date 2013-3-21
 */

@Controller
@RequestMapping("/basicdata/systemtableattri")
public class SystemTableAttributeController extends BaseController {
    @Autowired
    private SystemTableAttributeService systemTableAttributeService;

    /**
     * 显示系统表结构属性列表
     *
     * @param queryInfo
     * @return
     */

    @RequestMapping(value = "/list")
    public @ResponseBody
    JqGridQueryData listAsJson(JqGridQueryInfo queryInfo) {
        JqGridQueryData queryData = systemTableAttributeService.query(queryInfo);
        return queryData;
    }
}
