package com.wellsoft.pt.basicdata.directorydata.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.directorydata.bean.DataBean;
import com.wellsoft.pt.basicdata.directorydata.bean.DirectoryBean;
import com.wellsoft.pt.basicdata.directorydata.dao.DataDao;
import com.wellsoft.pt.basicdata.directorydata.dao.DirectoryDao;
import com.wellsoft.pt.basicdata.directorydata.entity.Data;
import com.wellsoft.pt.basicdata.directorydata.entity.Directory;
import com.wellsoft.pt.basicdata.directorydata.service.DirectoryDataService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.acl.service.AclService;
import com.wellsoft.pt.security.acl.support.QueryInfo;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Description: 目录数据服务实现类
 *
 * @author huanglinchuan
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-1-22.1	huanglinchuan		2015-1-22		Create
 * </pre>
 * @date 2015-1-22
 */
@Service
@Transactional
public class DirectoryDataServiceImpl extends BaseServiceImpl implements DirectoryDataService {

    @Autowired
    DirectoryDao directoryDao;
    @Autowired
    DataDao dataDao;
    @Autowired
    AclService aclService;
    @Autowired
    OrgApiFacade orgApiFacade;

    @Override
    @SuppressWarnings("all")
    public void updateDirectory(DirectoryBean directoryBean) {
        Directory directory = (Directory) directoryBean;
        Directory directoryFromDb = directoryDao.get(directory.getUuid());
        if (directoryFromDb != null && directoryFromDb.getName().equals(directory.getName()) && !directoryFromDb.getUuid().equals(directory.getUuid())) {
            throw new RuntimeException("directory name exists within same parent directory.");
        }
        directoryFromDb.setDirectoryTopUuid(directory.getDirectoryTopUuid());
        directoryFromDb.setName(directory.getName());
        directoryFromDb.setJsonData(directory.getJsonData());
        directoryFromDb.setOrderNo(directory.getOrderNo());
        directoryFromDb.setParent(directory.getParent());
        directoryFromDb.setModifyTime(new Timestamp(System.currentTimeMillis()));
        directoryFromDb.setModifier(SpringSecurityUtils.getCurrentUserId());

        directoryDao.merge(directoryFromDb);

        // aclService.removePermission(Directory.class,
        // directoryFromDb.getUuid());
        // Map<Permission, List<String>> aclConfigInfo =
        // directoryBean.getAclConfigInfo();
        // for (Map.Entry<Permission, List<String>> entry :
        // aclConfigInfo.entrySet()) {
        // Permission permission = entry.getKey();
        // List<String> sids = entry.getValue();
        // for (String sid : sids) {
        // aclService.addPermission(Directory.class, directory.getUuid(),
        // permission, sid);
        // }
        // }
    }

    @Override
    @SuppressWarnings("all")
    public void deleteDirectory(String directoryUuid, boolean containSubDirectories) {
        Directory directoryFromDb = directoryDao.findByUuid(directoryUuid);
        if (directoryFromDb == null) {
            throw new RuntimeException("Cannot find directory with uuid '" + directoryUuid + "'");
        }
        if (directoryFromDb.getChildren() != null && containSubDirectories) {
            for (Directory sub : directoryFromDb.getChildren()) {
                deleteDirectory(sub.getUuid(), containSubDirectories);
            }
        }
        aclService.removePermission(Directory.class, directoryFromDb.getUuid());
        directoryDao.delete(directoryFromDb);
    }

    @Override
    @SuppressWarnings("all")
    public boolean checkDirectoryNameExists(String directoryName, Directory parentDirectory) {
        return directoryDao.findByUniqueKeys(directoryName, parentDirectory != null ? parentDirectory.getUuid() : null) != null;
    }

    @Override
    public List<TreeNode> loadDirectoryTree(String parentDirectoryUuid) {
        return loadDirecotryTreeByUserId(parentDirectoryUuid, SpringSecurityUtils.getCurrentUserId());
    }

