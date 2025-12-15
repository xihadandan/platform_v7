/*
 * @(#)2018年10月10日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.pt.bpm.engine.context.event.Event;
import com.wellsoft.pt.bpm.engine.core.Archive;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年10月10日.1	zhulh		2018年10月10日		Create
 * </pre>
 * @date 2018年10月10日
 */
public interface FlowArchiveService {

    /**
     * 流程归档
     *
     * @param event
     * @param archive
     */
    void archive(Event event, Archive archive);

}
