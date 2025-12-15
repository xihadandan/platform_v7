/*
 * @(#)2012-12-20 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.web;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.security.audit.bean.ResourceBean;
import com.wellsoft.pt.security.audit.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description: 资源维护控制器类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-12-20.1	zhulh		2012-12-20		Create
 * </pre>
 * @date 2012-12-20
 */
@Controller
@RequestMapping("/security/resource")
public class ResourceController extends BaseController {
    @Autowired
    private ResourceService resourceService;

    /**
     * 打开资源列表界面
     *
     * @return
     */
    @RequestMapping(value = "")
    public String resource() {
        return forward("/security/resource");
    }

    @RequestMapping(value = "/get")
    @ResponseBody
    public ResultMessage get(@RequestBody ResourceBean resource) {
        ResultMessage resultMessage = new ResultMessage();
        ResourceBean bean = resourceService.getBean(resource.getUuid());
        resultMessage.setData(bean);
        return resultMessage;
    }

    @RequestMapping(value = "/save")
    @ResponseBody
    public ResultMessage save(@RequestBody ResourceBean bean) {
        ResultMessage resultMessage = new ResultMessage();
        resourceService.saveBean(bean);
        resultMessage.setData(bean);
        return resultMessage;
    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    public ResultMessage delete(@RequestBody ResourceBean bean) {
        ResultMessage resultMessage = new ResultMessage();
        resourceService.remove(bean.getUuid());
        resultMessage.setData(bean);
        return resultMessage;
    }

    /**
     * 获取资源的树形结构
     *
     * @param excludeUuid
     * @return
     */
    @RequestMapping(value = "/get/tree", method = RequestMethod.POST)
    @ResponseBody
    public List<TreeNode> getTree(@RequestParam("excludeUuid") String excludeUuid) {
        TreeNode treeNode = resourceService.getMenuAsTree(excludeUuid);
        return treeNode.getChildren();
    }

}
