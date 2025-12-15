/*
 * @(#)2015-7-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.gz.facade;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.workflow.gz.entity.WfGzDataSync;

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
 * 2015-7-16.1	zhulh		2015-7-16		Create
 * </pre>
 * @date 2015-7-16
 */
public interface WfGzDataSyncExchange extends BaseService {

    /**
     * 如何描述该方法
     *
     * @return
     */
    List<WfGzDataSync> getAllByModifyTimeAsc();

    /**
     * 如何描述该方法
     *
     * @param syncDatas
     */
    void syncDatas(List<WfGzDataSync> syncDatas);

}
