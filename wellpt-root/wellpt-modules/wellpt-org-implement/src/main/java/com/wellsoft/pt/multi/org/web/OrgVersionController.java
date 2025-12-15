/*
 * @(#)2015-10-10 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.wellsoft.context.component.jqgrid.JqGridQueryController;
import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.exception.WellException;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.util.date.DateUtil;
import com.wellsoft.context.util.excel.ExcelUtils;
import com.wellsoft.context.util.file.FileDownloadUtils;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.basicdata.datastore.support.export.DataStoreExportFactory;
import com.wellsoft.pt.multi.org.bean.*;
import com.wellsoft.pt.multi.org.entity.*;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgTreeDialogService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgVersionFacade;
import com.wellsoft.pt.multi.org.service.MultiOrgElementService;
import com.wellsoft.pt.multi.org.service.MultiOrgTreeNodeService;
import com.wellsoft.pt.multi.org.service.MultiOrgTypeService;
import com.wellsoft.pt.multi.org.service.MultiOrgVersionService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-14.1	zyguo		2015-10-10		Create
 * </pre>
 * @date 2015-10-10
 */
@Controller
@RequestMapping(value = "/multi/org")
public class OrgVersionController extends JqGridQueryController {
    @Autowired
    private MultiOrgVersionFacade multiOrgVersionFacadeService;

    @Autowired
    private MultiOrgVersionService multiOrgVersionService;

    @Autowired
    private MultiOrgService multiOrgService;

    @Autowired
    private MultiOrgUserService multiOrgUserService;

    @Autowired
    private MultiOrgTypeService multiOrgTypeService;

    @Autowired
    private MultiOrgTreeNodeService treeNodeService;

    @Autowired
    private DataStoreExportFactory dataStoreExportFactory;
    @Autowired
    private MultiOrgTreeDialogService multiOrgTreeDialogService;
    @Autowired
    private MultiOrgElementService multiOrgElementService;

    /**
     * 打开列表界面
     *
     * @return
     */
    @RequestMapping(value = "/version/list")
    public String orgVersionList(Model model) {
        return "/multi/org/org_version_list";
    }

    @RequestMapping(value = "/version/showLeader")
    public String showLeader(String verId, Model model) {
        if (StringUtils.isBlank(verId)) {
            throw new RuntimeException("参数不对");
        }
        MultiOrgVersion ver = this.multiOrgVersionService.getById(verId);
        model.addAttribute("orgVersion", ver);
        return "/multi/org/org_version_leader_list";
    }

    @RequestMapping(value = "/version/list/tree")
    public @ResponseBody
    JqGridQueryData listAsTreeJson(HttpServletRequest request, JqGridQueryInfo jqGridQueryInfo) {
        QueryInfo queryInfo = buildQueryInfo(jqGridQueryInfo, request);
        jqGridQueryInfo.set_search(request.getParameter("systemUnitId"));
        QueryData queryData = multiOrgVersionFacadeService.getForPageAsTree(jqGridQueryInfo,
                queryInfo);
        return convertToJqGridQueryData(queryData);
    }

    /**
     * 打开配置页面
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/version/config")
    public String config(Model model, HttpServletRequest req) {
        String versionId = req.getParameter("id");
        MultiOrgVersion orgVersion = multiOrgVersionService.getById(versionId);
        if (orgVersion == null) {
            throw new RuntimeException("参数不对");
        }
        model.addAttribute("orgVersion", orgVersion);
        return "/multi/org/config_org_version";
    }

    /**
     * 打开升级页面
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/node/list")
    public String treeNodeList(Model model, HttpServletRequest req) {
        String orgVersionId = req.getParameter("orgVersionId");
        if (StringUtils.isBlank(orgVersionId)) {
            throw new RuntimeException("参数不对");
        }
        MultiOrgVersion orgVersion = this.multiOrgVersionService.getById(orgVersionId);
        if (orgVersion == null) {
            throw new RuntimeException("参数不对");
        }
        model.addAttribute("orgVersion", orgVersion);
        return "/multi/org/org_node_list";
    }

    /**
     * 统一的组织弹出框
     *
     * @param model
     * @param req
     * @return
     */
    @RequestMapping(value = "/dialog")
    public String dialog(Model model, HttpServletRequest req) {
        String unitId = req.getParameter("unitId");
        List<MultiOrgOption> list = multiOrgService.getOrgOptionListByUnitId(unitId, false);
        model.addAttribute("orgOptionList", JsonUtils.object2Json(list));
        // 组织类型
        String currentUnitId = StringUtils.isBlank(
                unitId) ? SpringSecurityUtils.getCurrentUserUnitId() : unitId;
        List<MultiOrgType> orgTypes = multiOrgTypeService.queryOrgTypeListBySystemUnitId(
                currentUnitId);
        model.addAttribute("orgTypes", orgTypes);
        return "/multi/org/org_tree_dialog";
    }

