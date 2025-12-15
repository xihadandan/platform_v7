package com.wellsoft.pt.basicdata.excelexporttemplate.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface TextExportService {
    /**
     * 2015-07-09 yuyq
     * 如何描述该方法
     *
     * @param txt      文本里面显示的内容
     * @param request
     * @param response
     * @return
     */
    public String generateTextFile(String txt, HttpServletRequest request, HttpServletResponse response);
}
