/*
 * @(#)2015-10-10 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.web;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.*;
import com.wellsoft.context.component.jqgrid.JqGridQueryController;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.exception.WellException;
import com.wellsoft.context.util.date.DateUtil;
import com.wellsoft.context.util.excel.ExcelUtils;
import com.wellsoft.context.util.file.FileDownloadUtils;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.jpa.util.HqlUtils;
import com.wellsoft.pt.multi.org.bean.*;
import com.wellsoft.pt.multi.org.dto.MultiOrgPwdSettingDto;
import com.wellsoft.pt.multi.org.dto.NewAccountDto;
import com.wellsoft.pt.multi.org.dto.UserImportDto;
import com.wellsoft.pt.multi.org.entity.*;
import com.wellsoft.pt.multi.org.enums.AdminSetPwdTypeEnum;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgPwdSettingFacadeService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserAccountFacadeService;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.multi.org.service.*;
import com.wellsoft.pt.multi.org.util.PwdUtils;
import com.wellsoft.pt.multi.org.utils.ExportAccountUtils;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

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
@Api(tags = "组织用户管理接口")
@Controller
@RequestMapping(value = "/multi/org/user")
public class OrgUserController extends JqGridQueryController {

    @Autowired
    private MultiOrgService multiOrgService;

    @Autowired
    private OrgApiFacade orgApiFacade;

    @Autowired
    private MultiOrgVersionService multiOrgVersionService;

    @Autowired
    private MultiOrgElementService multiOrgElementService;
    @Autowired
    private MultiOrgUserAccountService multiOrgUserAccountService;
    @Autowired
    private MultiOrgUserInfoService multiOrgUserInfoService;
    @Autowired
    private MultiOrgUserTreeNodeService multiOrgUserTreeNodeService;
    @Autowired
    private MultiOrgTreeNodeService multiOrgTreeNodeService;

    @Autowired
    private MongoFileService mongoFileService;
    @Autowired
    private MultiOrgPwdSettingFacadeService multiOrgPwdSettingFacadeService;
    @Autowired
    private MultiOrgUserAccountFacadeService multiOrgUserAccountFacadeService;

    /**
     * 打开组织用户列表
     *
     * @return
     */
    @RequestMapping(value = "/tree")
    public String orgUserTree(Model model) {
        return "_tree";
    }

    /**
     * 打开列表界面
     *
     * @return
     */
    @RequestMapping(value = "/list")
    public String orgUserList(Model model) {
        model.addAttribute("systemUnitId", SpringSecurityUtils.getCurrentUserUnitId());
        return "/multi/org/org_user_list";
    }

    // 检查数据格式是否正确
    private List<String> checkImportData(Sheet sheet, HashMap<String, OrgTreeNodeDto> jobMap, MultiOrgVersion ver) {
        List<String> errList = Lists.newArrayList();
        int maxRow = sheet.getLastRowNum();
        String unitId = SpringSecurityUtils.getCurrentUserUnitId();
        try {
            // 第一行是标题，所以是从第二行开始读取
            for (int rowNum = 1; rowNum <= maxRow; rowNum++) {
                Row row = sheet.getRow(rowNum);
                // 检查数据类型，并利用map剔除重复数据;同时清洗数据格式，
                if (row != null) {
                    // 账号，姓名，职位全路径，员工编码
                    Cell unitNameCell = row.getCell(0);
                    Cell deptPathCell = row.getCell(1);
                    Cell jobNameCell = row.getCell(2);
                    Cell loginNameCell = row.getCell(3);
                    //Cell loginNameZhCell = row.getCell(4);
                    Cell userNameCell = row.getCell(5);
                    Cell sexCell = row.getCell(6);
                    String unitName = unitNameCell == null ? null : unitNameCell.getStringCellValue();
                    String deptPath = deptPathCell == null ? null : deptPathCell.getStringCellValue();
                    String jobName = jobNameCell == null ? null : jobNameCell.getStringCellValue();
                    String loginName = loginNameCell == null ? null : loginNameCell.getStringCellValue();
                    //String loginNameZh = loginNameZhCell == null ? null : loginNameZhCell.getStringCellValue();
                    String userName = userNameCell == null ? null : userNameCell.getStringCellValue();
                    String sex = sexCell == null ? null : sexCell.getStringCellValue();
                    StringBuilder sb = new StringBuilder();
                    if (StringUtils.isBlank(loginName)) {
                        sb.append("账号不能为空;");
                    }
                    if (StringUtils.isBlank(userName)) {
                        sb.append("姓名不能为空;");
                    }
                    // if (StringUtils.isBlank(sex)) {
                    // sb.append("性别不能为空;");
                    // }
                    if (StringUtils.isNotBlank(jobName)) {
                        String[] paths = new String[]{ver.getName(), unitName, deptPath, jobName};
                        String jobPath = "";
                        for (String path : paths) {
                            if (StringUtils.isNotBlank(path)) {
                                jobPath = jobPath + path + "/";
                            }
                        }
                        jobPath = jobPath.substring(0, jobPath.length() - 1);
                        if (!jobMap.containsKey(jobPath)) {
                            sb.append("对应的职位【").append(jobName).append("】不存在;");
                        }
                    }
                    if (StringUtils.isNotBlank(loginName)) {
                        MultiOrgUserAccount a = this.orgApiFacade.getAccountByLoginName(loginName);
                        if (a != null && !a.getSystemUnitId().equals(unitId)) {
                            sb.append("该账号已在其他家单位存在，请换一个账号名;");
                        }
                    }
                    if (sb.length() > 0) {
                        errList.add("第" + rowNum + "条数据异常:" + sb.toString());
                    }
                }
            }
        } catch (Exception e) {
            errList.add(e.getMessage());
            logger.error(e.getMessage(), e);
        }
        return errList;
    }

    @ApiOperation(value = "用户导入-随机密码", notes = "用户导入-随机密码", tags = {"用户导入", "组织管理--->用户"})
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    @ResponseBody
    public String importUser(@RequestParam(value = "upload") MultipartFile excelFile,
                             @RequestParam(value = "verId") String verId, @RequestParam(value = "fun", required = false) String fun,
                             HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserImportDto result = this.importUserReturn(excelFile, verId, null, request, response);
        if (StringUtils.isNotBlank(fun)) {
            fun = HtmlUtils.htmlUnescape(fun);
            fun = fun.replace("{data}", JSONArray.toJSONString(result));
            return fun;
        }
        return JSONArray.toJSONString(result);

    }

    @ApiOperation(value = "用户导入-自定义密码", notes = "用户导入-自定义密码", tags = {"用户导入", "组织管理--->用户"})
    @RequestMapping(value = "/import_user_defined_pwd", method = RequestMethod.POST)
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userDefinedPwd", value = "自定义密码 加密处理", paramType = "query", dataType = "String", required = false)})
    public String importUserAndUserDefinedPwd(@RequestParam(value = "upload") MultipartFile excelFile,
                                              @RequestParam(value = "verId") String verId, @RequestParam(value = "fun", required = false) String fun,
                                              @RequestParam(value = "userDefinedPwd", required = false) String userDefinedPwd, HttpServletRequest request,
                                              HttpServletResponse response) throws Exception {
        UserImportDto result = this.importUserReturn(excelFile, verId, userDefinedPwd, request, response);
        if (StringUtils.isNotBlank(fun)) {
            fun = HtmlUtils.htmlUnescape(fun);
            fun = fun.replace("{data}", JSONArray.toJSONString(result));
            return fun;
        }
        return JSONArray.toJSONString(result);
    }

    /**
     * 导入用户处理
     *
     * @param excelFile
     * @param verId
     * @param userDefinedPwd 自定义密码 加密处理 （自定义密码模式才有值，其他模式填null）
     * @return java.lang.String
     **/
    private UserImportDto importUserReturn(MultipartFile excelFile, String verId, String userDefinedPwd,
                                           HttpServletRequest request, HttpServletResponse response) throws Exception {
        Sheet sheet = ExcelUtils.getSheetFromInputStream(excelFile.getInputStream(), "用户");
        UserImportDto userImportDto = new UserImportDto();
        if (StringUtils.isNotBlank(userDefinedPwd)) {
            userDefinedPwd = PwdUtils.decodePwdBybase64AndUnicode(userDefinedPwd);
        }
        if (sheet == null) {
            userImportDto.setSuccess(Boolean.FALSE);
            userImportDto.setMessage("上传的excel解析失败，请检查文件。");
            return userImportDto;
        }
        MultiOrgVersion ver = this.multiOrgVersionService.getById(verId);
        if (ver == null) {
            userImportDto.setSuccess(Boolean.FALSE);
            userImportDto.setMessage("对应的组织版本不存在");
            return userImportDto;
        }
        // 获取该版本组织树
        OrgTreeNode orgTree = this.multiOrgService.getOrgAsTreeByVersionId(verId);
        // 获取该组织下的所有职位
        List<TreeNode> jobList = orgTree.queryChildNodeListByTypeAndRemoveRepeat(IdPrefix.JOB.getValue(), true);
        if (CollectionUtils.isEmpty(jobList)) {
            userImportDto.setSuccess(Boolean.FALSE);
            userImportDto.setMessage("该组织版本还未初始化数据，请先进行配置");
            return userImportDto;
        }
        // 转成map, 方便使用
        HashMap<String, OrgTreeNodeDto> jobMap = Maps.newHashMap();
        for (TreeNode treeNode : jobList) {
            OrgTreeNodeDto dto = (OrgTreeNodeDto) treeNode.getData();
            jobMap.put(dto.getEleNamePath(), dto);
        }
        // 检查数据格式是否正确
        List<String> errList = checkImportData(sheet, jobMap, ver);
        if (!errList.isEmpty()) {
            userImportDto.setSuccess(Boolean.FALSE);
            userImportDto.setMessage(StringUtils.join(errList, "<br/>"));
            return userImportDto;
        }
        String systemUnitId = SpringSecurityUtils.getCurrentUserUnitId();

        // 密码规则配置
        MultiOrgPwdSettingDto multiOrgPwdSettingDto = multiOrgPwdSettingFacadeService.getMultiOrgPwdSetting();
        if (StringUtils.isBlank(multiOrgPwdSettingDto.getUuid())) {
            userImportDto.setSuccess(Boolean.FALSE);
            userImportDto.setMessage("密码规则数据为空，无法操作，请联系管理员保存密码规则！");
            return userImportDto;
        }
        // map key:loginname value 新账号导出对象
        Map<String, NewAccountDto> newAccountMap = Maps.newHashMap();
        String result = "";
        try {
            // 清洗数据，数据去重，标准化格式，以及补充缺漏的部门节点数据
            int maxRow = sheet.getLastRowNum();
            // 第一行是标题，所以是从第二行开始读取
            for (int rowNum = 1; rowNum <= maxRow; rowNum++) {
                Row row = sheet.getRow(rowNum);
                // 检查数据类型，并利用map剔除重复数据;同时清洗数据格式，
                if (row != null) {
                    ImportUser inode = new ImportUser(row);
                    MultiOrgUserAccount account = this.orgApiFacade.getAccountByLoginName(inode.getLoginName());
                    if (account == null) { // 新账号，添加
                        OrgUserVo vo = new OrgUserVo();
                        importUser2OrgUserVo(inode, vo, true);
                        vo.setSystemUnitId(systemUnitId);
                        // 编号不能为空，所以没有的话，就用系统当前系统时间赋值
                        if (StringUtils.isBlank(vo.getCode())) {
                            vo.setCode(System.currentTimeMillis() + "");
                        }
                        // 设置职位信息
                        if (StringUtils.isNotBlank(inode.getJobName())) {
                            OrgTreeNodeDto jobDto = jobMap.get(ver.getName() + "/" + inode.getJobPath());
                            String jobId = jobDto.getOrgVersionId() + "/" + jobDto.getEleId();
                            vo.setMainJobId(jobId);
                        }
                        if (AdminSetPwdTypeEnum.RandomPwd.getValue()
                                .equals(multiOrgPwdSettingDto.getAdminSetPwdType())) {
                            // 随机密码
                            String randomPwd = PwdUtils.generatePwdByRoleByCkeckPwdRole(multiOrgPwdSettingDto);
                            vo.setPassword(randomPwd);
                            OrgUserVo orgUserVo = this.multiOrgService.addUser(vo);

                            NewAccountDto newAccountDto = newAccountMap.get(vo.getLoginName());
                            if (newAccountDto == null) {
                                newAccountDto = new NewAccountDto();
                                newAccountDto.setLoginName(vo.getLoginName());
                                newAccountDto.setPassword(randomPwd);
                                newAccountDto.setUserName(vo.getUserName());
                                newAccountMap.put(newAccountDto.getLoginName(), newAccountDto);
                            }
                        } else {
                            // 自定义密码
                            vo.setPassword(userDefinedPwd);
                            this.multiOrgService.addUser(vo);
                        }

                    } else { // 旧账号，更新
                        // 需要重新判断，因为有可能正在导入的时候，刚好有其他家单位创建了同样的账号名
                        if (!account.getSystemUnitId().equals(systemUnitId)) {
                            throw new WellException(account.getLoginName() + "账号已被其他单位使用，请换一个");
                        }
                        OrgUserVo vo = this.orgApiFacade.getUserVoById(account.getId());
                        // 更新信息
                        importUser2OrgUserVo(inode, vo, false);
                        // 追加职位信息到其他职位上
                        if (StringUtils.isNotBlank(inode.getJobName())) {
                            OrgTreeNodeDto jobDto = jobMap.get(ver.getName() + "/" + inode.getJobPath());
                            String jobId = jobDto.getOrgVersionId() + "/" + jobDto.getEleId();
                            // 判断该用户该职位是否已经存在, 若已存在 ，无需追加
                            String allJobId = vo.getMainJobId() + ";" + vo.getOtherJobIds();
                            if (allJobId.indexOf(jobId) == -1) {
                                String otherJobIds = vo.getOtherJobIds();
                                if (StringUtils.isBlank(otherJobIds)) {
                                    otherJobIds = jobId;
                                } else {
                                    otherJobIds = otherJobIds + ";" + jobId;
                                }
                                vo.setOtherJobIds(otherJobIds);
                            }
                        }
                        vo.setPassword(null);// 旧账号密码不更新
                        this.multiOrgService.modifyUser(vo);
                    }

                }
            }

            // 收集到的用户数据，导出到前端
            if (AdminSetPwdTypeEnum.RandomPwd.getValue().equals(multiOrgPwdSettingDto.getAdminSetPwdType())) {
                // 随机密码模式要导出新用户和密码
                // 导入
                if (newAccountMap.size() == 0) {
                    userImportDto.setSuccess(Boolean.TRUE);
                    userImportDto.setMessage("");
                } else {
                    List<String[]> dataList = multiOrgUserAccountFacadeService.getNewUserAccountDataList(newAccountMap);
                    try {
                        // ExportAccountUtils.exportPwdAccounts(ExportAccountUtils.pwdTitles, dataList,
                        // request, response);
                        HSSFWorkbook excel = ExcelUtils.generateExcelBook(dataList, ExportAccountUtils.pwdTitles);
                        result = multiOrgUserAccountFacadeService.uploadExcel(excel);
                        userImportDto.setSuccess(Boolean.TRUE);
                        userImportDto.setMessage(result);
                    } catch (Exception e) {
                        logger.error("导出用户列表失败：", e);
                    }
                    // ExportAccountUtils.exportPwdAccounts(ExportAccountUtils.pwdTitles, dataList,
                    // request, response);
                }
            } else {
                userImportDto.setSuccess(Boolean.TRUE);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = "导入异常，请联系管理员";
            userImportDto.setSuccess(Boolean.FALSE);
            userImportDto.setMessage(result);
        } finally {
            return userImportDto;
        }

    }

    /**
     * 获取要导出的excel所有数据集合
     *
     * @param newAccountMap
     * @return java.util.List<java.lang.String [ ]>
     **/
    private List<String[]> getNewUserAccountDataList(Map<String, NewAccountDto> newAccountMap) {
        List<String[]> dataList = Lists.newArrayList();
        Map<String, MultiOrgUserAccount> userAccountMap = new HashMap<>();
        List<String> loginNames = getLoginNames(newAccountMap);
        List<MultiOrgUserAccount> userAccountList = multiOrgUserAccountService
                .getUserAccountListByloginNames(loginNames);
        for (MultiOrgUserAccount userAccount : userAccountList) {
            userAccountMap.put(userAccount.getId(), userAccount);
        }

        StringBuilder userInfohql = new StringBuilder("from MultiOrgUserInfo where ");
        Map<String, Object> userInfoParam = new HashMap<>();
        HqlUtils.appendSql("userId", userInfoParam, userInfohql, Sets.<Serializable>newHashSet(userAccountMap.keySet()));
        List<MultiOrgUserInfo> userInfoList = multiOrgUserInfoService.listByHQL(userInfohql.toString(), userInfoParam);
        Map<String, MultiOrgUserInfo> userInfoMap = new HashMap<>();
        for (MultiOrgUserInfo userInfo : userInfoList) {
            userInfoMap.put(userInfo.getUserId(), userInfo);
        }
        StringBuilder userTreeHql = new StringBuilder("from MultiOrgUserTreeNode where ");
        Map<String, Object> userTreeParam = new HashMap<>();
        HqlUtils.appendSql("userId", userTreeParam, userTreeHql, Sets.<Serializable>newHashSet(userAccountMap.keySet()));
        List<MultiOrgUserTreeNode> userTreeNodeList = multiOrgUserTreeNodeService.listByHQL(userTreeHql.toString(),
                userTreeParam);
        Multimap<String, String> userJobMap = HashMultimap.create();
        for (MultiOrgUserTreeNode userTreeNode : userTreeNodeList) {
            userJobMap.put(userTreeNode.getUserId(), userTreeNode.getEleId());
        }
        StringBuilder jobTreeHql = new StringBuilder("from MultiOrgTreeNode where ");
        Map<String, Object> jobTreeParam = new HashMap<>();
        HqlUtils.appendSql("eleId", jobTreeParam, jobTreeHql, Sets.<Serializable>newHashSet(userJobMap.values()));
        List<MultiOrgTreeNode> orgTreeNodeList = multiOrgTreeNodeService.listByHQL(jobTreeHql.toString(), jobTreeParam);
        Map<String, String[]> jobIdMap = new HashMap<>();
        Set<String> eleIdSet = new HashSet<>();
        for (MultiOrgTreeNode treeNode : orgTreeNodeList) {
            String eleIdPath = treeNode.getEleIdPath();
            String[] eleIdPaths = eleIdPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
            String[] jobEleIdPath = new String[eleIdPaths.length - 1];
            for (int i = 1; i < eleIdPaths.length; i++) {
                eleIdSet.add(eleIdPaths[i]);
                jobEleIdPath[i - 1] = eleIdPaths[i];
            }
            jobIdMap.put(treeNode.getEleId(), jobEleIdPath);
        }
        Map<String, MultiOrgElement> elementMap = new HashMap<>();
        StringBuilder elementHql = new StringBuilder("from MultiOrgElement where ");
        Map<String, Object> elementParam = new HashMap<>();
        HqlUtils.appendSql("id", elementParam, elementHql, Sets.<Serializable>newHashSet(eleIdSet));
        List<MultiOrgElement> elementList = multiOrgElementService.listByHQL(elementHql.toString(), elementParam);
        for (MultiOrgElement multiOrgElement : elementList) {
            elementMap.put(multiOrgElement.getId(), multiOrgElement);
        }
        for (MultiOrgUserAccount userAccount : userAccountList) {

            String loginName = userAccount.getLoginName();
            String userName = userAccount.getUserName();

            MultiOrgUserInfo userInfo = userInfoMap.get(userAccount.getId());
            String mobile = null;
            String email = null;
            String employeeNumber = null;
            String homePhone = null;
            String officePhone = null;
            String idNumber = null;
            String englishName = null;
            String sex = null;
            if (userInfo != null) {
                mobile = userInfo.getMobilePhone();
                email = userInfo.getMainEmail();
                employeeNumber = userInfo.getEmployeeNumber();
                homePhone = userInfo.getHomePhone();
                officePhone = userInfo.getOfficePhone();
                idNumber = userInfo.getIdNumber();
                englishName = userInfo.getEnglishName();
                if (userInfo.getSex() != null) {
                    sex = userInfo.getSex().equals("1") ? "男" : "女";
                }
            }

            if (userJobMap.containsKey(userAccount.getId())) {
                for (String jobId : userJobMap.get(userAccount.getId())) {
                    String[] eleIdPaths = jobIdMap.get(jobId);
                    if (eleIdPaths == null) {
                        continue;
                    }
                    String[] eleNames = new String[eleIdPaths.length];
                    for (int i = 0; i < eleIdPaths.length; i++) {
                        MultiOrgElement element = elementMap.get(eleIdPaths[i]);
                        if (element != null) {
                            eleNames[i] = element.getName();
                        }
                    }
                    String unitName = eleNames[0];
                    String deptPath = StringUtils.join(eleNames, "/", 1, eleNames.length - 1);
                    ;
                    String jobName = eleNames[eleNames.length - 1];

                    String pwd = newAccountMap.get(userAccount.getLoginName()).getPassword();
                    String[] data = new String[]{unitName, deptPath, jobName, loginName, userName, pwd, sex, mobile,
                            email, employeeNumber, homePhone, officePhone, idNumber, englishName};
                    dataList.add(data);
                }
            } else {

                String pwd = newAccountMap.get(userAccount.getLoginName()).getPassword();
                String[] data = new String[]{null, null, null, loginName, userName, pwd, sex, mobile, email,
                        employeeNumber, homePhone, officePhone, idNumber, englishName};
                dataList.add(data);
            }
        }

        return dataList;
    }

    /**
     * 获取所有的登录名集合
     *
     * @param
     * @return java.util.List<java.lang.String>
     **/
    private List<String> getLoginNames(Map<String, NewAccountDto> newAccountMap) {
        List<String> loginNames = new ArrayList<>();
        for (String key : newAccountMap.keySet()) {
            NewAccountDto newAccountDto = newAccountMap.get(key);
            loginNames.add(newAccountDto.getLoginName());
        }
        return loginNames;
    }

    private void importUser2OrgUserVo(ImportUser inode, OrgUserVo vo, boolean isNew) {
        if (isNew) {
            vo.setLoginName(inode.getLoginName());
            vo.setType(0); // 普通账号
            vo.setIsForbidden(0);
        }
        BeanUtils.copyProperties(inode, vo, new String[]{"loginName", "jobPath", "code"});
        vo.setSex("男".equals(inode.getSex()) ? "1" : "0");
        if (StringUtils.isNotBlank(inode.getCode())) {
            vo.setEmployeeNumber(inode.getCode());
            vo.setCode(inode.getCode());
        }
    }

    private void newExport(String userUuids, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String fileName = "用户_" + DateUtil.getFormatDate(new Date(), "yyyy-MM-dd") + ".xls";
        String systemUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        Map<String, MultiOrgUserAccount> userAccountMap = new HashMap<>();
        List<MultiOrgUserAccount> userAccountList = new ArrayList<>();
        if (StringUtils.isBlank(userUuids)) {
            // 导出全部用户
            userAccountList = this.orgApiFacade.queryAllAccountOfUnit(systemUnitId);
        } else {
            userAccountList = multiOrgUserAccountService.listByUuids(Lists.newArrayList(userUuids.split(";")));
        }
        for (MultiOrgUserAccount userAccount : userAccountList) {
            userAccountMap.put(userAccount.getId(), userAccount);
        }
        StringBuilder userInfohql = new StringBuilder("from MultiOrgUserInfo where ");
        Map<String, Object> userInfoParam = new HashMap<>();
        HqlUtils.appendSql("userId", userInfoParam, userInfohql, Sets.<Serializable>newHashSet(userAccountMap.keySet()));
        List<MultiOrgUserInfo> userInfoList = multiOrgUserInfoService.listByHQL(userInfohql.toString(), userInfoParam);
        Map<String, MultiOrgUserInfo> userInfoMap = new HashMap<>();
        for (MultiOrgUserInfo userInfo : userInfoList) {
            userInfoMap.put(userInfo.getUserId(), userInfo);
        }
        StringBuilder userTreeHql = new StringBuilder("from MultiOrgUserTreeNode where ");
        Map<String, Object> userTreeParam = new HashMap<>();
        HqlUtils.appendSql("userId", userTreeParam, userTreeHql, Sets.<Serializable>newHashSet(userAccountMap.keySet()));
        List<MultiOrgUserTreeNode> userTreeNodeList = multiOrgUserTreeNodeService.listByHQL(userTreeHql.toString(),
                userTreeParam);
        Multimap<String, String> userJobMap = HashMultimap.create();
        for (MultiOrgUserTreeNode userTreeNode : userTreeNodeList) {
            userJobMap.put(userTreeNode.getUserId(), userTreeNode.getEleId());
        }
        StringBuilder jobTreeHql = new StringBuilder("from MultiOrgTreeNode where ");
        Map<String, Object> jobTreeParam = new HashMap<>();
        HqlUtils.appendSql("eleId", jobTreeParam, jobTreeHql, Sets.<Serializable>newHashSet(userJobMap.values()));
        List<MultiOrgTreeNode> orgTreeNodeList = multiOrgTreeNodeService.listByHQL(jobTreeHql.toString(), jobTreeParam);
        Map<String, String[]> jobIdMap = new HashMap<>();
        Set<String> eleIdSet = new HashSet<>();
        for (MultiOrgTreeNode treeNode : orgTreeNodeList) {
            String eleIdPath = treeNode.getEleIdPath();
            String[] eleIdPaths = eleIdPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
            String[] jobEleIdPath = new String[eleIdPaths.length - 1];
            for (int i = 1; i < eleIdPaths.length; i++) {
                eleIdSet.add(eleIdPaths[i]);
                jobEleIdPath[i - 1] = eleIdPaths[i];
            }
            jobIdMap.put(treeNode.getEleId(), jobEleIdPath);
        }
        Map<String, MultiOrgElement> elementMap = new HashMap<>();
        StringBuilder elementHql = new StringBuilder("from MultiOrgElement where ");
        Map<String, Object> elementParam = new HashMap<>();
        HqlUtils.appendSql("id", elementParam, elementHql, Sets.<Serializable>newHashSet(eleIdSet));
        List<MultiOrgElement> elementList = multiOrgElementService.listByHQL(elementHql.toString(), elementParam);
        for (MultiOrgElement multiOrgElement : elementList) {
            elementMap.put(multiOrgElement.getId(), multiOrgElement);
        }
        List<String[]> dataList = Lists.newArrayList();
        for (MultiOrgUserAccount userAccount : userAccountList) {

            String loginName = userAccount.getLoginName();
            String loginNameZh = userAccount.getLoginNameZh();
            String userName = userAccount.getUserName();

            MultiOrgUserInfo userInfo = userInfoMap.get(userAccount.getId());
            String mobile = null;
            String email = null;
            String employeeNumber = null;
            String homePhone = null;
            String officePhone = null;
            String idNumber = null;
            String englishName = null;
            String sex = null;
            if (userInfo != null) {
                mobile = userInfo.getMobilePhone();
                email = userInfo.getMainEmail();
                employeeNumber = userInfo.getEmployeeNumber();
                homePhone = userInfo.getHomePhone();
                officePhone = userInfo.getOfficePhone();
                idNumber = userInfo.getIdNumber();
                englishName = userInfo.getEnglishName();
                if (userInfo.getSex() != null) {
                    sex = userInfo.getSex().equals("1") ? "男" : "女";
                }
            }

            if (userJobMap.containsKey(userAccount.getId())) {
                for (String jobId : userJobMap.get(userAccount.getId())) {
                    String[] eleIdPaths = jobIdMap.get(jobId);
                    if (eleIdPaths == null) {
                        continue;
                    }
                    String[] eleNames = new String[eleIdPaths.length];
                    for (int i = 0; i < eleIdPaths.length; i++) {
                        MultiOrgElement element = elementMap.get(eleIdPaths[i]);
                        if (element != null) {
                            eleNames[i] = element.getName();
                        }
                    }
                    String unitName = eleNames[0];
                    String deptPath = StringUtils.join(eleNames, "/", 1, eleNames.length - 1);
                    ;
                    String jobName = eleNames[eleNames.length - 1];
                    String[] data = new String[]{unitName, deptPath, jobName, loginName, loginNameZh, userName, sex, mobile, email,
                            employeeNumber, homePhone, officePhone, idNumber, englishName};
                    dataList.add(data);
                }
            } else {
                String[] data = new String[]{null, null, null, loginName, loginNameZh, userName, sex, mobile, email,
                        employeeNumber, homePhone, officePhone, idNumber, englishName};
                dataList.add(data);
            }
        }
        String[] titles = new String[]{"单位", "部门路径", "职位", "账号", "中文账号", "姓名", "性别", "手机号", "电子邮件", "员工编号", "家庭电话", "办公电话",
                "身份证号", "英文名"};
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            HSSFWorkbook excel = ExcelUtils.generateExcelBook(dataList, titles);
            excel.write(os);
            FileDownloadUtils.download(request, response, new ByteArrayInputStream(os.toByteArray()), fileName);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        } finally {
            os.close();
        }
    }

    @RequestMapping(value = "/export", method = RequestMethod.POST)
    public void export(@RequestParam(required = false) String userUuids, HttpServletRequest request,
                       HttpServletResponse response) throws Exception {
        // this.oldExport(userUuids, request, response);
        this.newExport(userUuids, request, response);
    }

    private void oldExport(String userUuids, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String fileName = "用户_" + DateUtil.getFormatDate(new Date(), "yyyy-MM-dd") + ".xls";
        String systemUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        List<String> uuidList = Lists.newArrayList();
        if (StringUtils.isBlank(userUuids)) {
            // 导出全部用户
            List<MultiOrgUserAccount> users = this.orgApiFacade.queryAllAccountOfUnit(systemUnitId);
            for (MultiOrgUserAccount a : users) {
                uuidList.add(a.getUuid());
            }
        } else {
            uuidList = Lists.newArrayList(userUuids.split(";"));
        }
        String[] titles = new String[]{"单位", "部门路径", "职位", "账号", "姓名", "性别", "手机号", "电子邮件", "员工编号", "家庭电话", "办公电话",
                "身份证号", "英文名"};
        Map<String, MultiOrgElement> allEleMap = Maps.newHashMap();
        List<MultiOrgVersion> vers = this.multiOrgVersionService
                .queryCurrentActiveVersionListOfSystemUnit(systemUnitId);
        for (MultiOrgVersion ver : vers) {
            Map<String, MultiOrgElement> map = this.multiOrgElementService.queryElementMapByOrgVersion(ver.getId());
            if (!CollectionUtils.isEmpty(map)) {
                allEleMap.putAll(map);
            }
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            List<String[]> dataList = Lists.newArrayList();
            for (String uuid : uuidList) {
                OrgUserVo u = this.multiOrgService.getUser(uuid);
                if (u != null) {
                    // 判断用户的职位，如果有多个职位，需要插入多条记录
                    if (CollectionUtils.isEmpty(u.getJobList())) {
                        String[] data = getExportUserData(u, null);
                        dataList.add(data);
                    } else {
                        for (OrgUserJobDto job : u.getJobList()) {
                            job.getOrgTreeNodeDto().computeEleNamePath(allEleMap);
                            String[] data = getExportUserData(u, job.getOrgTreeNodeDto());
                            dataList.add(data);
                        }
                    }
                }
            }

            HSSFWorkbook excel = ExcelUtils.generateExcelBook(dataList, titles);
            excel.write(os);
            FileDownloadUtils.download(request, response, new ByteArrayInputStream(os.toByteArray()), fileName);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        } finally {
            os.close();
        }
    }

    private String[] getExportUserData(OrgUserVo vo, OrgTreeNodeDto jobDto) {
        String unitName = null;
        String deptPath = null;
        String jobName = null;
        if (jobDto != null) {
            String[] names = jobDto.getEleNamePath().split("/");
            unitName = names[1];
            deptPath = StringUtils.join(names, "/", 2, names.length - 1);
            jobName = jobDto.getName();
        }
        String loginName = vo.getLoginName();
        String userName = vo.getUserName();
        String sex = null;
        if (vo.getSex() != null) {
            sex = vo.getSex().equals("1") ? "男" : "女";
        }
        String mobile = vo.getMobilePhone();
        String email = vo.getMainEmail();
        String employeeNumber = vo.getEmployeeNumber();
        String homePhone = vo.getHomePhone();
        String officePhone = vo.getOfficePhone();
        String idNumber = vo.getIdNumber();
        String englishName = vo.getEnglishName();
        return new String[]{unitName, deptPath, jobName, loginName, userName, sex, mobile, email, employeeNumber,
                homePhone, officePhone, idNumber, englishName};
    }

    // 上传头像
    @RequestMapping(value = "/upload/photo", method = RequestMethod.POST)
    @ResponseBody
    public void uploadPhoto(@RequestParam(value = "upload") MultipartFile upload, HttpServletResponse response)
            throws IOException {
        // 上传处理
        MongoFileEntity fileEntity = mongoFileService.saveFile(upload.getOriginalFilename(), upload.getInputStream());

        BufferedImage image = ImageIO.read(fileEntity.getInputstream());
        if (image == null) { // 如果不是图片格式
            response.setContentType(MediaType.TEXT_HTML_VALUE);
            response.getWriter().write("");
            response.getWriter().flush();
            response.getWriter().close();
        } else {
            response.setContentType(MediaType.TEXT_HTML_VALUE);
            response.getWriter().write(fileEntity.getId());
            response.getWriter().flush();
            response.getWriter().close();
        }
    }

    // 查看头像
    @RequestMapping(value = "/view/photo/{nodeUuid}")
    @ResponseBody
    public void viewPhoto(@PathVariable(value = "nodeUuid") String nodeUuid, HttpServletResponse response) {
        response.setContentType("image/jpeg"); // 必须设置ContentType为image/jpeg
        // response.setHeader("Pragma", "No-cache");
        // response.setHeader("Cache-Control", "no-cache");
        // response.setDateHeader("Expires", 0);
        InputStream is = null;
        OutputStream os = null;
        try {
            if (StringUtils.isNotBlank(nodeUuid) && !"null".equals(nodeUuid)) {
                // 上传处理
                MongoFileEntity fileEntity = mongoFileService.getFile(nodeUuid);
                if (fileEntity != null) {
                    is = fileEntity.getInputstream();
                    os = response.getOutputStream();
                    if (is != null) {
                        IOUtils.write(IOUtils.toByteArray(is), os);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        } finally {
            if (is != null) {
                IOUtils.closeQuietly(is);
            }
            if (os != null) {
                IOUtils.closeQuietly(os);
            }
        }
    }

}
