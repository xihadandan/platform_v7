/*
 * @(#)2019年1月17日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.printtemplate.dao;

import com.wellsoft.pt.basicdata.printtemplate.entity.PrintContents;
import com.wellsoft.pt.jpa.dao.JpaDao;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年1月17日.1	zhongzh		2019年1月17日		Create
 * </pre>
 * @date 2019年1月17日
 */
public interface PrintContentsDao extends JpaDao<PrintContents, String> {

    public abstract List<PrintContents> listByTemplateUuid(String templateUuid);

}
