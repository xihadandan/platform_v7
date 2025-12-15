/*
 * @(#)2015-6-18 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.cg.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.cg.entity.CodeGeneratorConfig;

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
 * 2015-6-18.1	zhulh		2015-6-18		Create
 * </pre>
 * @date 2015-6-18
 */
public interface CodeGeneratorConfigService extends BaseService {

    /**
     * 如何描述该方法
     *
     * @param uuid
     * @return
     */
    CodeGeneratorConfig get(String uuid);

    /**
     * 如何描述该方法
     *
     * @param bean
     */
    void save(CodeGeneratorConfig codeGeneratorConfig);

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    void remove(String uuid);

    /**
     * 如何描述该方法
     *
     * @param uuids
     */
    void removeAll(Collection<String> uuids);

    /**
     * 获取实体类
     *
     * @return
     */
    List<TreeNode> getEntity(String a);

    /**
     * 获取数据库所有表格
     *
     * @param s
     * @return
     */
    List getTables(String s);

    /**
     * 获取实体类
     *
     * @return
     */
    List<TreeNode> getDyforms(String uuid);

    /**
     * 获取流程定义
     *
     * @param s
     * @return
     */
    List getFlows(String s);

}
