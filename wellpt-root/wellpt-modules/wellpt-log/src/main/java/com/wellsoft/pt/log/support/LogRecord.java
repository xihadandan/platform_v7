/*
 * @(#)2013-10-13 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.log.support;

import java.io.Serializable;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-10-13.1	zhulh		2013-10-13		Create
 * </pre>
 * @date 2013-10-13
 */
public class LogRecord implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 7338699611920165938L;

    public long lineNumber;

    public String content;

}
