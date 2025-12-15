/*
 * @(#)Jan 8, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.action;

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
 * Jan 8, 2018.1	zhulh		Jan 8, 2018		Create
 * </pre>
 * @date Jan 8, 2018
 */
public interface FileActionManager {

    FileAction getFileAction(String id);

    /**
     * 如何描述该方法
     *
     * @return
     */
    List<String> getAllActionIds();

}
