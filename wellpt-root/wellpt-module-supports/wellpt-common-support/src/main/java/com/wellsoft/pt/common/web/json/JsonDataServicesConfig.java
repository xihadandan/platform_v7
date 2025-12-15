/*
 * @(#)2013-12-25 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.web.json;

import java.util.List;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-25.1	zhulh		2013-12-25		Create
 * </pre>
 * @date 2013-12-25
 */
public interface JsonDataServicesConfig {

    List<String> getAnonymousServices();

    Set<String> getLoginedAuthenticatedServices();

}
