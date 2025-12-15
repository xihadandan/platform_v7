/*
 * @(#)2016年2月17日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.web;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.util.barcode.QRCodeEncoderUtils;
import com.wellsoft.context.util.web.HttpRequestDeviceUtils;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.basicdata.params.facade.SystemParams;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年2月17日.1	zhulh		2016年2月17日		Create
 * </pre>
 * @date 2016年2月17日
 */
@Controller
@RequestMapping("/mobile/app")
public class MobileAppDownloadController extends BaseController {

    @RequestMapping("/download")
    public String download(HttpServletRequest request, HttpServletResponse response) {
        if (HttpRequestDeviceUtils.isAndroidDevice(request)) {
            return android(request, response);
        }
        if (HttpRequestDeviceUtils.isIOSDevice(request)) {
            return ios(request, response);
        }
        return redirect("/resfacade/mobile?filename=mobile.zip");
    }

    @RequestMapping("/qrcode")
    public void qrcode(HttpServletRequest request, HttpServletResponse response) {
        String content = request.getRequestURL().toString();
        content = StringUtils.replace(content, "qrcode", "download");
        try {
            QRCodeEncoderUtils.QRCodeEncoder(request, response, content, 127, 127, 6);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public String android(HttpServletRequest request, HttpServletResponse response) {
        return redirect("/resfacade/mobile/android?filename=wellim.apk");
    }

    public String ios(HttpServletRequest request, HttpServletResponse response) {
        if (SystemParams.getValue("mobile.app.ios.dowload.from.market", "false").equalsIgnoreCase(Config.TRUE)) {
            return redirect(SystemParams.getValue("mobile.app.ios.market.dowload.url"));
        }
        return redirect(SystemParams.getValue("mobile.app.ios.market.dowload.url"));
    }

}
