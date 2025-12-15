/*
 * @(#)2021-08-25 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.facade.service.impl;

import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.workflow.facade.service.WfFlowInspectionFileRecordFacadeService;
import com.wellsoft.pt.workflow.service.WfFlowInspectionFileRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description: 数据库表WF_FLOW_INSPECTION_FILE_RECORD的门面服务实现类
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-08-25.1	zenghw		2021-08-25		Create
 * </pre>
 * @date 2021-08-25
 */
@Service
public class WfFlowInspectionFileRecordFacadeServiceImpl extends AbstractApiFacade implements WfFlowInspectionFileRecordFacadeService {

    @Autowired
    private WfFlowInspectionFileRecordService wfFlowInspectionFileRecordService;


}
