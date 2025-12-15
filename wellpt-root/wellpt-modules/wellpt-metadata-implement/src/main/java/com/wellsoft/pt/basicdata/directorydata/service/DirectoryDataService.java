package com.wellsoft.pt.basicdata.directorydata.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.basicdata.directorydata.bean.DataBean;
import com.wellsoft.pt.basicdata.directorydata.bean.DirectoryBean;
import com.wellsoft.pt.basicdata.directorydata.entity.Data;
import com.wellsoft.pt.basicdata.directorydata.entity.Directory;

import java.util.List;

/**
 * Description: 目录数据服务，包括增删改查目录树，增删改查数据
 *
 * @author huanglinchuan
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-1-13.1	huanglinchuan		2015-1-13		Create
 * </pre>
 * @date 2015-1-13
 */
public interface DirectoryDataService {

    public String addDirectory(DirectoryBean directoryBean);

    public void updateDirectory(DirectoryBean directoryBean);

    public void deleteDirectory(String directoryUuid, boolean containSubDirectories);

    public boolean checkDirectoryNameExists(String directoryName, Directory parentDirectory);

    public List<TreeNode> loadDirectoryTree(String parentDirectoryUuid);

    public List<TreeNode> loadDirectoryTreeByParentUuid(String id, String parentDirectoryUuid);

    public List<TreeNode> loadDirecotryTreeByUserId(String parentDirectoryUuid, String userId);

    public Directory getDirectoryByUuid(String uuid);

    public Data getDataByUuid(String uuid);

    public void addData(DataBean dataBean);

    public void updateData(DataBean dataBean);

    public void deleteData(String dataUuid);

    public List<TreeNode> loadDirectoryDataTree(String parentDirectoryUuid);

    public List<TreeNode> loadDirecotryDataTreeByUserId(String parentDirectoryUuid, String userId);

    public List<Directory> getTopDirectory();

    public List<Data> getAllChildDataByParentId(String parentId);

    public List<Data> getAllDatas(String diectoryUuid);

    public List<TreeNode> getAllChildDataTree(String id, String directoryUuid);

    public List<TreeNode> getAllChildDataTreeByUserId(String id, String directoryUuid);

    public List<TreeNode> getAllChildDirectoryTree(String id, String directoryUuid);

    public List<TreeNode> getAllChildDirectoryTreeByUserId(String id, String directoryUuid);

    public List<TreeNode> getTopTreeNodeDirectory(String parentUuid);
}
