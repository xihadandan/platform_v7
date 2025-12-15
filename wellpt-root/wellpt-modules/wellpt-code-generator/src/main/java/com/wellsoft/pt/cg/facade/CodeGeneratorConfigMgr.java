/*
 * @(#)2015-6-18 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.cg.facade;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.cg.bean.CodeGeneratorConfigBean;

import java.util.Collection;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-18.1	zhulh		2015-6-18		Create
 * </pre>
 * @date 2015-6-18
 */
public interface CodeGeneratorConfigMgr extends BaseService {

    CodeGeneratorConfigBean getBean(String uuid);

    void saveBean(CodeGeneratorConfigBean bean);

    void remove(String uuid);

    void removeAll(Collection<String> uuids);

}
