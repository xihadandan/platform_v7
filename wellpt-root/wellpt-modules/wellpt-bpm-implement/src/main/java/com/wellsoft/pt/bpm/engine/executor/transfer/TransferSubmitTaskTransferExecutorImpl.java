/*
 * @(#)2014-11-2 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.executor.transfer;

import com.wellsoft.pt.bpm.engine.executor.TaskTransferExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-11-2.1	zhulh		2014-11-2		Create
 * </pre>
 * @date 2014-11-2
 */
@Service
@Transactional
public class TransferSubmitTaskTransferExecutorImpl extends TaskTransferExecutor implements
        TransferSubmitTaskTransferExecutor {

}