    @RequestMapping(value = "/show")
    public String showOrg(Model model, String type, String orgVersionId, String unitId,
                          String userId) {
        OrgTreeDialogParams params = new OrgTreeDialogParams();
        params.setIsNeedUser(1);
        params.setInMyUnit(false);
        params.setOrgVersionId(orgVersionId);
        params.setUnitId(unitId);
        params.setUserId(userId);
        model.addAttribute("params", JsonUtils.object2Json(params));
        model.addAttribute("type", type);

        TreeNode rootNode = new TreeNode();
        List<TreeNode> userList = null;
        List<TreeNode> children = multiOrgTreeDialogService.queryUnitTreeDialogDataByType(type,
                params);
        if (!CollectionUtils.isEmpty(children)) {
            rootNode.getChildren().addAll(children);
            userList = rootNode.queryChildNodeListByTypeAndRemoveRepeat(IdPrefix.USER.getValue(),
                    true);
        }
        model.addAttribute("userList", userList == null ? new ArrayList<TreeNode>() : userList);
        return "/multi/org/show_org";
    }

    // 检查数据格式是否正确
    private List<String> checkImportData(Sheet sheet) {
        List<String> errList = Lists.newArrayList();
        int maxRow = sheet.getLastRowNum();
        // 第一行是标题，所以是从第二行开始读取
        for (int rowNum = 1; rowNum <= maxRow; rowNum++) {
            Row row = sheet.getRow(rowNum);
            // 检查数据类型，并利用map剔除重复数据;同时清洗数据格式，
            if (row != null) {
                // 单位，部门路径，职位，职务，编码，节点类型，
                Cell unitCell = row.getCell(0);
                Cell typeCell = row.getCell(5);
                String unitName = unitCell == null ? "" : unitCell.getStringCellValue();
                if (StringUtils.isBlank(unitName)) {
                    errList.add("第" + rowNum + "条数据异常，单位不能为空");
                } else {
                    String typeName = typeCell == null ? "" : typeCell.getStringCellValue();
                    if (StringUtils.isNotBlank(typeName)) {
                        if (ImportNode.getNodeTypeFromName(typeName) == null) {
                            errList.add("第" + rowNum + "条数据异常，节点类型支持：单位，部门，职位，节点4种类型");
                        }
                    }
                }
            }
        }
        return errList;
    }

    // 清洗数据，通过map数据去重，标准化格式，以及补充缺漏的部门节点数据
    private Map<String, ImportNode> formatImportData(Sheet sheet, MultiOrgVersion ver) {
        Map<String, ImportNode> allNodeMap = Maps.newHashMap();
        int maxRow = sheet.getLastRowNum();
        // 第一行是标题，所以是从第二行开始读取
        for (int rowNum = 1; rowNum <= maxRow; rowNum++) {
            Row row = sheet.getRow(rowNum);
            // 检查数据类型，并利用map剔除重复数据;同时清洗数据格式，
            if (row != null) {
                ImportNode inode = new ImportNode(ver, row);
                allNodeMap.put(inode.getNamePath(), inode);
            }
        }

        // 因为组织是个 树形结构，数据有前后依赖关系，所以必须先排序下，然后重新检查数据的上下级依赖关系是否有缺失，如有缺失则需要自动补充节点
        List<String> sortedList = Ordering.natural().sortedCopy(allNodeMap.keySet());
        for (String namePath : sortedList) {
            String[] names = namePath.split("/");
            // 依次判断上级节点是否存在
            // 因为是判断上级节点，所以 i = length - 1, 另外第一个数据是组织版本名称，所以i>1开始
            for (int i = names.length - 1; i > 1; i--) {
                String parentNamePath = StringUtils.join(names, "/", 0, i);
                if (allNodeMap.containsKey(parentNamePath)) {
                    continue;
                } else {
                    // 缺失对应上级节点，需要补充数据
                    ImportNode node = new ImportNode(ver, parentNamePath);
                    allNodeMap.put(parentNamePath, node);
                }
            }
        }
        return allNodeMap;

    }

