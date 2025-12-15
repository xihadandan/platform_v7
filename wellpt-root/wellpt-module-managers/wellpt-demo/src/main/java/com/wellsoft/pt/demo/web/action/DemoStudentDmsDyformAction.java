package com.wellsoft.pt.demo.web.action;

import com.wellsoft.pt.dms.core.annotation.Action;
import com.wellsoft.pt.dms.core.annotation.ActionConfig;
import com.wellsoft.pt.dms.core.support.DyFormActionData;
import com.wellsoft.pt.dms.core.web.action.ActionResult;
import com.wellsoft.pt.dms.ext.dyform.web.action.DyFormGetAction;
import com.wellsoft.pt.dms.ext.support.ActionDataCustomizer;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Action(value = "平台二开示例--培训demo--数据管理--学生操作")
public class DemoStudentDmsDyformAction extends DyFormGetAction {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 7134813705155577267L;

    @Autowired
    private DyFormFacade dyFormApiFacade;


    @ActionConfig(name = "保存学生信息", id = "btn_demo_save_student", validate = true)
    @ResponseBody
    public ActionResult saveStudent(@RequestBody DyFormActionData dyFormActionData) {
        ActionResult actionResult = createActionResult();
        try {
            String dataUuid = dyFormApiFacade.saveFormData(dyFormActionData.getDyFormData());
            Map<String, String> params = new HashMap<String, String>();
            //获取前端传入的参数
            System.out.println(dyFormActionData.getExtraString("param1"));

            //获取数据管理组件配置事件处理配置界面上传入的参数
            System.out.println(dyFormActionData.getExtraString("uiParam"));

            params.put("idValue", dataUuid);
            actionResult.setAppendUrlParams(params);
            actionResult.setTriggerEvents(ActionDataCustomizer.getTriggerEvents(dyFormActionData));
            actionResult.setClose(ActionDataCustomizer.isClose(dyFormActionData));
            actionResult.setRefresh(true);
            actionResult.setRefreshParent(ActionDataCustomizer.isRefreshParent(dyFormActionData, true));
            actionResult.setMsg(ActionDataCustomizer.getSuccessMsg(dyFormActionData, "保存成功！"));
        } catch (Exception e) {
            actionResult.setShowMsg(false);
            logger.error("学生信息保存失败：", e);
            throw new ServiceException("保存失败");
        }
        return actionResult;
    }

}
