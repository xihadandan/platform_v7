package com.wellsoft.pt.webmail.web.action;


import com.wellsoft.pt.dms.core.annotation.Action;
import com.wellsoft.pt.dms.core.annotation.ActionConfig;
import com.wellsoft.pt.dms.core.support.DyFormActionData;
import com.wellsoft.pt.dms.core.web.action.ActionResult;
import com.wellsoft.pt.dms.ext.dyform.service.DyFormActionService;
import com.wellsoft.pt.dms.ext.dyform.web.action.DyFormSaveAction;
import com.wellsoft.pt.webmail.constant.MailConstant;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * Description: 通讯录表单动作
 *
 * @author chenq
 * @date 2018/6/6
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/6/6    chenq		2018/6/6		Create
 * </pre>
 */
@Action("平台邮件-通讯录表单操作")
public class ContactBookDyfromAction extends DyFormSaveAction {
    @Autowired
    private DyFormActionService dyFormActionService;


    @ActionConfig(name = "保存通讯录联系人", id = "btn_contact_book_save", validate = true)
    @ResponseBody
    public ActionResult saveContactBook(@RequestBody DyFormActionData dyFormActionData) {
        //生成联系人ID
        if (StringUtils.isBlank(dyFormActionData.getDataUuid())) {
            dyFormActionData.getDyFormData().setFieldValue("contact_id",
                    MailConstant.CONTACT_BOOK_ID_PREFIX + DateFormatUtils.format(new Date(),
                            "yyMMddHHmmssSSS"));
        }
        return super.actionPerformed(dyFormActionData);
    }


    @ActionConfig(name = "保存通讯录分组", id = "btn_contact_book_group_save", validate = true)
    @ResponseBody
    public ActionResult saveContactBookGroup(@RequestBody DyFormActionData dyFormActionData) {
        //生成联系人分组ID
        if (StringUtils.isBlank(dyFormActionData.getDataUuid())) {
            dyFormActionData.getDyFormData().setFieldValue("group_id",
                    MailConstant.CONTACT_BOOK_GRP_ID_PREFIX + DateFormatUtils.format(new Date(),
                            "yyMMddHHmmssSSS"));
        }
        return super.actionPerformed(dyFormActionData);
    }


}
