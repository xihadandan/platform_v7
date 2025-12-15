/*
 * @(#)2016-08-24 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.facade.service.impl;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.util.web.JsonDataServicesContextHolder;
import com.wellsoft.pt.app.context.parse.FontIconParser;
import com.wellsoft.pt.app.css.FontIcon;
import com.wellsoft.pt.app.facade.service.AppCommonComponentService;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
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
@Service
public class AppCommonComponentServiceImpl implements AppCommonComponentService {

    private static final int FILE_TYPE_MONGO = 1;
    private static final int FILE_TYPE_PROJECT = 2;
    private static final int FILE_TYPE_FONT_ICON = 3;

    //    @Autowired
    private FontIconParser fontIconParser;


    @Autowired
    private MongoFileService mongoFileService;

    /**
     * 图片库-获取文件夹
     *
     * @param folderID         顶级文件夹ID
     * @param projRelativePath 项目相对路径
     * @return
     */
    @Override
    @Deprecated
    public List<TreeNode> getPicutreLibFolder(String mongoFolderID, String projRelativePath) {
        List<TreeNode> list = new ArrayList<TreeNode>();
        // 数据库资源
        TreeNode mongoTreeNode = new TreeNode();
        mongoTreeNode.setId(mongoFolderID);
        mongoTreeNode.setName("数据库资源");
        Map<String, Object> mongoTreeNodeData = new HashMap<String, Object>();
        mongoTreeNodeData.put("folderType", FILE_TYPE_MONGO);
        mongoTreeNode.setData(mongoTreeNodeData);

        // 项目资源
        TreeNode projectTreeNode = new TreeNode();

        HttpServletRequest request = JsonDataServicesContextHolder.getRequest();
        String path = request.getServletContext().getRealPath("/");
        String projectFolderId = path + projRelativePath;
        projectFolderId = StringUtils.replace(projectFolderId, "\\", File.separator);
        projectFolderId = StringUtils.replace(projectFolderId, "/", File.separator);
        File file = new File(projectFolderId);

        projectTreeNode.setId(projectFolderId);
        projectTreeNode.setName(file.getName());
        Map<String, Object> projectTreeNodeData = new HashMap<String, Object>();
        projectTreeNodeData.put("folderType", FILE_TYPE_PROJECT);
        projectTreeNode.setData(projectTreeNodeData);

        List<TreeNode> childrenFolder = this.getAllChildrenFolder(file);
        if (childrenFolder != null && childrenFolder.size() > 0) {
            projectTreeNode.setIsParent(true);
            projectTreeNode.getChildren().addAll(childrenFolder);
        }
        projectTreeNode.setId(projectFolderId);
        projectTreeNode.setName("项目资源");


        list.add(mongoTreeNode);
        list.add(projectTreeNode);

        return list;
    }

    /**
     * 获取所有子孙文件夹
     *
     * @param file
     * @return
     */
    private List<TreeNode> getAllChildrenFolder(File file) {
        List<TreeNode> treeNodeList = new ArrayList<TreeNode>();
        if (file.isDirectory()) {
            for (File childFile : file.listFiles()) {
                if (childFile.isDirectory()) {
                    TreeNode childTreeNode = new TreeNode();
                    childTreeNode.setId(childFile.getPath());
                    childTreeNode.setName(childFile.getName());
                    Map<String, Object> projectTreeNodeData = new HashMap<String, Object>();
                    projectTreeNodeData.put("folderType", FILE_TYPE_PROJECT);
                    childTreeNode.setData(projectTreeNodeData);

                    List<TreeNode> treeNodeList2 = this.getAllChildrenFolder(childFile);
                    if (treeNodeList2 != null && treeNodeList2.size() > 0) {
                        childTreeNode.setIsParent(true);
                        childTreeNode.getChildren().addAll(treeNodeList2);
                    }
                    treeNodeList.add(childTreeNode);
                }
            }
        }
        return treeNodeList;
    }

    /**
     * 图片库-通过文件夹ID获取文件
     *
     * @param folderID
     * @param folderType
     * @return
     */
    @Transactional(readOnly = true)
    @Deprecated
    public List<Map<String, Object>> getPicutreLibFilesByFolderID(String folderID, int folderType) {
        List<Map<String, Object>> fileList = new ArrayList<Map<String, Object>>();
        if (FILE_TYPE_MONGO == folderType) {
            List<MongoFileEntity> mongoFiles = mongoFileService.getFilesFromFolder(folderID, "");
            List<String> uniqueFiles = new ArrayList<String>();
            if (mongoFiles != null && !mongoFiles.isEmpty()) {
                for (MongoFileEntity mongoFile : mongoFiles) {
                    // 过滤重复相同物理ID的文件
                    String physicalId = mongoFile.getPhysicalID();
                    if (uniqueFiles.contains(physicalId)) {
                        continue;
                    }
                    uniqueFiles.add(physicalId);
                    Map<String, Object> fileMap = new HashMap<String, Object>();
                    fileMap.put("folderType", folderType);
                    fileMap.put("id", mongoFile.getFileID());
                    fileMap.put("name", mongoFile.getFileName());
                    fileList.add(fileMap);
                }
            }
        } else if (FILE_TYPE_PROJECT == folderType) {
            // 统一操作系统路径
            folderID = StringUtils.replace(folderID, "\\", File.separator);
            folderID = StringUtils.replace(folderID, "/", File.separator);
            File file = new File(folderID);
            fileList.addAll(this.getAllChildrenFiles(file));
        } else if (FILE_TYPE_FONT_ICON == folderType) {

            List<FontIcon> fontIcons = fontIconParser.getFontIcons();
            for (FontIcon fi : fontIcons) {
                if (!fi.getId().equalsIgnoreCase(folderID)) {
                    continue;
                }
                List<String> iconClasses = fi.getClasses();
                for (String c : iconClasses) {
                    Map<String, Object> fileMap = new HashMap<String, Object>();
                    fileMap.put("folderType", FILE_TYPE_FONT_ICON);
                    fileMap.put("id", c);
                    fileMap.put("name", c);
                    fileList.add(fileMap);
                }

            }

        }

        return fileList;
    }

    /**
     * 获取所有子孙文件
     *
     * @param file
     * @return
     */
    private List<Map<String, Object>> getAllChildrenFiles(File file) {
        List<Map<String, Object>> childrenFileList = new ArrayList<Map<String, Object>>();
        if (file.isDirectory()) {
            for (File childFile : file.listFiles()) {
                List<Map<String, Object>> childrenFiles = this.getAllChildrenFiles(childFile);
                if (childrenFiles != null && childrenFiles.size() > 0) {
                    childrenFileList.addAll(childrenFiles);
                }
            }
        } else {
            Map<String, Object> fileMap = new HashMap<String, Object>();
            fileMap.put("folderType", FILE_TYPE_PROJECT);
            fileMap.put("id", file.getPath());
            fileMap.put("name", file.getName());
            childrenFileList.add(fileMap);
        }
        return childrenFileList;
    }


}
