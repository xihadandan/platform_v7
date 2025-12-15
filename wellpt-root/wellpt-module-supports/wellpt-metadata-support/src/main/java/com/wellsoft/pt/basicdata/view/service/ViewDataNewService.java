/*
 * @(#)2013-3-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.view.service;

import java.util.List;

/**
 * Description: 视图数据service类
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-15.1	Administrator		2013-3-15		Create
 * </pre>
 * @date 2013-3-15
 */
public interface ViewDataNewService {

    public abstract List getViewById();

    /**
     *
     * 更改公告的置顶状态
     *
     * @param fmUuid
     * @return
     */
    //public String changeGGTopStatus(String fmUuid, String status);

}
