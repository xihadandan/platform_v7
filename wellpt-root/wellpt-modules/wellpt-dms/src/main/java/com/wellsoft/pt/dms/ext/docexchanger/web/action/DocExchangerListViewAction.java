package com.wellsoft.pt.dms.ext.docexchanger.web.action;

import com.wellsoft.pt.app.facade.service.AppContextService;
import com.wellsoft.pt.dms.core.annotation.Action;
import com.wellsoft.pt.dms.core.annotation.ActionConfig;
import com.wellsoft.pt.dms.core.annotation.ListViewActionConfig;
import com.wellsoft.pt.dms.core.support.DocExchangeActionData;
import com.wellsoft.pt.dms.core.web.action.ActionResult;
import com.wellsoft.pt.dms.core.web.action.OpenViewAction;
import com.wellsoft.pt.dms.facade.service.DocExchangerFacadeService;
import com.wellsoft.pt.dms.service.DmsDocExchangeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Description: 文档交换-视图列表操作
 *
 * @author chenq
 * @date 2018/5/17
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/17    chenq		2018/5/17		Create
 * </pre>
 */
@Action("文档交换")
public class DocExchangerListViewAction extends OpenViewAction {

    @Autowired
    private AppContextService appContextService;

    @Autowired
    private DmsDocExchangeRecordService dmsDocExchangeRecordService;

    @Autowired
    private DocExchangerFacadeService docExchangerFacadeService;

    @ListViewActionConfig(name = "查看", id = DocExchangerActions.ACTION_VIEW_DOC_EXCHANGER, executeJsModule = "DmsDocExchangerListViewAction")
    public String viewDocExchanger(String pkValue, HttpServletRequest request,
                                   HttpServletResponse response, Model model) {
        return open(pkValue, request, response, model);
    }


    @ListViewActionConfig(name = "编辑", id = DocExchangerActions.ACTION_EDIT_DOC_EXCHANGER, executeJsModule = "DmsDocExchangerListViewAction")
    public String editDocExchanger(String pkValue, HttpServletRequest request,
                                   HttpServletResponse response, Model model) {
        return open(pkValue, request, response, model);
    }


    @ActionConfig(name = "批量签收", id = DocExchangerActions.ACTION_BATCH_SIGN_DOC_EXCHANGER, executeJsModule = "DmsDocExchangerListViewAction")
    @ResponseBody
    public ActionResult signDoc(@RequestBody List<DocExchangeActionData> actionDataList,
                                HttpServletRequest request,
                                HttpServletResponse response, Model model) {
        ActionResult actionResult = createActionResult();
        try {
            for (DocExchangeActionData data : actionDataList) {
                docExchangerFacadeService.signDocExchangeRecord(data.getDocExcRecordUuid(), false,
                        null);
            }
        } catch (Exception e) {
            logger.error("签收异常：", e);
            actionResult.setSuccess(false);
            actionResult.setMsg("签收失败：" + e.getMessage());
            return actionResult;
        }
        actionResult.setRefresh(true);
        actionResult.setMsg("签收成功");
        return actionResult;
    }
}