    @Override
    public List<TreeNode> loadDirecotryTreeByUserId(String parentDirectoryUuid, String userId) {
        List<Directory> list = new ArrayList<Directory>();
        TreeNode parentNode = new TreeNode();
        // 查询所有根结点
        if (parentDirectoryUuid.equals(TreeNode.ROOT_ID)) {
            list = this.getTopDirectory();
        } else {
            Directory directory = this.directoryDao.get(parentDirectoryUuid);
            if (directory != null) {
                list = Lists.newArrayList(directory.getChildren());
            }
        }

        List<TreeNode> children = parentNode.getChildren();
        for (Directory directory : list) {
            TreeNode child = new TreeNode();
            child.setId(directory.getUuid());
            child.setName(directory.getName());
            DirectoryBean bean = new DirectoryBean();
            BeanUtils.copyProperties(directory, bean);
            child.setData(directory.getUuid());
            child.setIsParent(directory.getChildren().size() > 0);
            children.add(child);
        }
        return children;
    }

    private void getAllChildrenDirectories(List<TreeNode> nodes, TreeNode parentNode, Directory parentDirectory, List<Directory> allDirectoriesWithPermissions) {
        if (parentDirectory != null && parentDirectory.getChildren() != null) {
            for (Directory child : parentDirectory.getChildren()) {
                // if (allDirectoriesWithPermissions.contains(child)) {
                TreeNode node = new TreeNode(child.getUuid(), child.getName(), null);
                node.setIsParent(child.getChildren() != null && !child.getChildren().isEmpty());
                node.setData(child);
                nodes.add(node);
                parentNode.getChildren().add(node);
                // }
            }
        }
    }

    @Override
    public void addData(DataBean dataBean) {
        Data data = new Data();
        data.setJsonData(dataBean.getJsonData());
        data.setDirectory(dataBean.getDirectory());
        data.setName(dataBean.getName());
        data.setDirectoryTopUuid(dataBean.getDirectoryTopUuid());
        dataDao.save(data);
    }

    @Override
    public void updateData(DataBean dataBean) {
        Data data = (Data) dataBean;
        Data dataFromDb = dataDao.findByUuid(data.getUuid());
        dataFromDb.setDirectoryTopUuid(data.getDirectoryTopUuid());
        dataFromDb.setName(data.getName());
        dataFromDb.setJsonData(data.getJsonData());
        dataFromDb.setDirectory(directoryDao.findByUuid(dataBean.getDirectory().getUuid()));
        dataFromDb.setModifier(SpringSecurityUtils.getCurrentUserId());
        dataFromDb.setModifyTime(new Timestamp(System.currentTimeMillis()));
        dataDao.merge(dataFromDb);
    }

    @Override
    public void deleteData(String dataUuid) {
        dataDao.delete(dataUuid);
    }

    @Override
    public List<TreeNode> loadDirectoryDataTree(String parentDirectoryUuid) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 根据父节点uuid获得当前用户的带权限的目录数据树
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.directorydata.service.DirectoryDataService#loadDirecotryDataTreeByUserId(java.lang.String, java.lang.String)
     */
    @Override
    public List<TreeNode> loadDirecotryDataTreeByUserId(String parentDirectoryUuid, String userId) {
        List<TreeNode> nodes = new ArrayList<TreeNode>();
        Directory parent = directoryDao.findByUuid(parentDirectoryUuid);
        if (parent == null) {
            throw new RuntimeException("Cannot find directory with uuid '" + parentDirectoryUuid + "'");
        }
        List<Permission> permissions = new ArrayList<Permission>();
        permissions.add(BasePermission.ADMINISTRATION);
        permissions.add(BasePermission.DELETE);
        permissions.add(BasePermission.READ);
        permissions.add(BasePermission.WRITE);

        Set<String> orgIds = orgApiFacade.getUserOrgIds(userId);
        List<String> sids = new ArrayList<String>(orgIds);
        List<Directory> allDirectoriesWithPermissions = aclService.getAll(Directory.class, permissions, sids);
        TreeNode parentNode = new TreeNode(parentDirectoryUuid, parent.getName(), null);
        nodes.add(parentNode);
        getAllChildrenDirectoryAndData(nodes, parentNode, parent, allDirectoriesWithPermissions, sids);

        return nodes;
    }

