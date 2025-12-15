/*
 * @(#)2016-08-24 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.facade.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.service.BaseService;

import java.util.List;
import java.util.Map;

/**
 * Description: 通用组件服务类
 *
 * @author t
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-08-24.1	wujx		2016-08-24		Create
 * </pre>
 * @date 2016-08-24
 */
public interface AppCommonComponentService extends BaseService {

    /**
     * 图片库-获取文件夹
     *
     * @param folderID         顶级文件夹ID
     * @param projRelativePath 项目相对路径
     * @return
     */
    @Deprecated
    public List<TreeNode> getPicutreLibFolder(String mongoFolderID, String projRelativePath);

    /**
     * 图片库-通过文件夹ID获取文件
     *
     * @param folderID
     * @param folderType
     * @return
     */
    @Deprecated
    public List<Map<String, Object>> getPicutreLibFilesByFolderID(String folderID, int folderType);
}
