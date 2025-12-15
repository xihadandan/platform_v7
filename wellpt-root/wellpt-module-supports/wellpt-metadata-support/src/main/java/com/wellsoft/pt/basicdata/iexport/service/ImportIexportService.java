package com.wellsoft.pt.basicdata.iexport.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.basicdata.iexport.protos.ProtoData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataDifference;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;

/**
 * @author yt
 * @title: ImportIexportService
 * @date 2020/8/13 14:38
 */
public interface ImportIexportService extends BaseService {

    /**
     * 导出数据
     *
     * @param exportIds
     * @return
     */
    ProtoData.ProtoDataList getExportData(String[] exportIds);


    /**
     * 导出tree
     *
     * @param uuid
     * @param type
     * @return
     */
    List<TreeNode> getExportTree(String uuid, String type);

    /**
     * 导入数据
     *
     * @param inputstream
     * @param importIds
     */
    void importData(InputStream inputstream, String importIds) throws Exception;

    /**
     * 导入tree
     *
     * @param inputstream
     * @return
     */
    List<TreeNode> getImportTree(InputStream inputstream) throws Exception;

    /**
     * 比较 不同的详细信息
     *
     * @param inputstream
     * @param uuid
     * @return
     */
    IexportDataDifference getDifference(InputStream inputstream, String uuid);

    List<TreeNode> exportDataDefAsTree(List<String> uuid, String type);

    List<TreeNode> exportDataDefAsTree(List<String> uuid, List<String> type);

    void exportDataDefinition(List<String> typeUuids, HttpServletResponse response);

    IexportDataProvider getIexportDataProvider(String type);
}
