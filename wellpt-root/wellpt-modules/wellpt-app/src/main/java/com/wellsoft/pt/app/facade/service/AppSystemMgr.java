/*
 * @(#)2016-05-09 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.facade.service;

import com.wellsoft.context.component.select2.Select2QueryApi;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.app.bean.AppSystemBean;
import com.wellsoft.pt.app.entity.AppSystem;

import java.util.Collection;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author t
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-05-09.1	t		2016-05-09		Create
 * </pre>
 * @date 2016-05-09
 */
public interface AppSystemMgr extends BaseService, Select2QueryApi {

    /**
     * 获取
     *
     * @param uuid
     * @return
     */
    AppSystemBean getBean(String uuid);

    /**
     * 保存
     *
     * @param bean
     * @return
     */
    AppSystem saveBean(AppSystemBean bean);

    /**
     * 删除
     *
     * @param uuid
     * @return
     */
    void remove(String uuid);

    /**
     * 批量删除
     *
     * @param uuid
     * @return
     */
    void removeAll(Collection<String> uuids);

    List<TreeNode> getTreeByAppSystemId(String id);

    List<TreeNode> getTreeByAppSystemUuid(String uuid);

    /**
     * 获取指定单位id下的产品集成树
     *
     * @param systemUnitId
     * @return
     */
    List<TreeNode> getTreeBySystemUnitId(String systemUnitId);

    List<TreeNode> getSystemModuleAppTreeBySystemUnitId(String systemUnitId);
}
