/*
 * @(#)Dec 27, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Dec 27, 2017.1	zhulh		Dec 27, 2017		Create
 * </pre>
 * @date Dec 27, 2017
 */
public interface FileActionExecutor<P extends FileActionParam, R extends FileActionResult> {

    R execute(P param);

}
