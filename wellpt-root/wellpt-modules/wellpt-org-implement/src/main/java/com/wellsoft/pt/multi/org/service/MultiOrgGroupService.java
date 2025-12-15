/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.org.dao.MultiOrgGroupDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgGroup;

import java.util.List;

/**
 * Description:
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
public interface MultiOrgGroupService extends JpaService<MultiOrgGroup, MultiOrgGroupDao, String> {

    /**
     * 如何描述该方法
     *
     * @param groupId
     * @return
     */
    MultiOrgGroup getById(String groupId);

    /**
     * 如何描述该方法
     *
     * @param type
     * @return
     */
    List<MultiOrgGroup> queryGroupByType(int type);

    List<MultiOrgGroup> getGroupsByIds(List<String> ids);

    public long countBySystemUnitIds(List<String> systemUnitIds);
}
