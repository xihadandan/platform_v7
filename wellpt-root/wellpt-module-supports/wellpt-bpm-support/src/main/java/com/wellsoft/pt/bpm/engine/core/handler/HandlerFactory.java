/*
 * @(#)2012-11-27 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.core.handler;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.core.ext.CancelTaskHandler;
import com.wellsoft.pt.bpm.engine.enums.TransferCode;
import com.wellsoft.pt.bpm.engine.node.*;

/**
 * Description: 结点处理接口工厂类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-11-27.1	zhulh		2012-11-27		Create
 * </pre>
 * @date 2012-11-27
 */
public class HandlerFactory {

    /**
     * 根据任务结点，返回相应的处理器
     *
     * @param node
     * @return
     */
    public static Handler getHanlder(Node node) {
        if (node instanceof StartNode) {
            return ApplicationContextHolder.getBean(StartHandler.class);
        }

        if (node instanceof SubTaskNode) {
            return ApplicationContextHolder.getBean(SubTaskHandler.class);
        }

        if (node instanceof TaskNode) {
            return ApplicationContextHolder.getBean(TaskHandler.class);
        }

        if (node instanceof EndNode) {
            return ApplicationContextHolder.getBean(EndHandler.class);
        }

        return null;
    }

    public static Handler getHanlder(TransferCode transferCode) {
        Handler handler = null;
        Integer code = transferCode.getCode();
        switch (code) {
            case 1:
                // 提交流转 Submit(1)
                handler = ApplicationContextHolder.getBean(TaskHandler.class);
                break;
            case 2:
                // 退回流转
                handler = ApplicationContextHolder.getBean(TaskHandler.class);
                break;
            case 3:
                // 撤回流转
                handler = ApplicationContextHolder.getBean(CancelTaskHandler.class);
                break;
            case 4:
                // 移交环节流转
                handler = ApplicationContextHolder.getBean(TaskHandler.class);
                break;
            case 5:
                // 办理人为空自动跳过
                handler = ApplicationContextHolder.getBean(TaskHandler.class);
                break;
            case 6:
                // 转办提交流转
                handler = ApplicationContextHolder.getBean(TaskHandler.class);
                break;
            default:
                break;
        }
        return handler;
    }

}
