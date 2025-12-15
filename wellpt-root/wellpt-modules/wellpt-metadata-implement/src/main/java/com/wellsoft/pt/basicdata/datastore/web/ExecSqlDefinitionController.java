package com.wellsoft.pt.basicdata.datastore.web;

import com.wellsoft.context.web.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Description: 可执行SQL配置控制层
 *
 * @author chenq
 * @date 2018/9/4
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/9/4    chenq		2018/9/4		Create
 * </pre>
 */
@Controller
@RequestMapping("/basicdata/datastore/execSql")
public class ExecSqlDefinitionController extends BaseController {

    @RequestMapping("/list")
    public String list() {
        return forward("/basicdata/datastore/execute_sql_definition");
    }
}