    @RequestMapping(value = "/version/import", method = RequestMethod.POST)
    @ResponseBody
    public String importOrg(@RequestParam(value = "upload") MultipartFile excelFile,
                            @RequestParam(value = "verId") String verId,
                            @RequestParam(value = "fun", required = false) String fun,
                            HttpServletResponse response) throws IOException {
        String result = this.importOrgReturn(excelFile, verId);
        if (StringUtils.isNotBlank(fun)) {
            fun = HtmlUtils.htmlUnescape(fun);
            fun = fun.replace("{data}", result);
            return fun;
        }
        return result;
    }

    private String importOrgReturn(MultipartFile excelFile, String verId) throws IOException {
        Sheet sheet = ExcelUtils.getSheetFromInputStream(excelFile.getInputStream(), "组织");
        if (sheet == null) {
            return "上传的excel解析失败，请检查文件。";
        }
        MultiOrgVersion ver = this.multiOrgVersionFacadeService.getVersionById(verId);
        if (ver == null) {
            return "对应的组织版本不存在";
        }
        // 检查该版本是否有配置过用户，如果有配置过，则不能导入，因为导入会自动清空以前的旧数据
        List<OrgUserTreeNodeDto> users = this.multiOrgUserService.queryUserByOrgVersion(
                ver.getId());
        if (!CollectionUtils.isEmpty(users)) {
            return "该组织已分配了用户，无法导入，请先移除该组织下的用户";
        }

        // 检查数据格式是否正确
        List<String> errList = checkImportData(sheet);
        if (!errList.isEmpty()) {
            return StringUtils.join(errList, "\r\n");
        }

        // 清洗数据，数据去重，标准化格式，以及补充缺漏的部门节点数据
        // 该版本对应的单位节点
        Map<String, ImportNode> nodeMap = formatImportData(sheet, ver);

        // 重新排序下
        List<String> sortedList = Ordering.natural().sortedCopy(nodeMap.keySet());
        // 按顺序依次生成节点ID, 以及开始组装 id_path
        Map<String, String> namePath2idPath = Maps.newHashMap();
        // 把版本ID和本单位对应的ID,放进来
        namePath2idPath.put(ver.getName(), ver.getId());
        OrgTreeNodeDto selfUnit = this.multiOrgService.getNodeByEleIdAndOrgVersion(
                ver.getSelfBusinessUnitId(),
                ver.getId());
        String selfUnitName = null;
        if (selfUnit != null) {
            selfUnitName = ver.getName() + "/" + selfUnit.getName();
            namePath2idPath.put(selfUnitName, ver.getId() + "/" + selfUnit.getEleId());
            // 先清空所有旧数据，然后插入新数据
        } else {
            selfUnit = new OrgTreeNodeDto();
            selfUnit.setEleIdPath(ver.getId() + "/" + ver.getSelfBusinessUnitId());
            selfUnit.setOrgVersionId(ver.getId());
        }
        this.clearOldOrgData(selfUnit);
        String unitId = SpringSecurityUtils.getCurrentUserUnitId();
        for (String namePath : sortedList) {
            ImportNode node = nodeMap.get(namePath);
            // 单位自身节点已经存在，不用再生成ID, 直接跳过
            if (namePath.equals(selfUnitName)) {
                node.setEleId(selfUnit.getEleId());
                node.setIdPath(namePath2idPath.get(namePath));
                continue;
            } else {
                try {
                    OrgTreeNodeVo vo = new OrgTreeNodeVo();
                    vo.setName(node.getNodeName());
                    vo.setType(node.getNodeType());
                    vo.setCode(node.getCode());
                    if (StringUtils.isBlank(node.getCode())) {
                        vo.setCode(System.currentTimeMillis() + "");
                    }
                    vo.setOrgVersionId(ver.getId());
                    vo.setSystemUnitId(unitId);
                    String parentNamePath = node.getParentNamePath();
                    String parentIdPath = namePath2idPath.get(parentNamePath);
                    if (StringUtils.isBlank(parentIdPath)) {
                        return "数据转化异常，找不到对应的的idPath=" + parentNamePath;
                    }
                    vo.setParentEleNamePath(parentNamePath);
                    vo.setParentEleIdPath(parentIdPath);
                    // 如果是个职位节点，需要判断是否有配置职务
                    if (node.getNodeType().equals(IdPrefix.JOB.getValue())) {
                        if (StringUtils.isNotBlank(node.getDutyName())) {
                            MultiOrgDuty duty = this.multiOrgService.getDutyByName(
                                    node.getDutyName());
                            if (duty == null) {
                                duty = new MultiOrgDuty();
                                duty.setName(node.getDutyName());
                                duty.setSystemUnitId(unitId);
                                duty.setCode(System.currentTimeMillis() + "");
                                this.multiOrgService.addDuty(duty);
                            }
                            vo.setDutyId(duty.getId());
                        }
                    }
                    this.multiOrgService.addOrgChildNode(vo);
                    // 添加成功后，需要将
                    namePath2idPath.put(vo.getEleNamePath(), vo.getEleIdPath());

                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    throw e;
                }
            }
        }

        return "导入成功";
    }