    /**
     * 获得某个目录节点下的所有的子节点的目录以及数据集合
     *
     * @param nodes
     * @param parentNode
     * @param parentDirectory
     * @param allDirectoriesWithPermissions
     * @param sids
     */
    private void getAllChildrenDirectoryAndData(List<TreeNode> nodes, TreeNode parentNode, Directory parentDirectory,
                                                List<Directory> allDirectoriesWithPermissions, List<String> sids) {
        if (parentDirectory != null && parentDirectory.getChildren() != null) {
            for (Directory child : parentDirectory.getChildren()) {
                if (allDirectoriesWithPermissions.contains(child)) {
                    TreeNode node = new TreeNode(child.getUuid(), child.getName(), null);
                    node.setIsParent(child.getChildren() != null && !child.getChildren().isEmpty());
                    node.setData(child.getUuid());
                    nodes.add(node);
                    parentNode.getChildren().add(node);
                }
            }
        }
        List<Permission> permissions = new ArrayList<Permission>();
        permissions.add(BasePermission.ADMINISTRATION);
        permissions.add(BasePermission.DELETE);
        permissions.add(BasePermission.READ);
        permissions.add(BasePermission.WRITE);

        QueryInfo<Data> queryInfo = new QueryInfo<Data>();
        queryInfo.addQueryParams("o.directory.uuid", parentDirectory.getUuid());
        List<QueryItem> dataItems = aclService.queryForItem(Data.class, queryInfo, permissions, sids);

    }

