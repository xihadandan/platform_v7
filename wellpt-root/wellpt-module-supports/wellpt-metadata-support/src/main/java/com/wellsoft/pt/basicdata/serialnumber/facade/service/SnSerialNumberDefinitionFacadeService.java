/*
 * @(#)7/12/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.facade.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.basicdata.serialnumber.dto.SnSerialNumberDefinitionDto;

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
 * 7/12/22.1	zhulh		7/12/22		Create
 * </pre>
 * @date 7/12/22
 */
public interface SnSerialNumberDefinitionFacadeService extends Facade {
    /**
     * 获取流水号定义
     *
     * @param uuid
     * @return
     */
    SnSerialNumberDefinitionDto getDto(String uuid);

    /**
     * 根据ID获取流水号定义
     *
     * @param id
     * @return
     */
    SnSerialNumberDefinitionDto getById(String id);

    /**
     * 获取所有流水号定义
     *
     * @return
     */
    List<SnSerialNumberDefinitionDto> listAll();

    /**
     * 保存流水号定义
     *
     * @param dto
     */
    void saveDto(SnSerialNumberDefinitionDto dto);

    /**
     * 删除没用的流水号定义
     *
     * @param uuid
     * @return
     */
    int deleteWhenNotUsed(String uuid);

    /**
     * 异步加载流水号定义选择树
     *
     * @param treeId
     * @return
     */
    List<TreeNode> asyncLoadSerialNumberTree(String treeId);

    /**
     * 加载流水号定义选择树
     *
     * @return
     */
    List<TreeNode> loadSerialNumberTree();

    /**
     * 根据流水号分类UUID或流水号定义ID获取流水号定义列表
     *
     * @param categoryUuidOrIds
     * @return
     */
    List<SnSerialNumberDefinitionDto> listByCategoryUuidOrId(List<String> categoryUuidOrIds);


}
