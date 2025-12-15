/*
 * @(#)2021年1月8日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.log;

import com.wellsoft.pt.log.entity.BusinessDetailsLog;
import com.wellsoft.pt.log.entity.BusinessOperationLog;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年1月8日.1	zhongzh		2021年1月8日		Create
 * </pre>
 * @date 2021年1月8日
 */
public class LogEvent implements Serializable {

    public static final String TAGS_LOG = "LOG";

    //public static final String TAGS_WORKFLOW = "WORKFLOW";

    //public static final String TAGS_SECURITY = "SECURITY";

    //public static final String TAGS_FILE = "FILE";

    //public static final String TAGS_ORG = "ORG";

    // public static final String TAGS_APP = "APP";
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    private Map<String, Object> params;

    private BusinessOperationLog source;

    private List<BusinessDetailsLog> details;

    /**
     * @param source
     * @param params
     */
    public LogEvent(BusinessOperationLog source, Map<String, Object> params) {
        this.source = source;
        this.params = params;
    }

    /**
     * @return the params
     */
    public Map<String, Object> getParams() {
        return params;
    }

    /**
     * @return the source
     */
    public BusinessOperationLog getSource() {
        return source;
    }

    public List<BusinessDetailsLog> getDetails() {
        return details;
    }

    public void setDetails(List<BusinessDetailsLog> details) {
        this.details = details;
    }
}
