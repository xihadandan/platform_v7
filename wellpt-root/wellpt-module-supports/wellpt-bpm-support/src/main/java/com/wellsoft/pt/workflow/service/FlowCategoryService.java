/*
 * @(#)2012-12-3 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.workflow.bean.FlowCategoryBean;
import com.wellsoft.pt.workflow.dao.FlowCategoryDao;
import com.wellsoft.pt.workflow.entity.FlowCategory;

import java.util.Collection;
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
 * 2012-12-3.1	zhulh		2012-12-3		Create
 * </pre>
 * @date 2012-12-3
 */
public interface FlowCategoryService extends JpaService<FlowCategory, FlowCategoryDao, String> {

    FlowCategory saveAndPublishData(FlowCategory entity);

    FlowCategory getOne(String uuid);

    @Override
    void delete(String uuid);

    int deleteWhenNotUsed(String uuid);

    FlowCategoryBean getBean(String uuid);

    String saveBean(FlowCategoryBean bean);

    List<FlowCategory> saveAll(Collection<FlowCategory> entities);

    void remove(FlowCategory entity);

    void removeAll(Collection<FlowCategory> entities);

    void removeByPk(String uid);

    void removeAllByPk(Collection<String> uids);

    TreeNode getAsTreeAsyncByUnitId(String systemUnitId);

    String generateFlowCategoryCode();

    FlowCategory getByCode(String code);

    List<FlowCategory> getAll();

    List<FlowCategory> getAllBySystemUnitIds();

    List<FlowCategory> getAllBySystemUnitIdsLikeName(String name);

    /**
     * 如何描述该方法
     *
     * @return
     */
    List<FlowCategory> getTopLevel();

}
