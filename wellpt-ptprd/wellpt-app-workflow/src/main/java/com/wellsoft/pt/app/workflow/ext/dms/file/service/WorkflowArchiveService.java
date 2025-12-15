/*
 * @(#)2018年9月26日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.ext.dms.file.service;

import com.wellsoft.pt.bpm.engine.context.event.Event;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年9月26日.1	zhulh		2018年9月26日		Create
 * </pre>
 * @date 2018年9月26日
 */
public interface WorkflowArchiveService {

    /**
     * 流程数据归档
     *
     * @param folderUuid
     * @param event
     */
    void archive(String folderUuid, Event event);

}
