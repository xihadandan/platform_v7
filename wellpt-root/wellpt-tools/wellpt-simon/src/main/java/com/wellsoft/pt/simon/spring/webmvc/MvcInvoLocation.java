/*
 * @(#)2019年11月24日 V1.0
 * 
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.simon.spring.webmvc;

import javax.servlet.http.HttpServletRequest;

import org.javasimon.callback.calltree.CallTreeNode;

import com.wellsoft.pt.simon.spring.InvoLocation;

/**
 * Description: 如何描述该类
 *  
 * @author zhongzh
 * @date 2019年11月24日
 * @version 1.0
 * 
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年11月24日.1	zhongzh		2019年11月24日		Create
 * </pre>
 *
 */
public class MvcInvoLocation extends InvoLocation<String> {

    /**
     * HTTP Servlet Request.
     */
    private final HttpServletRequest request;

    /**
     * Request processing step: controller processing org view rendering.
     */
    private String step;

    private CallTreeNode callTree;

    /**
     *
     * @param logThreshold
     * @param rootHandler
     * @param rootSplit
     */
    public MvcInvoLocation(HttpServletRequest request, String handler, String step) {
        super(handler);
        this.request = request;
        this.step = step;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public CallTreeNode getCallTree() {
        return callTree;
    }

    public void setCallTree(CallTreeNode callTree) {
        this.callTree = callTree;
    }

}
