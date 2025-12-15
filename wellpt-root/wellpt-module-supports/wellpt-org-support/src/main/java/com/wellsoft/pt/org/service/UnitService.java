/*
 * @(#)2013-2-18 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.support.KeyValuePair;
import com.wellsoft.pt.org.bean.UnitBean;
import com.wellsoft.pt.org.entity.Unit;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-2-18.1	zhulh		2013-2-18		Create
 * </pre>
 * @date 2013-2-18
 */
public interface UnitService {

    Unit getById(String unitId);

    /**
     * Description how to use this method
     *
     * @param uuid
     * @return
     */
    UnitBean getBean(String uuid);

    /**
     * Description how to use this method
     *
     * @param bean
     */
    void saveBean(UnitBean bean);

    /**
     * Description how to use this method
     *
     * @param uuid
     */
    void remove(String uuid);

    /**
     * Description how to use this method
     *
     * @param excludeUuid
     * @return
     */
    TreeNode getAsTree(String excludeUuid);

    /**
     * @param userUuid
     * @return
     */
    List<Unit> getUserUnits(String userUuid);

    /**
     * 返回业务类型的数据字典
     *
     * @param bussinessType
     * @return
     */
    KeyValuePair getBusinessTypes(String bussinessType);

    /**
     * 如何描述该方法
     *
     * @param id
     * @return
     */
    List<Unit> getLeafUnits(String id);
}
