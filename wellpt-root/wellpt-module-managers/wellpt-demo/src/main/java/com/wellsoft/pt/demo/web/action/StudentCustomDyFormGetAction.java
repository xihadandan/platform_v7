package com.wellsoft.pt.demo.web.action;

import com.wellsoft.pt.dms.core.annotation.Action;
import com.wellsoft.pt.dms.core.annotation.ActionConfig;
import com.wellsoft.pt.dms.core.support.DyFormActionData;
import com.wellsoft.pt.dms.core.support.DyformDocumentData;
import com.wellsoft.pt.dms.ext.dyform.web.action.DyFormGetAction;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;


@Action(value = "平台二开示例--数据管理--表单页面")
public class StudentCustomDyFormGetAction extends DyFormGetAction {

    @ActionConfig(name = "加载数据", id = "btn_get_student_dyform_data")
    @ResponseBody
    public DyformDocumentData loadStudentDyformData(@RequestBody DyFormActionData dyFormActionData) {
        DyformDocumentData dyformDocumentData = super.actionPerformed(dyFormActionData);

        return dyformDocumentData;
    }

}
