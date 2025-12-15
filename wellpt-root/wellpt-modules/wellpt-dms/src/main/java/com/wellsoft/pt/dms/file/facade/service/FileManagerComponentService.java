/*
 * @(#)Jan 5, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.facade.service;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.dms.entity.DmsFolderEntity;

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
 * Jan 5, 2018.1	zhulh		Jan 5, 2018		Create
 * </pre>
 * @date Jan 5, 2018
 */
public interface FileManagerComponentService extends BaseService {

    /**
     * 列出文件库
     *
     * @return
     */
    List<DmsFolderEntity> listFileLibrary();

    TreeNode getFolderTree();

    /**
     * 数据展示
     *
     * @param queryInfo
     * @return
     */
    Select2QueryData getDataViewSelectData(Select2QueryInfo queryInfo);

    Select2QueryData getDmsRoleSelectData(Select2QueryInfo queryInfo);

    /**
     * 默认数据展示
     *
     * @param queryInfo
     * @return
     */
    Select2QueryData getDefaultDataViewSelectData(Select2QueryInfo queryInfo);

}
