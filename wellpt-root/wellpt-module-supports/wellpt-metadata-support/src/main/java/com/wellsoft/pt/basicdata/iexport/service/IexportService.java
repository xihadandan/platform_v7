/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataDifference;
import com.wellsoft.pt.basicdata.iexport.table.ExportTable;
import com.wellsoft.pt.basicdata.iexport.visitor.ExportVisitor;

import java.io.InputStream;
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
 * 2015-6-16.1	zhulh		2015-6-16		Create
 * </pre>
 * @date 2015-6-16
 */
public interface IexportService extends BaseService {

    List<TreeNode> getExportTree(String uuid, String type);

    /**
     * 如何描述该方法
     *
     * @param in
     * @return
     */
    void importData(InputStream input, boolean newVer, String importIds, boolean isProtobuf);

    void importData(String fileId, boolean newVer, String importIds);

    ExportVisitor getExportData(String[] exportIds, String id, String type);

    List<TreeNode> getImportTree(String fileId);

    /**
     * 获取数据不同的信息
     *
     * @param fileId
     * @param uuid
     * @param type
     * @return
     */
    IexportDataDifference getDifference(String fileId, String uuid, String type);


    List<TreeNode> exportTableDataAsTree(ExportTable table);

}
