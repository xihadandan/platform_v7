/*
 * @(#)Jan 3, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

import com.wellsoft.pt.dms.file.action.EditFileAction;
import com.wellsoft.pt.dms.file.executor.EditFileActionExecutor.EditFileActionParam;
import com.wellsoft.pt.dms.file.executor.EditFileActionExecutor.EditFileActionResult;
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
public class EditFileActionExecutor extends AbstractFileActionExecutor<EditFileActionParam, EditFileActionResult> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.executor.FileActionExecutor#execute(com.wellsoft.pt.dms.file.executor.FileActionParam)
     */
    @Override
    public EditFileActionResult execute(EditFileActionParam param) {
        // TODO Auto-generated method stub
        return null;
    }

    public static class EditFileActionParam extends FileActionParam<EditFileAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 6629180137569237449L;

    }

    public static class EditFileActionResult extends FileActionResult<EditFileAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -5492627918949886385L;

    }

}
