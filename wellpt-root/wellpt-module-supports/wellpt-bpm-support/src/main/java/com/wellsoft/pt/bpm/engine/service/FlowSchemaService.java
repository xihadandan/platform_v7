/*
 * @(#)2018年4月9日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.pt.bpm.engine.dao.FlowSchemaDao;
import com.wellsoft.pt.bpm.engine.entity.FlowSchema;
import com.wellsoft.pt.bpm.engine.parser.FlowConfiguration;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.timer.support.TsTimerConfigUsedChecker;
import com.wellsoft.pt.timer.support.TsWorkTimePlanUsedChecker;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月9日.1	chenqiong		2018年4月9日		Create
 * </pre>
 * @date 2018年4月9日
 */
public interface FlowSchemaService extends JpaService<FlowSchema, FlowSchemaDao, String>, TsWorkTimePlanUsedChecker, TsTimerConfigUsedChecker {

    /**
     * @param uuid
     * @param name
     * @param configuration
     * @return
     */
    String save(String uuid, String name, FlowConfiguration configuration);

    /**
     * @param uuid
     * @param name
     * @param configXml
     * @param configJson
     */
    void update(String uuid, String name, String configXml, String configJson);

    /**
     * @param uuid
     * @param name
     * @param oldXml
     * @param oldJson
     * @param configuration
     * @param isCreate
     * @param allowAsync
     */
    void saveLog(String uuid, String name, String oldXml, String oldJson, FlowConfiguration configuration, boolean isCreate, boolean allowAsync);

}
