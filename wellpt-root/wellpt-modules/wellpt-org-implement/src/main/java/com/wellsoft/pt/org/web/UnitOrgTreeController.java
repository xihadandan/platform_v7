package com.wellsoft.pt.org.web;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.org.unit.service.UnitOrgTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Description: 组织树控制器
 *
 * @author Xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年5月30日.1	Xiem		2016年5月30日		Create
 * </pre>
 * @date 2016年5月30日
 */
@Controller
@RequestMapping("/unit/org/tree")
public class UnitOrgTreeController {
    @Autowired
    private UnitOrgTreeService UnitOrgTreeService;

    @RequestMapping(value = "/getTree", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<TreeNode> getTree(HttpServletRequest request) {
        String uuid = request.getParameter("uuid");
        return UnitOrgTreeService.getOrgEmployeeTree(uuid);
    }

    @RequestMapping(value = "/getTreeByName", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<TreeNode> getTreeByName(HttpServletRequest request) {
        String name = request.getParameter("name");
        return UnitOrgTreeService.getOrgEmployeeeTreeBySearchName(name);
    }

    @RequestMapping(value = "/getManageTree", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<TreeNode> getManageTree(HttpServletRequest request) {
        String uuid = request.getParameter("uuid");
        return UnitOrgTreeService.getOrgEmployeeManageTree(uuid);
    }

    @RequestMapping(value = "/getManageTreeByName", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<TreeNode> getTreeManageByName(HttpServletRequest request) {
        String name = request.getParameter("name");
        return UnitOrgTreeService.getOrgEmployeeeManageTreeBySearchName(name);
    }
}
