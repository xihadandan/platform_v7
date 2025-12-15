package com.wellsoft.pt.basicdata.systemtable.web;

import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.basicdata.systemtable.service.SystemTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Description: 系统表结构控制层
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
@RequestMapping("/basicdata/systemtable")
public class SystemTableController extends BaseController {
    @Autowired
    private SystemTableService systemTableService;

    /**
     * 跳转到系统表结构界面
     *
     * @return
     */
    @RequestMapping(value = "")
    public String systemTable() {
        return forward("/basicdata/systemtable/systemtable");
    }

    /**
     * 显示系统表结构列表
     *
     * @param queryInfo
     * @return
     */

    @RequestMapping(value = "/list")
    public @ResponseBody
    JqGridQueryData listAsJson(JqGridQueryInfo queryInfo) {
        JqGridQueryData queryData = systemTableService.query(queryInfo);
        return queryData;
    }
}
