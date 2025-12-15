/*
 * @(#)Jan 5, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

import com.wellsoft.pt.dms.file.action.FileAction;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Jan 5, 2018.1	zhulh		Jan 5, 2018		Create
 * </pre>
 * @date Jan 5, 2018
 */
public class FileActionExecutorFactory {

    @Autowired
    private List<FileAction> fileActions;

    private Map<String, FileAction> fileActionMap = new HashMap<String, FileAction>();

    @Autowired
    private List<FileActionExecutor> fileActionExecutors;
    private Map<Class<?>, FileActionExecutorMethod> fileActionExecutorMap = new HashMap<Class<?>, FileActionExecutorMethod>();

    public static final FileActionExecutor<FileActionParam, FileActionResult> getFileActionExecutor(String action) {
        return null;
    }

}
