/*
 * @(#)Jan 5, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.app.entity.AppDefElementI18nEntity;
import com.wellsoft.pt.app.service.AppDefElementI18nService;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.dms.facade.api.DmsFileServiceFacade;
import com.wellsoft.pt.dms.file.action.FileActions;
import com.wellsoft.pt.dms.file.executor.FileActionExecutorMethodFactory;
import com.wellsoft.pt.dms.file.executor.ListFolderActionExecutor.ListFolderActionParam;
import com.wellsoft.pt.dms.file.executor.ListFolderActionExecutor.ListFolderActionResult;
import com.wellsoft.pt.dms.file.executor.ReadFolderActionExecutor.ReadFolderActionParam;
import com.wellsoft.pt.dms.file.executor.ReadFolderActionExecutor.ReadFolderActionResult;
import com.wellsoft.pt.dms.file.query.api.DmsFileQuery;
import com.wellsoft.pt.dms.model.DmsFolder;
import com.wellsoft.pt.jpa.datasource.DatabaseType;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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
@Controller
@RequestMapping({"/dms/file/manager/component", "/api/dms/file/manager"})
public class DmsFileManagerComponentController extends BaseController {

    @Autowired
    private DmsFileServiceFacade dmsFileServiceFacade;

    @Autowired
    private AppDefElementI18nService appDefElementI18nService;

    @RequestMapping(value = "/load/folder/tree")
    @ResponseBody
    public ApiResult<List<TreeNode>> loadTree(
            @RequestParam(value = "parentFolderUuid", required = false) String parentFolderUuid,
            @RequestParam(value = "belongToFolderUuid", required = true) String belongToFolderUuid,
            @RequestParam(value = "showRootFolder", required = false, defaultValue = "true") boolean showRootFolder,
            @RequestParam(value = "rootFolderDisplayName", required = false, defaultValue = StringUtils.EMPTY) String rootFolderDisplayName,
            @RequestParam(value = "loadAction", required = false, defaultValue = "false") boolean loadAction,
            @RequestParam(value = "checkIsParent", required = false, defaultValue = "false") boolean checkIsParent,
            @RequestParam(value = "keyword", required = false, defaultValue = StringUtils.EMPTY) String keyword,
            @RequestParam(value = "withoutPermission", required = false) boolean withoutPermission,
            HttpServletRequest request) {
        List<TreeNode> treeNodes = null;
        // 关键字查询
        if (StringUtils.isNotBlank(keyword)) {
            return ApiResult.success(queryFolderTree(belongToFolderUuid, keyword));
        } else {
            String[] belongToFolderUuids = StringUtils.split(belongToFolderUuid, Separator.SEMICOLON.getValue());
            List<DmsFolder> dmsFolders = Lists.newArrayList();
            // 显示根目录
            if (ArrayUtils.isEmpty(belongToFolderUuids) && StringUtils.isBlank(parentFolderUuid)) {
                dmsFolders = dmsFileServiceFacade.listRootFolder();
                treeNodes = converToTreeNodes(dmsFolders);
            } else if (belongToFolderUuids.length > 1 && StringUtils.isBlank(parentFolderUuid)) {
                // 显示多个根结点
                for (String rootFolderUuid : belongToFolderUuids) {
                    ReadFolderActionParam readFolderActionParam = new ReadFolderActionParam();
                    readFolderActionParam.setFolderUuid(rootFolderUuid);
                    readFolderActionParam.setWithoutPermission(withoutPermission);
                    ReadFolderActionResult readFolderActionResult = FileActionExecutorMethodFactory
                            .getFileActionExecutorMethod(FileActions.READ_FOLDER).execute(readFolderActionParam);
                    DmsFolder dmsFolder = readFolderActionResult.getData();
                    if (dmsFolder != null) {
                        dmsFolders.add(dmsFolder);
                    }
                }
                treeNodes = converToTreeNodes(dmsFolders);
            } else {
                // 单一根结点
                ListFolderActionParam listFolderActionParam = new ListFolderActionParam();
                String folderUuid = parentFolderUuid;
                TreeNode root = null;
                // 第一次加载，读取指定结点
                if (StringUtils.isBlank(folderUuid)) {
                    ReadFolderActionParam readFolderActionParam = new ReadFolderActionParam();
                    readFolderActionParam.setFolderUuid(belongToFolderUuid);
                    readFolderActionParam.setWithoutPermission(withoutPermission);
                    ReadFolderActionResult readFolderActionResult = FileActionExecutorMethodFactory
                            .getFileActionExecutorMethod(FileActions.READ_FOLDER).execute(readFolderActionParam);
                    DmsFolder dmsFolder = readFolderActionResult.getData();
                    // 指定结点读取不到，直接返回
                    if (dmsFolder == null) {
                        return ApiResult.success(Collections.emptyList());
                    }
                    root = new TreeNode();
                    root.setId(dmsFolder.getUuid());
                    root.setName(dmsFolder.getName());
                    root.setIsParent(true);
                    folderUuid = belongToFolderUuid;
                    listFolderActionParam.setListNearestIfNotFound(true);
                }

                // 异步加载子结点
                listFolderActionParam.setFolderUuid(folderUuid);
                listFolderActionParam.setLoadAction(loadAction);
                listFolderActionParam.setCheckIsParent(checkIsParent);
                listFolderActionParam.setWithoutPermission(withoutPermission);
                ListFolderActionResult result = FileActionExecutorMethodFactory.getFileActionExecutorMethod(
                        FileActions.LIST_FOLDER).execute(listFolderActionParam);
                dmsFolders = result.getDataList();

                treeNodes = converToTreeNodes(dmsFolders);
                if (root != null) {
                    root.getChildren().addAll(treeNodes);
                    treeNodes = new ArrayList<TreeNode>();
                    treeNodes.add(root);
                }

                // 第一次加载且不显示根目录
                if (StringUtils.isBlank(parentFolderUuid) && !Boolean.TRUE.equals(showRootFolder)
                        && treeNodes.size() == 1) {
                    return ApiResult.success(treeNodes.get(0).getChildren());
                }
            }

            // 根结点显示指定名称
            if (Boolean.TRUE.equals(showRootFolder) && StringUtils.isNotBlank(rootFolderDisplayName)
                    && treeNodes.size() == 1) {
                treeNodes.get(0).setName(rootFolderDisplayName);
            }

        }
        return ApiResult.success(treeNodes);
    }

    /**
     * 关键字查询
     *
     * @param belongToFolderUuid
     * @param keyword
     * @return
     */
    private List<TreeNode> queryFolderTree(String belongToFolderUuid, String keyword) {
        DmsFileQuery dmsFileQuery = dmsFileServiceFacade.createFileQuery();
        dmsFileQuery.setFolderUuid(belongToFolderUuid);
        dmsFileQuery.setKeyword(keyword);
        Map<String, Object> params = Maps.newHashMap();
        params.put("keyword", keyword);
        if (DatabaseType.MySQL5.getName().equalsIgnoreCase(Config.getValue("database.type"))) {
            dmsFileQuery.where("t.name like concat('%', :keyword, '%')", params);
        } else {
            dmsFileQuery.where("t.name like '%' || :keyword || '%'", params);
        }
        dmsFileQuery.setListFileAction(FileActions.LIST_ALL_FOLDER);
        dmsFileQuery.setFirstResult(0);
        dmsFileQuery.setMaxResults(20);
        List<DmsFolder> dmsFolders = dmsFileQuery.list(DmsFolder.class);
        for (DmsFolder dmsFolder : dmsFolders) {
            dmsFolder.setParent(false);
        }
        return converToTreeNodes(dmsFolders);
    }

    /**
     * @param dmsFolders
     * @return
     */
    private List<TreeNode> converToTreeNodes(List<DmsFolder> dmsFolders) {
        TreeNode treeNode = new TreeNode();
        Map<String, TreeNode> nodeMap = Maps.newHashMap();
        for (DmsFolder dmsFolder : dmsFolders) {
            TreeNode folderNode = new TreeNode();
            folderNode.setId(dmsFolder.getUuid());
            folderNode.setName(dmsFolder.getName());
            folderNode.setIsParent(dmsFolder.isParent());
            treeNode.getChildren().add(folderNode);
            nodeMap.put(folderNode.getId(), folderNode);
        }
        if (MapUtils.isNotEmpty(nodeMap) && !LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
            List<AppDefElementI18nEntity> i18nEntities = appDefElementI18nService.getI18ns(nodeMap.keySet(), IexportType.DmsFolder, "name", LocaleContextHolder.getLocale().toString());
            if (CollectionUtils.isNotEmpty(i18nEntities)) {
                for (AppDefElementI18nEntity i : i18nEntities) {
                    if (nodeMap.containsKey(i.getDefId()) && StringUtils.isNotBlank(i.getContent())) {
                        nodeMap.get(i.getDefId()).setName(i.getContent());
                    }
                }
            }
        }
        return treeNode.getChildren();
    }

    /**
     * @param folderCodeMap
     * @return
     */
    @PostMapping(value = "/updateFolderCodes")
    @ApiOperation(value = "更新夹编号", notes = "更新夹编号")
    @ResponseBody
    public ApiResult<Boolean> updateFolderCodes(@RequestBody Map<String, String> folderCodeMap) {

        dmsFileServiceFacade.updateFolderCodes(folderCodeMap);

        return ApiResult.success();
    }

}