    @RequestMapping(value = "/version/export", method = RequestMethod.POST)
    public void export(String verId, HttpServletRequest request,
                       HttpServletResponse response) throws IOException {
        if (StringUtils.isBlank(verId)) {
            throw new WellException("参数异常");
        }
        MultiOrgVersion ver = this.multiOrgVersionService.getById(verId);
        if (ver == null) {
            throw new WellException("对应的组织版本不存在");
        }

        String fileName = ver.getName() + "_" + DateUtil.getFormatDate(new Date(),
                "yyyy-MM-dd") + ".xls";
        Map<String, MultiOrgElement> allEleMap = this.multiOrgElementService.queryElementMapByOrgVersion(
                verId);
        List<OrgTreeNodeDto> list = treeNodeService.queryAllChildrenNodeOfOrgVersionByEleIdPath(
                verId, verId);
        String[] titles = new String[]{"单位", "部门", "职位", "职务", "编码", "节点类型"};
        List<String[]> dataList = Lists.newArrayList();
        for (OrgTreeNodeDto dto : list) {
            dto.computeEleNamePath(allEleMap);
            String[] names = dto.getEleNamePath().split("/");
            if (names.length > 1) {
                String[] data = getExportData(dto, names);
                if (data != null) {
                    dataList.add(data);
                }
            }
        }
        HSSFWorkbook excel = ExcelUtils.generateExcelBook(dataList, titles);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            excel.write(os);
            FileDownloadUtils.download(request, response,
                    new ByteArrayInputStream(os.toByteArray()), fileName);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        } finally {
            os.close();
        }
    }

    private void clearOldOrgData(OrgTreeNodeDto selfUnit) {
        this.multiOrgService.clearOrgTree(selfUnit);
    }

    private String[] getExportData(OrgTreeNodeDto dto, String[] names) {
        String unitName = names[1];
        String deptPath = null;
        if (names.length > 2) {
            deptPath = StringUtils.join(names, "/", 2, names.length);
        }
        String jobName = null;
        String dutyName = null;
        String code = dto.getCode();
        String type = dto.getType();
        // 计算节点类型
        String typeName = null;
        if (type.equals(IdPrefix.BUSINESS_UNIT.getValue())) {
            typeName = "单位";
        } else if (type.equals(IdPrefix.DEPARTMENT.getValue())) {
            typeName = "部门";
        } else if (type.equals(IdPrefix.JOB.getValue())) {
            typeName = "职位";
        } else if (type.equals(IdPrefix.ORG.getValue())) {
            typeName = "节点";
        } else if (type.equals(IdPrefix.ORG_VERSION.getValue())) {
            return null;
        } else {
            typeName = type;
        }

        // 计算职务信息
        if (dto.getType().equals(IdPrefix.JOB.getValue())) {
            jobName = dto.getName();
            // 最后一个是职位名称，不能计入部门路径里面
            deptPath = StringUtils.join(names, "/", 2, names.length - 1);
            MultiOrgJobDuty jobDuty = this.multiOrgService.getJobDutyByJobId(dto.getEleId());
            if (jobDuty != null) {
                MultiOrgDuty duty = this.multiOrgService.getDutyById(jobDuty.getDutyId());
                if (duty != null) {
                    dutyName = duty.getName();
                }
            }
        }
        return new String[]{unitName, deptPath, jobName, dutyName, code, typeName};
    }

    @RequestMapping(value = "/orgTypesOptions")
    public @ResponseBody
    Map<String, Object> getUnitOrgTypesOptions(@RequestParam(required = false) String unitId) {
        List<MultiOrgOption> list = multiOrgService.getOrgOptionListByUnitId(unitId, false);
        String currentUnitId = StringUtils.isBlank(
                unitId) ? SpringSecurityUtils.getCurrentUserUnitId() : unitId;
        List<MultiOrgType> orgTypes = multiOrgTypeService.queryOrgTypeListBySystemUnitId(
                currentUnitId);

        Map<String, Object> result = Maps.newHashMap();
        result.put("orgOptionsList", list);
        result.put("orgTypes", orgTypes);
        return result;
    }


}
