package com.wellsoft.oauth2.web.controller;

import com.wellsoft.oauth2.entity.BatchDataImportDetailsHisEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/9/26
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/9/26    chenq		2019/9/26		Create
 * </pre>
 */
@Controller
@RequestMapping("/batchImportDetails")
public class BatchImportDetailsController extends
        EntityManagerController<BatchDataImportDetailsHisEntity> {
    @Override
    protected String toIndex(Model model, HttpServletRequest request, HttpServletResponse response,
                             String defaultView) {
        model.addAttribute("batchDataImportUuid", request.getParameter("buuid"));
        model.addAttribute("status", request.getParameter("s"));
        return super.toIndex(model, request, response, defaultView);
    }
}
