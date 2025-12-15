/*
 * @(#)2018年4月9日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.workflow.dao.FlowOpinionCategoryDao;
import com.wellsoft.pt.workflow.entity.FlowOpinionCategory;

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
 * 2018年4月9日.1	chenqiong		2018年4月9日		Create
 * </pre>
 * @date 2018年4月9日
 */
public interface FlowOpinionCategoryService extends JpaService<FlowOpinionCategory, FlowOpinionCategoryDao, String> {

    /**
     * 如何描述该方法
     *
     * @param example
     * @return
     */
    List<FlowOpinionCategory> findByExample(FlowOpinionCategory example);

    /**
     * 如何描述该方法
     *
     * @param example
     * @param order
     * @return
     */
    List<FlowOpinionCategory> findByExample(FlowOpinionCategory example, String order);

    /**
     * 获取用户办理意见分类，不包含最近使用分类
     *
     * @param userId
     * @return
     */
    List<FlowOpinionCategory> getUserOpinionCategoriesWithoutRecentCategory(String userId);

    /**
     * 获取用户办理意见分类，不包含最近使用分类
     *
     * @param userId
     * @param name   分类名称
     * @return
     */
    FlowOpinionCategory getUserOpinionCategoriesWithoutRecentCategory(String userId, String name);


    TreeNode getFlowOpinionCategoryTreeByBusinessAppDataDic(Boolean fetchOpinionCategory);

    @Override
    void deleteByUuids(List<String> strings);

    void deleteCategryAndOpinion(String uuid);

    @Override
    void save(FlowOpinionCategory entity);
}
