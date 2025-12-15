/*
 * @(#)2012-11-23 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.repository;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.node.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-11-23.1	zhulh		2012-11-23		Create
 * </pre>
 * @date 2012-11-23
 */
public class RepositoryFactory {
    public static Repository getRepository(Node node) {
        if (node instanceof StartNode) {
            return ApplicationContextHolder.getBean(StartRepository.class);
        }

        if (node instanceof TaskNode) {
            return ApplicationContextHolder.getBean(TaskRepository.class);
        }

        if (node instanceof SubTaskNode) {
            return ApplicationContextHolder.getBean(SubTaskRepository.class);
        }

        if (node instanceof EndNode) {
            return ApplicationContextHolder.getBean(EndRepository.class);
        }

        return null;
    }
}
