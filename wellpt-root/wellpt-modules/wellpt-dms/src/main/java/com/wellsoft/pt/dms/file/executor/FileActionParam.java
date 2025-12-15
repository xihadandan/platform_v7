/*
 * @(#)Dec 27, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.pt.dms.file.action.FileAction;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

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
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class FileActionParam<T extends FileAction> extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -4180667717236712336L;

    private boolean withoutPermission = false;

    public boolean isWithoutPermission() {
        return withoutPermission;
    }

    public void setWithoutPermission(boolean withoutPermission) {
        this.withoutPermission = withoutPermission;
    }

}
