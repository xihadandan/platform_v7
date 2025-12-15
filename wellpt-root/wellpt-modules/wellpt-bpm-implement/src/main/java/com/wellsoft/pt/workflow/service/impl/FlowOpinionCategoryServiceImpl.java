/*
 * @(#)2018年4月9日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.basicdata.datadict.facade.service.CdDataDictionaryFacadeService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.workflow.dao.FlowOpinionCategoryDao;
import com.wellsoft.pt.workflow.entity.FlowOpinionCategory;
import com.wellsoft.pt.workflow.service.FlowOpinionCategoryService;
import com.wellsoft.pt.workflow.service.FlowOpinionService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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
@Service
public class FlowOpinionCategoryServiceImpl extends
        AbstractJpaServiceImpl<FlowOpinionCategory, FlowOpinionCategoryDao, String> implements
        FlowOpinionCategoryService {

    private final String GET_USER_OPINION_CATEGORIES_WITHOUT_RECENT_CATEGORY = "from FlowOpinionCategory t where t.creator = :creator and t.code <> :recentCategoryCode order by t.code asc";
    @Autowired
    private CdDataDictionaryFacadeService dataDictionaryService;
    @Autowired
    private FlowOpinionService flowOpinionService;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowOpinionCategoryService#findByExample(com.wellsoft.pt.workflow.entity.FlowOpinionCategory)
     */
    @Override
    public List<FlowOpinionCategory> findByExample(FlowOpinionCategory example) {
        return dao.listByEntity(example);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowOpinionCategoryService#findByExample(com.wellsoft.pt.workflow.entity.FlowOpinionCategory, java.lang.String)
     */
    @Override
    public List<FlowOpinionCategory> findByExample(FlowOpinionCategory example, String order) {
        return dao.listByEntityAndPage(example, null, order);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowOpinionCategoryService#getUserOpinionCategoriesWithoutRecentCategory(java.lang.String)
     */
    @Override
    public List<FlowOpinionCategory> getUserOpinionCategoriesWithoutRecentCategory(String userId) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("creator", userId);
        values.put("recentCategoryCode", "999");
        return this.dao.listByHQL(GET_USER_OPINION_CATEGORIES_WITHOUT_RECENT_CATEGORY, values);
    }

    @Override
    public FlowOpinionCategory getUserOpinionCategoriesWithoutRecentCategory(String userId, String name) {
        FlowOpinionCategory entity = new FlowOpinionCategory();
        entity.setCreator(userId);
        entity.setName(name);
        List<FlowOpinionCategory> entities = this.dao.listByEntity(entity);
        if (entities.size() > 0) {
            return entities.get(0);
        }
        return null;
    }

    @Override
    public TreeNode getFlowOpinionCategoryTreeByBusinessAppDataDic(Boolean fetchOpinionCategory) {
//        CdDataDictionaryEntity dataDictionary = dataDictionaryService.getDataDictionaryByCode(
//                "PT_BUSINESS_APP_CATEGORY");//查询行业应用分类
        TreeNode node = dataDictionaryService.listItemAsTreeByDictionaryCode("PT_BUSINESS_APP_CATEGORY");
        // TreeNode node = dataDictionaryService.getAllAsTree(dataDictionary.getUuid());
        if (BooleanUtils.isTrue(fetchOpinionCategory)) {
            cascadeQueryFlowOptionCategory(node);
            return node;
        } else {
            return node;
        }
    }

    @Override
    @Transactional
    public void deleteCategryAndOpinion(String uuid) {
        this.dao.delete(uuid);
        this.flowOpinionService.deleteByCategoryUuid(uuid);
    }

    public void cascadeQueryFlowOptionCategory(TreeNode node) {
        List<TreeNode> children = node.getChildren();
        if (CollectionUtils.isNotEmpty(children)) {
            for (TreeNode n : children) {
                cascadeQueryFlowOptionCategory(n);
            }
        }

        FlowOpinionCategory example = new FlowOpinionCategory();
        example.setBusinessFlag(node.getId());
        List<FlowOpinionCategory> categories = this.findByExample(example, " code asc");
        if (CollectionUtils.isNotEmpty(categories)) {
            for (FlowOpinionCategory c : categories) {
                TreeNode cn = new TreeNode(c.getUuid(), c.getName(), null);
                cn.setType("-1");
                node.getChildren().add(cn);
            }
        }

    }
}