    @Override
    public List<Directory> getTopDirectory() {

        return this.directoryDao.getTopDirectory();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.directorydata.service.DirectoryDataService#getDataByUuid(java.lang.String)
     */
    @Override
    public Data getDataByUuid(String uuid) {
        // TODO Auto-generated method stub
        return dataDao.findByUuid(uuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.directorydata.service.DirectoryDataService#getAllChildDataByParentId(java.lang.String)
     */
    @Override
    public List<Data> getAllChildDataByParentId(String parentId) {
        List<Data> list = new ArrayList<Data>();
        if (parentId != null) {
            list = getAllDatas(parentId);
        }
        return list;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.directorydata.service.DirectoryDataService#getAllDatas(java.lang.String)
     */
    @Override
    public List<Data> getAllDatas(String directoryUuid) {

        // 设置权限集合
        // List<Permission> permissions = new ArrayList<Permission>();
        // permissions.add(BasePermission.ADMINISTRATION);
        // permissions.add(BasePermission.DELETE);
        // permissions.add(BasePermission.READ);
        // permissions.add(BasePermission.WRITE);
        //
        // String masks_str = "";
        // for (int i = 0; i < permissions.size(); i++) {
        // masks_str += String.valueOf((permissions.get(i)).getMask());
        // if (i < permissions.size() - 1) {
        // masks_str += ",";
        // }
        // }
        //
        // String currentUserId = SpringSecurityUtils.getCurrentUserId();
        // List<String> allRelatedGroupOrgs =
        // orgApiFacade.getAllRelatedGroupOrgs(currentUserId);
        // String sids_str = "'" + currentUserId + "'";
        // for (String groupOrg : allRelatedGroupOrgs) {
        // sids_str += ",'GROUP_" + groupOrg + "'";
        // }
        // Map<String, Object> values = new HashMap<String, Object>();
        // values.put("directoryUuid", "'" + directoryUuid + "'");
        // values.put("sids", sids_str);
        // values.put("masks", masks_str);
        //
        // List<Data> datas = nativeDao.namedQuery("dataQuery", values,
        // Data.class);
        List<Data> datas = dataDao.findByDirectoryUuid(directoryUuid);
        return datas;

    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.directorydata.service.DirectoryDataService#getAllChildDataTree(java.lang.String, java.lang.String)
     */
    @Override
    public List<TreeNode> getAllChildDataTree(String id, String directoryUuid) {
        // List<Permission> permissions = new ArrayList<Permission>();
        // permissions.add(BasePermission.ADMINISTRATION);
        // permissions.add(BasePermission.DELETE);
        // permissions.add(BasePermission.READ);
        // permissions.add(BasePermission.WRITE);
        //
        // String currentUserId = SpringSecurityUtils.getCurrentUserId();
        // List<String> sids =
        // orgApiFacade.getAllRelatedGroupOrgs(currentUserId);
        // List<Directory> list = aclService.getAll(Directory.class,
        // permissions, sids);

        List<Directory> list = new ArrayList<Directory>();
        // 查询所有根结点
        if (directoryUuid.equals(TreeNode.ROOT_ID)) {
            list = this.getTopDirectory();
        } else {
            Directory directory = this.directoryDao.get(directoryUuid);
            if (directory != null) {
                list = Lists.newArrayList(directory.getChildren());
            }
        }
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        for (Directory directory : list) {
            TreeNode dNode = new TreeNode();
            dNode.setId(directory.getUuid());
            dNode.setName(directory.getName());
            dNode.setData(directory.getJsonData());
            dNode.setNocheck(true);

            List<Directory> childDirectorys = directory.getChildren();
            if (childDirectorys.size() > 0) {
                List<TreeNode> cTreeNodes = new ArrayList<TreeNode>();
                for (Directory child : childDirectorys) {
                    TreeNode cNode = new TreeNode();
                    cNode.setId(child.getUuid());
                    cNode.setName(child.getName());
                    cNode.setData(child.getJsonData());
                    cNode.setNocheck(true);
                    List<TreeNode> childTreeNodes2 = new ArrayList<TreeNode>();
                    List<Data> datas = getAllDatas(child.getUuid());
                    for (Data cData : datas) {
                        TreeNode cnode = new TreeNode();
                        cnode.setId(cData.getUuid());
                        Long kcNum = 0L;
                        String unit = "个";
                        try {
                            JSONObject jo = new JSONObject(cData.getJsonData());
                            kcNum = jo.getLong("goodsNum");
                            unit = jo.getString("unit");
                        } catch (JSONException e) {
                            logger.error(ExceptionUtils.getStackTrace(e));
                        }

                        cnode.setName(cData.getName() + "<" + kcNum + unit + ">");
                        cnode.setData(cData.getJsonData());
                        childTreeNodes2.add(cnode);
                        cNode.setChildren(childTreeNodes2);
                        cNode.setIsParent(true);

                    }
                    cTreeNodes.add(cNode);
                    dNode.setChildren(cTreeNodes);
                }
            } else {

                List<TreeNode> childTreeNodes = new ArrayList<TreeNode>();
                List<Data> datas = getAllDatas(directory.getUuid());
                for (int i = 0; i < datas.size(); i++) {
                    TreeNode node = new TreeNode();
                    node.setId(datas.get(i).getUuid());
                    node.setName(datas.get(i).getName());
                    node.setData(datas.get(i).getJsonData());
                    childTreeNodes.add(node);
                    dNode.setChildren(childTreeNodes);
                    dNode.setIsParent(true);
                }
            }
            treeNodes.add(dNode);
        }
        return treeNodes;
    }

    @Override
    public List<TreeNode> getTopTreeNodeDirectory(String parentUuid) {
        List<Directory> directoryLists = this.getTopDirectory();
        List<TreeNode> parentTreeNode = new ArrayList<TreeNode>();
        for (Directory directory : directoryLists) {
            TreeNode child = new TreeNode();
            child.setId(directory.getUuid());
            child.setName(directory.getName());
            DirectoryBean bean = new DirectoryBean();
            BeanUtils.copyProperties(directory, bean);
            child.setData(bean);
            child.setIsParent(false);
            parentTreeNode.add(child);
        }
        return parentTreeNode;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.directorydata.service.DirectoryDataService#getDirectoryByUuid(java.lang.String)
     */
    @Override
    public Directory getDirectoryByUuid(String uuid) {
        return directoryDao.findByUuid(uuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.directorydata.service.DirectoryDataService#getAllChildDirectoryTree(java.lang.String, java.lang.String)
     */
    @Override
    public List<TreeNode> getAllChildDirectoryTree(String id, String directoryUuid) {

        // List<Permission> permissions = new ArrayList<Permission>();
        // permissions.add(BasePermission.ADMINISTRATION);
        // permissions.add(BasePermission.DELETE);
        // permissions.add(BasePermission.READ);
        // permissions.add(BasePermission.WRITE);
        //
        // String currentUserId = SpringSecurityUtils.getCurrentUserId();
        // List<String> sids =
        // orgApiFacade.getAllRelatedGroupOrgs(currentUserId);
        // List<Directory> list = aclService.getAll(Directory.class,
        // permissions, sids);

        List<Directory> list = new ArrayList<Directory>();
        // 查询所有根结点
        if (directoryUuid.equals(TreeNode.ROOT_ID)) {
            list = this.getTopDirectory();
        } else {
            Directory directory = this.directoryDao.get(directoryUuid);
            if (directory != null) {
                list = Lists.newArrayList(directory.getChildren());
            }
        }
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        for (Directory directory : list) {
            TreeNode dNode = new TreeNode();
            dNode.setId(directory.getUuid());
            dNode.setName(directory.getName());
            dNode.setData(directory.getJsonData());
            dNode.setNocheck(true);

            List<Directory> childDirectorys = directory.getChildren();
            getChildDirectory(dNode, childDirectorys);
            treeNodes.add(dNode);
        }
        return treeNodes;

    }

    public void getChildDirectory(TreeNode dNode, List<Directory> list) {
        if (list.size() > 0) {
            List<TreeNode> cTreeNodes = new ArrayList<TreeNode>();
            for (Directory child : list) {
                TreeNode cNode = new TreeNode();
                cNode.setId(child.getUuid());
                cNode.setName(child.getName());
                cNode.setData(child.getJsonData());
                cNode.setNocheck(true);
                cTreeNodes.add(cNode);
                dNode.setChildren(cTreeNodes);
                List<Directory> childDirectorys = child.getChildren();
                getChildDirectory(cNode, childDirectorys);
            }
        }
    }

    public void getChildDirectoryByUserId(TreeNode dNode, List<Directory> list) {
        if (list.size() > 0) {
            List<TreeNode> cTreeNodes = new ArrayList<TreeNode>();
            for (Directory child : list) {
                TreeNode cNode = new TreeNode();
                JSONObject jsonObject;
                try {
                    if (child.getJsonData() != null) {
                        jsonObject = new JSONObject(child.getJsonData());
                        String userId = jsonObject.getString("currentUserId");
                        if (userId.equals(SpringSecurityUtils.getCurrentUserId())) {
                            cNode.setId(child.getUuid());
                            cNode.setName(child.getName());
                            cNode.setData(child.getJsonData());
                            cNode.setNocheck(true);
                            cTreeNodes.add(cNode);
                            dNode.setChildren(cTreeNodes);
                            List<Directory> childDirectorys = child.getChildren();
                            getChildDirectoryByUserId(cNode, childDirectorys);
                        }
                    }
                } catch (JSONException e) {
                    logger.error(ExceptionUtils.getStackTrace(e));
                }
            }
        }
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.directorydata.service.DirectoryDataService#addDirectory(com.wellsoft.pt.basicdata.directorydata.bean.DirectoryBean)
     */
    @Override
    public String addDirectory(DirectoryBean directoryBean) {
        Directory directory = new Directory();
        directory.setName(directoryBean.getName());
        directory.setJsonData(directoryBean.getJsonData());
        directory.setOrderNo(directoryBean.getOrderNo());
        directory.setParent(directoryBean.getParent());
        directory.setDirectoryTopUuid(directoryBean.getDirectoryTopUuid());
        // if (checkDirectoryNameExists(directory.getName(),
        // directory.getParent())) {
        // throw new
        // RuntimeException("directory name exists within same parent directory.");
        // }
        directoryDao.save(directory);
        Map<Permission, List<String>> aclConfigInfo = directoryBean.getAclConfigInfo();
        if (aclConfigInfo != null) {
            for (Map.Entry<Permission, List<String>> entry : aclConfigInfo.entrySet()) {
                Permission permission = entry.getKey();
                List<String> sids = entry.getValue();
                for (String sid : sids) {
                    aclService.addPermission(Directory.class, directory.getUuid(), permission, sid);
                }
            }
        }
        return directory.getUuid();

    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.directorydata.service.DirectoryDataService#getAllChildDataTreeByUserId(java.lang.String, java.lang.String)
     */
    @Override
    public List<TreeNode> getAllChildDataTreeByUserId(String id, String directoryUuid) {

        // List<Permission> permissions = new ArrayList<Permission>();
        // permissions.add(BasePermission.ADMINISTRATION);
        // permissions.add(BasePermission.DELETE);
        // permissions.add(BasePermission.READ);
        // permissions.add(BasePermission.WRITE);
        //
        // String currentUserId = SpringSecurityUtils.getCurrentUserId();
        // List<String> sids =
        // orgApiFacade.getAllRelatedGroupOrgs(currentUserId);
        // List<Directory> list = aclService.getAll(Directory.class,
        // permissions, sids);

        List<Directory> list = new ArrayList<Directory>();
        // 查询所有根结点
        if (directoryUuid.equals(TreeNode.ROOT_ID)) {
            list = this.getTopDirectory();
        } else {
            Directory directory = this.directoryDao.get(directoryUuid);
            if (directory != null) {
                list = Lists.newArrayList(directory.getChildren());
            }
        }
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        for (Directory directory : list) {
            TreeNode dNode = new TreeNode();
            List<TreeNode> childTreeNodes3 = new ArrayList<TreeNode>();
            try {
                if (directory.getJsonData() != null) {
                    JSONObject jsonObject = new JSONObject(directory.getJsonData());
                    String userId = jsonObject.getString("currentUserId");
                    if (userId.equals(SpringSecurityUtils.getCurrentUserId())) {
                        dNode.setId(directory.getUuid());
                        dNode.setName(directory.getName());
                        dNode.setData(directory.getJsonData());
                        dNode.setNocheck(true);
                        List<Data> datas = getAllDatas(directory.getUuid());
                        for (Data cData : datas) {
                            JSONObject jsonObject4;
                            jsonObject4 = new JSONObject(cData.getJsonData());
                            String userId4 = jsonObject4.getString("currentUserId");
                            if (userId4.equals(SpringSecurityUtils.getCurrentUserId())) {
                                TreeNode cnode = new TreeNode();
                                cnode.setId(cData.getUuid());
                                cnode.setName(cData.getName());
                                cnode.setData(cData.getJsonData());
                                childTreeNodes3.add(cnode);
                            }
                        }
                        dNode.setChildren(childTreeNodes3);
                        dNode.setIsParent(true);
                    }
                } else {
                    dNode.setId(directory.getUuid());
                    dNode.setName(directory.getName());
                    dNode.setData(directory.getJsonData());
                    dNode.setNocheck(true);

                    List<Data> datas = getAllDatas(directory.getUuid());
                    for (Data cData : datas) {
                        JSONObject jsonObject4;
                        jsonObject4 = new JSONObject(cData.getJsonData());
                        String userId4 = jsonObject4.getString("currentUserId");
                        if (userId4.equals(SpringSecurityUtils.getCurrentUserId())) {
                            TreeNode cnode = new TreeNode();
                            cnode.setId(cData.getUuid());
                            cnode.setName(cData.getName());
                            cnode.setData(cData.getJsonData());
                            childTreeNodes3.add(cnode);
                        }
                    }
                    dNode.setChildren(childTreeNodes3);
                    dNode.setIsParent(true);
                }
            } catch (JSONException e) {
                logger.error(ExceptionUtils.getStackTrace(e));
            }

            List<Directory> childDirectorys = directory.getChildren();
            if (childDirectorys.size() > 0) {
                for (Directory child : childDirectorys) {
                    JSONObject jsonObject2;
                    try {
                        if (directory.getJsonData() != null) {
                            if (child.getJsonData() != null) {
                                jsonObject2 = new JSONObject(child.getJsonData());
                                String userId2 = jsonObject2.getString("currentUserId");
                                if (userId2.equals(SpringSecurityUtils.getCurrentUserId())) {
                                    TreeNode cNode = new TreeNode();
                                    cNode.setId(child.getUuid());
                                    cNode.setName(child.getName());
                                    cNode.setData(child.getJsonData());
                                    cNode.setNocheck(true);
                                    List<TreeNode> childTreeNodes2 = new ArrayList<TreeNode>();
                                    List<Data> datas = getAllDatas(child.getUuid());
                                    for (Data cData : datas) {
                                        TreeNode cnode = new TreeNode();
                                        cnode.setId(cData.getUuid());
                                        cnode.setName(cData.getName());
                                        cnode.setData(cData.getJsonData());
                                        childTreeNodes2.add(cnode);
                                        cNode.setChildren(childTreeNodes2);
                                        cNode.setIsParent(true);

                                    }
                                    childTreeNodes3.add(cNode);
                                    dNode.setChildren(childTreeNodes3);
                                }
                            }
                        } else {
                            TreeNode cNode = new TreeNode();
                            cNode.setId(child.getUuid());
                            cNode.setName(child.getName());
                            cNode.setData(child.getJsonData());
                            cNode.setNocheck(true);
                            List<TreeNode> childTreeNodes2 = new ArrayList<TreeNode>();
                            List<Data> datas = getAllDatas(child.getUuid());
                            for (Data cData : datas) {
                                JSONObject jsonObject4;
                                jsonObject4 = new JSONObject(cData.getJsonData());
                                String userId4 = jsonObject4.getString("currentUserId");
                                if (userId4.equals(SpringSecurityUtils.getCurrentUserId())) {
                                    TreeNode cnode = new TreeNode();
                                    cnode.setId(cData.getUuid());
                                    cnode.setName(cData.getName());
                                    cnode.setData(cData.getJsonData());
                                    childTreeNodes2.add(cnode);
                                    cNode.setChildren(childTreeNodes2);
                                    cNode.setIsParent(true);
                                }
                            }
                            childTreeNodes3.add(cNode);
                            dNode.setChildren(childTreeNodes3);
                        }
                    } catch (JSONException e) {
                        logger.error(ExceptionUtils.getStackTrace(e));
                    }
                }
            } else {

                List<TreeNode> childTreeNodes = new ArrayList<TreeNode>();
                List<Data> datas = getAllDatas(directory.getUuid());
                for (int i = 0; i < datas.size(); i++) {
                    TreeNode node = new TreeNode();
                    JSONObject jsonObject3;
                    try {
                        jsonObject3 = new JSONObject(datas.get(i).getJsonData());
                        String userId3 = jsonObject3.getString("currentUserId");
                        if (userId3.equals(SpringSecurityUtils.getCurrentUserId())) {
                            node.setId(datas.get(i).getUuid());
                            node.setName(datas.get(i).getName());
                            node.setData(datas.get(i).getJsonData());
                            childTreeNodes.add(node);
                            dNode.setChildren(childTreeNodes);
                            dNode.setIsParent(true);
                        }
                    } catch (JSONException e) {
                        logger.error(ExceptionUtils.getStackTrace(e));
                    }
                }
            }

            if (dNode.getName() != null) {
                treeNodes.add(dNode);
            }
        }
        return treeNodes;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.directorydata.service.DirectoryDataService#getAllChildDirectoryTreeByUserId(java.lang.String, java.lang.String)
     */
    @Override
    public List<TreeNode> getAllChildDirectoryTreeByUserId(String id, String directoryUuid) {

        List<Directory> list = new ArrayList<Directory>();
        // 查询所有根结点
        if (directoryUuid.equals(TreeNode.ROOT_ID)) {
            list = this.getTopDirectory();
        } else {
            Directory directory = this.directoryDao.get(directoryUuid);
            if (directory != null) {
                list = Lists.newArrayList(directory.getChildren());
            }
        }
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        for (Directory directory : list) {
            TreeNode dNode = new TreeNode();
            JSONObject jsonObject;
            try {
                if (directory.getJsonData() != null) {
                    jsonObject = new JSONObject(directory.getJsonData());
                    String userId = jsonObject.getString("currentUserId");
                    if (userId.equals(SpringSecurityUtils.getCurrentUserId())) {
                        dNode.setId(directory.getUuid());
                        dNode.setName(directory.getName());
                        dNode.setData(directory.getJsonData());
                        dNode.setNocheck(true);

                        List<Directory> childDirectorys = directory.getChildren();
                        getChildDirectoryByUserId(dNode, childDirectorys);
                        treeNodes.add(dNode);
                    }
                }
            } catch (JSONException e) {
                logger.error(ExceptionUtils.getStackTrace(e));
            }
        }
        return treeNodes;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.directorydata.service.DirectoryDataService#loadDirectoryTreeByParentUuid(java.lang.String, java.lang.String)
     */
    @Override
    public List<TreeNode> loadDirectoryTreeByParentUuid(String id, String parentDirectoryUuid) {
        return loadDirecotryTreeByUserId(parentDirectoryUuid, SpringSecurityUtils.getCurrentUserId());
    }

}
