/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.org.dao.MultiOrgOptionDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgOption;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月4日.1	chenqiong		2018年4月4日		Create
 * </pre>
 * @date 2018年4月4日
 */
public interface MultiOrgOptionService extends JpaService<MultiOrgOption, MultiOrgOptionDao, String> {

    List<MultiOrgOption> queryOrgOptionListBySystemUnitId(String systemUnitId, boolean onlyShow);

    MultiOrgOption getById(String optId);

    Select2QueryData loadSelectData(Select2QueryInfo queryInfo);

    Select2QueryData loadSelectDataNoId(Select2QueryInfo queryInfo);

    List<MultiOrgOption> getOrgOptionsByIds(String[] ids);

    public abstract String getOptionStyle(String id);
}
