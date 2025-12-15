/*
 * @(#)Jan 3, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

import com.wellsoft.pt.dms.file.action.CheckInFileAction;
import com.wellsoft.pt.dms.file.executor.CheckInFileActionExecutor.CheckInFileActionParam;
import com.wellsoft.pt.dms.file.executor.CheckInFileActionExecutor.CheckInFileActionResult;
import org.springframework.stereotype.Component;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Jan 3, 2018.1	zhulh		Jan 3, 2018		Create
 * </pre>
 * @date Jan 3, 2018
 */
@Component
public class CheckInFileActionExecutor extends
        AbstractFileActionExecutor<CheckInFileActionParam, CheckInFileActionResult> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.executor.FileActionExecutor#execute(com.wellsoft.pt.dms.file.executor.FileActionParam)
     */
    @Override
    public CheckInFileActionResult execute(CheckInFileActionParam param) {
        // TODO Auto-generated method stub
        return null;
    }

    public static class CheckInFileActionParam extends FileActionParam<CheckInFileAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 4189326318119885336L;

    }

    public static class CheckInFileActionResult extends FileActionResult<CheckInFileAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 1086224991863591015L;

    }

}
