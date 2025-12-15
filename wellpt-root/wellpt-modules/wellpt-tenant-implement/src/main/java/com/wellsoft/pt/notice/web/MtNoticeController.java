/*
 * @(#)2015-5-29 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.notice.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.notice.entity.MtNotice;
import com.wellsoft.pt.notice.service.MtNoticeService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-5-29.1	Administrator		2015-5-29		Create
 * </pre>
 * @date 2015-5-29
 */
@Controller
@RequestMapping("/mt/notice")
public class MtNoticeController extends BaseController {

    @Autowired
    private MtNoticeService mtNoticeService;

    /**
     * @param model
     * @return
     */
    @RequestMapping(value = "/view")
    public String viewByDataUuid(HttpServletRequest request, @RequestParam(value = "dataUuid") String dataUuid) {
        ResultMessage msg = new ResultMessage();
        MtNotice mtNotice = mtNoticeService.getByDataUuid(dataUuid);
        if (mtNotice == null) {
            return forward("/app/notice/notice_error");
        }
        String tenantId = SpringSecurityUtils.getCurrentTenantId();
        // 同一租户下查看公告
        if (tenantId.equals(mtNotice.getTenantId())) {
            StringBuilder sb = new StringBuilder();
            sb.append("uuid=" + mtNotice.getDataUuid());
            sb.append("&");
            sb.append("methodName=" + request.getParameter("methodName"));
            sb.append("&");
            sb.append("scriptUrl=" + request.getParameter("scriptUrl"));
            return redirect("/fileManager/file/edit?" + sb.toString());
        }
        String url = "/fileManager/file/readonly?uuid=" + mtNotice.getDataUuid() + "&sourceTenantId="
                + mtNotice.getTenantId();
        return redirect(url);
    }

}
