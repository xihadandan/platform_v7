/*
 * @(#)2019年4月16日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.service;

import com.wellsoft.pt.basicdata.iexport.suport.IexportDataRecord;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年4月16日.1	zhulh		2019年4月16日		Create
 * </pre>
 * @date 2019年4月16日
 */
public interface IexportDataRecorderService {

    /**
     * 记录更新
     *
     * @param record
     */
    void recordUpdate(IexportDataRecord record);

    /**
     * 记录插入
     *
     * @param record
     */
    void recordInsert(IexportDataRecord record);

}
