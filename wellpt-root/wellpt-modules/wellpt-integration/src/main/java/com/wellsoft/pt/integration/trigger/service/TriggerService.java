/*
 * @(#)2014-12-3 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.trigger.service;

import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.integration.bean.TableTigInfo;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-12-3.1	zhulh		2014-12-3		Create
 * </pre>
 * @date 2014-12-3
 */
public interface TriggerService extends BaseService {

    public List<TableTigInfo> query(QueryInfo queryInfo);

    String[] generateCreateTriggerSql(String tableName);

    void generateCreateTrigger(String tableName, String triggerFtl);

    String generateDisableTriggerSql(String tableName);

    String generateEnableTriggerSql(String tableName);

    String generateDropTriggerSql(String tableName);

    List<String> getAlltableName();
}
