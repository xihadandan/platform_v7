/*
 * @(#)2016-07-24 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.web;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.ConfigurableIdEntity;
import com.wellsoft.context.util.file.FileDownloadUtils;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.app.facade.service.AppProductIntegrationMgr;
import com.wellsoft.pt.app.service.AppProductService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-14.1	zhulh		2016-07-24		Create
 * </pre>
 * @date 2016-07-24
 */
@Controller
@RequestMapping(value = "/app/app/product/integration")
public class AppProductIntegrationMgrController extends BaseController {

    @Autowired
    private AppProductService appProductService;

    @Autowired
    private AppProductIntegrationMgr appProductIntegrationMgr;

    /**
     * @param treeNode
     * @param level
     * @param sb
     * @return
     */
    @SuppressWarnings("unchecked")
    private static void buildText(TreeNode treeNode, int level, StringBuilder sb) {
        Map<String, Object> data = (Map<String, Object>) treeNode.getData();
        Object id = data.get(ConfigurableIdEntity.ID);
        for (int index = 0; index < level * 4; index++) {
            sb.append(Separator.SPACE.getValue());
        }
        sb.append(treeNode.getName() + "(" + id + ")");
        sb.append(Separator.LINE.getValue());
        for (TreeNode child : treeNode.getChildren()) {
            buildText(child, level + 1, sb);
        }
    }

    /**
     * 打开列表界面
     *
     * @return
     */
    @RequestMapping(value = "/list")
    public String appProductIntegrationList() {
        return forward("/app/app_product_integration_list");
    }

    @RequestMapping(value = "/export/{productUuid}")
    public void export(@PathVariable("productUuid") String productUuid, HttpServletRequest request,
                       HttpServletResponse response) {
        TreeNode treeNode = appProductIntegrationMgr.getTree(productUuid);
        String responseText = generate(treeNode);
        String fileName = treeNode.getName() + ".txt";
        FileDownloadUtils.download(request, response, IOUtils.toInputStream(responseText), fileName);
    }

    @RequestMapping(value = "/export/pi/{piUuid}")
    public void exportPiUuid(@PathVariable("piUuid") String piUuid, HttpServletRequest request,
                             HttpServletResponse response) {
        String[] dataTypes = {"1", "2", "3", "4"};
        TreeNode treeNode = appProductIntegrationMgr.getTreeByUuid(piUuid, dataTypes);
        String responseText = generate(treeNode);
        String fileName = treeNode.getName() + ".txt";
        FileDownloadUtils.download(request, response, IOUtils.toInputStream(responseText), fileName);
    }

    /**
     * @param treeNode
     * @return
     */
    private String generate(TreeNode treeNode) {
        StringBuilder sb = new StringBuilder();
        buildText(treeNode, 0, sb);
        return sb.toString();
    }
}
