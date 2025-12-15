/*
 * @(#)2019年11月23日 V1.0
 * 
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.simon.spring.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.javasimon.Manager;
import org.javasimon.SimonManager;
import org.javasimon.Split;
import org.javasimon.callback.calltree.CallTree;
import org.javasimon.callback.calltree.CallTreeNode;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.wellsoft.pt.simon.spring.InvoLocation;
import com.wellsoft.pt.simon.support.AbstractInterceptor;
import com.wellsoft.pt.simon.support.CallTreeUtils;
import com.wellsoft.pt.simon.support.ClassUtils;

/**
 * Description: 重写HandlerStopwatchSource
 *  
 * @author zhongzh
 * @date 2019年11月23日
 * @version 1.0
 * 
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年11月23日.1	zhongzh		2019年11月23日		Create
 * </pre>
 *
 */
public class MvcHandlerInterceptor extends AbstractInterceptor<String, MvcInvoLocation> implements HandlerInterceptor {

    protected static final ThreadLocal<MvcInvoLocation> threadLocation = new ThreadLocal<MvcInvoLocation>();

    private String commonPrefix = "com.wellsoft.";

    /**
     * Suffix used for Simon names of Controllers.
     */
    private static final String CONTROLLER_SUFFIX = "ctrl";

    /**
     * Suffix used for Simon names of Views.
     */
    private static final String VIEW_SUFFIX = "view";

    /**
    *
    * @param stopwatchSource stopwatch provider for method invocation
    */
    public MvcHandlerInterceptor(MvcStopwatchSource stopwatchSource) {
        super(stopwatchSource);
    }

    public MvcHandlerInterceptor(Manager manager) {
        this(new MvcStopwatchSource(manager));
    }

    public MvcHandlerInterceptor() {
        this(new MvcStopwatchSource(SimonManager.manager()));
    }

    /**
     * Invoked before controller.
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        StringBuilder handlerBuilder = new StringBuilder();
        // Append controller type
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            handlerBuilder.append(ClassUtils.shiftPrefix(handlerMethod.getBeanType(), commonPrefix)).append(".");
            handlerBuilder.append(handlerMethod.getMethod().getName());
        } else {
            handlerBuilder.append(ClassUtils.shiftPrefix(handler.getClass(), commonPrefix));
        }
        final MvcInvoLocation location = new MvcInvoLocation(request, handlerBuilder.toString(), CONTROLLER_SUFFIX);
        threadLocation.set(location);
        CallTreeUtils.set(CallTreeUtils.create(null));

        // Start controller stopwatch
        Split split = startWatch(location);
        split.setAttribute("requestURI", request.getRequestURI());
        return true;
    }

    /**
     * Invoked between controller and view.
     */
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) {
        if (modelAndView != null) { // 存在视图层
            // Stop controller stopwatch
            stopWatch(modelAndView);

            MvcInvoLocation location = (MvcInvoLocation) threadLocation.get();
            location.setStep(VIEW_SUFFIX);

            // Start view stopwatch
            Split split = startWatch(location);
            split.setAttribute("requestURI", request.getRequestURI());
        }
    }

    /**
     * Invoked after view.
     */
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // Stop view stopwatch
        stopWatch(ex);
        CallTreeNode rootNode = null;
        CallTree callTree = CallTreeUtils.get();
        MvcInvoLocation location = threadLocation.get();
        if (null != callTree) {
            rootNode = callTree.getRootNode();
            CallTreeUtils.remove();
        }
        if (null != location) {
            location.setCallTree(rootNode);
            // Remove location
            threadLocation.remove();
        }
        if (null != rootNode) {
            logger.info("Call Tree:\r\n" + rootNode.toString());
        }
    }

    /**
     * Stop current thread stopwatch (if any).
     *
     * @return Stopped split
     */
    protected final Split stopWatch(Object data) {
        InvoLocation<String> location = threadLocation.get();
        return stopWatch(location, data);
    }
}
