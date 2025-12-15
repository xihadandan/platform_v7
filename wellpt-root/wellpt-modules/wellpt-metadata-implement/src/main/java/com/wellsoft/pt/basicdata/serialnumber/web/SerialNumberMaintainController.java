package com.wellsoft.pt.basicdata.serialnumber.web;

import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumberMaintain;
import com.wellsoft.pt.basicdata.serialnumber.service.SerialNumberMaintainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Description: 流水号维护控制层
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-13.1	zhouyq		2013-3-13		Create
 * </pre>
 * @date 2013-3-13
 */
@Controller
@RequestMapping("/basicdata/serialnumberMaintain")
public class SerialNumberMaintainController extends BaseController {
    @Autowired
    private SerialNumberMaintainService serialNumberMaintainService;

    private List<SerialNumberMaintain> serialList;

    /**
     * 跳转到流水号维护界面
     *
     * @return
     */
    @RequestMapping(value = "")
    public String serialNumberMaintain() {
        serialList = serialNumberMaintainService.getByIsEditor(true);
        System.out.println("serialList" + serialList);
        return forward("/basicdata/serialnumber/serialnumbermaintain");
    }

    /**
     * 显示流水号维护列表
     *
     * @param queryInfo
     * @return
     */
    @RequestMapping(value = "/list")
    public @ResponseBody
    JqGridQueryData listAsJson(JqGridQueryInfo queryInfo) {
        JqGridQueryData queryData = serialNumberMaintainService.query(queryInfo);
        return queryData;
    }

    public List<SerialNumberMaintain> getSerialList() {
        return serialList;
    }

    public void setSerialList(List<SerialNumberMaintain> serialList) {
        this.serialList = serialList;
    }
}
