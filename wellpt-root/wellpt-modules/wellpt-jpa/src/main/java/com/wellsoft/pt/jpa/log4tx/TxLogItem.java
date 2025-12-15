/*
 * @(#)2019年11月8日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.log4tx;

import java.io.Serializable;

/**
 * Description: 记录项
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年11月8日.1	zhongzh		2019年11月8日		Create
 * </pre>
 * @date 2019年11月8日
 */
public class TxLogItem implements Serializable {

    private static final long serialVersionUID = 7338699611920165938L;
    /**
     * 时间戳
     */
    public long timestamp;
    /**
     * 日志内容
     */
    public String content;
    /**
     * 日志级别
     */
    public TxLogLevel level;

}
