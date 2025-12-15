package com.wellsoft.pt.di.controller;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: 简单post协议转换为实际的请求路径
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2021年10月22日   chenq	 Create
 * </pre>
 */
@Controller
@RequestMapping(value = "/post-conversion")
public class SimplePostForwardController extends AbstractPostForwardController {


    @RequestMapping(value = "/handle", method = RequestMethod.POST)
    public void handle(HttpServletRequest request, HttpServletResponse response) {
        try {
            PostParameter postParameter = gson.fromJson(IOUtils.toString(request.getInputStream(), Charsets.UTF_8.name()), PostParameter.class);
            super.forward(postParameter, request, response);
        } catch (Exception e) {
            logger.error("post 异常：", e);
            responseException(response, e);
        }

    }

}
