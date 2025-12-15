/*
 * @(#)2012-12-25 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.unit.service.impl;

import com.google.common.collect.Sets;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.org.entity.Department;
import com.wellsoft.pt.org.entity.Option;
import com.wellsoft.pt.org.entity.User;
import com.wellsoft.pt.org.service.OptionService;
import com.wellsoft.pt.org.service.UserService;
import com.wellsoft.pt.org.unit.service.UnitTreeService;
import com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService;
import com.wellsoft.pt.org.unit.support.UnitTreeDataProviderDelegateService;
import com.wellsoft.pt.security.service.SessionService;
import com.wellsoft.pt.unit.service.impl.CommonUnitTreeServiceImpl;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-12-25.1	zhulh		2012-12-25		Create
 * </pre>
 * @date 2012-12-25
 */
@Service
@Transactional
public class UnitTreeServiceImpl implements UnitTreeService {
    public static final String SHOW_GROUP = "true";
    public static final String TYPE_USER = "U";
    public static final String TYPE_DEP = "D";
    public static final String TYPE_GROUP = "G";
    public static final String TYPE_JOB = "J";
    public static final String TYPE_DUTY = "W";
    public static final String NODE_UNITS = "units";
    public static final String NODE_UNIT = "unit";
    public static final String NODE_TYPES = "types";
    public static final String NODE_TYPE = "type";
    public static final String ATTRIBUTE_TYPE = "type";
    public static final String ATTRIBUTE_ISLEAF = "isLeaf";
    public static final String ATTRIBUTE_ID = "id";
    public static final String ATTRIBUTE_NAME = "name";
    public static final String ATTRIBUTE_PATH = "path";
    public static final String ATTRIBUTE_SEX = "sex";
    public static final String ATTRIBUTE_JOB = "job";
    public static final String ATTRIBUTE_TITLE = "title";
    public static final String ATTRIBUTE_EMAIL = "email";
    public static final String ATTRIBUTE_EMPLOYEENUMBER = "employeeNumber";
    public static final String ATTRIBUTE_LOGINNAME = "loginName";
    // id="唯一编号" name="名称" path="全路径" sex="性别(1-男/2-女/3-离线/0-禁用)"
    // job="岗位名称(若多部门则显示主岗)" title
    public static final String ATTRIBUTE_TYPE_DEP = "1";
    public static final String ATTRIBUTE_TYPE_USER = "2";
    public static final String ATTRIBUTE_TYPE_GROUP = "3";
    public static final String ATTRIBUTE_TYPE_ORG_UNIT = "4";
    public static final String ATTRIBUTE_TYPE_CATEGORY = "5";// 分类
    public static final String ATTRIBUTE_TYPE_JOB = "6";// 岗位
    public static final String ATTRIBUTE_TYPE_DUTY = "7";// 职务
    public static final String ATTRIBUTE_TYPE_UNIT = "8";// 单位
    public static final String TRUE = "1";
    public static final String FALSE = "0";
    public static final String SEX_MALE = "1";
    public static final String SEX_FEMALE = "2";
    public static final String SEX_UNENABLE = "0";
    public static final String SEX_LOGOUT = "3";
    public static final String ID_SPLIT = ";";
    public static final String ID_ALL = "All";
    public static final String ID_MYUNIT = "MyUnit1"; // MYUNIT使用Myunit1
    public static final String ID_MYDEPT = "MyDept";
    public static final String ID_MYLEADER = "MyLeader";
    public static final String ID_MYDIRECTLEADER = "MyDirectLeader";
    public static final String ID_MYUNDERLING = "MyUnderling";
    public static final String ID_PUBLICGROUP = "PublicGroup";
    public static final String ID_PRIVATEGROUP = "PrivateGroup";
    public static final String ID_MYPARENTDEPT = "MyParentDept";
    public static final String ID_ONLINE_USER = "OnlineUser";
    public static final String ID_MIXTRUE = "Mixture";
    public static final String ID_DEPT = "Dept";
    public static final String ID_GROUP = "Group";
    public static final String ID_ORG_UNIT = "OrgUnit";
    public static final String ID_COMMON_UNIT = "Unit";
    public static final String ID_COMMON_UNIT_USER = "UnitUser";
    public static final String ID_JOB = "Job";// 岗位 201408174add
    public static final String ID_DUTY = "Duty";// 岗位 201408174add
    public static final String ID_JOBDUTY = "JobDuty";// 岗位 201408174add
    public static final String ID_MYUNIT1 = "MyUnit";// 我的单位(职位)
    public static final String LOGIN_TRUE = "1";
    public static final String LOGIN_FALSE = "0";
    public static final String ALL_FULL = "2";
    public static final String ALL_SELF = "1";
    public static final String ALL_CHILD = "0";
    // 递归所有层次
    public static final String LEVEL_ALL = "1";
    // 只递归儿子
    public static final String LEVEL_CHILD = "2";
    // 不递归，只返回本身
    public static final String LEVEL_SELF = "3";
    // 目前应该有两种类型一个是取末端的类型，这个通过界面部分传递过来
    // 另外一个是判断叶子节点的类型，这个通过逻辑来处理。
    public String typeLeaf = TYPE_USER;
    public String typeEnd;
    public String all = ALL_FULL;
    public String login;
    @Autowired
    private UserService userService;
    private Set<Department> depSet = Sets.newHashSet();
    private Set<User> userSet = Sets.newHashSet();

    @Autowired
    private UnitTreeMixtrueTypeDelegateServiceImpl unitTreeMixtrueTypeDelegateServiceImpl;
    @Autowired
    private SessionService sessionService;

    @Autowired
    private OrgApiFacade orgApiFacade;

    @Autowired
    private OptionService optionService;
    @Autowired(required = false)
    private List<UnitTreeDataProviderDelegateService> unitTreeDataProviders;

    private Map<String, UnitTreeDataProviderDelegateService> unitTreeDataProviderMap = new HashMap<String, UnitTreeDataProviderDelegateService>();

    @Override
    public Document parserType(String isShowGroup, String psType) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(NODE_TYPES);
        List<Option> options = optionService.getAll();

        for (Option option : options) {
            if (!SHOW_GROUP.equals(isShowGroup)) {
                // update by liz reason：以是否显示为准
                if (Boolean.TRUE.equals(option.getShow()) || option.getId().equals(psType)) {
                    Element element = root.addElement(NODE_TYPE);
                    element.addAttribute(ATTRIBUTE_ID, option.getId());
                    element.addText(option.getName());
                }
            } else {
                Element element = root.addElement(NODE_TYPE);
                element.addAttribute(ATTRIBUTE_ID, option.getId());
                element.addText(option.getName());
            }
        }
        return document;
    }

    /**
     * 如何描述该方法
     * 修改2015-01-16 @yuyq
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.unit.service.UnitTreeService#searchXml(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Document searchXml(String optionType, String all, String login, String searchValue, String filterCondition,
                              HashMap<String, String> filterDisplayMap) {
        // 搜索部门
        if (ID_DEPT.equals(optionType)) {
            return ApplicationContextHolder.getBean("unitTreeDeptTypeDelegateService",
                    UnitTreeDeptTypeDelegateServiceImpl.class).searchXml(optionType, all, login, searchValue,
                    filterDisplayMap);
        } else if (ID_COMMON_UNIT.equals(optionType)) {
            return ApplicationContextHolder.getBean("unitTreeUnitTypeDelegateService",
                    UnitTreeUnitTypeDelegateServiceImpl.class).searchXml(optionType, all, login, searchValue,
                    filterDisplayMap);
        } else if (ID_MYUNIT.equals(optionType) // ||
                // ID_MYLEADER.equals(optionType)
                // || ID_MYUNDERLING.equals(optionType) //||
                // ID_PUBLICGROUP.equals(optionType)
                // || ID_PRIVATEGROUP.equals(optionType) //||
                // ID_MYPARENTDEPT.equals(optionType)
                // || ID_ONLINE_USER.equals(optionType)
                || ID_GROUP.equals(optionType)
                || ID_ORG_UNIT.equals(optionType)
                || ID_COMMON_UNIT_USER.equals(optionType)) {
            // 搜索用户
            return ApplicationContextHolder.getBean("unitTreeMyUnit1TypeDelegateService",
                    UnitTreeMyUnit1TypeDelegateServiceImpl.class).searchXml(optionType, all, login, searchValue,
                    filterDisplayMap);
        } else if (ID_JOB.equals(optionType)) {
            return ApplicationContextHolder.getBean("unitTreeJobTypeDelegateService",
                    UnitTreeJobTypeDelegateServiceImpl.class).searchXml(optionType, all, login, searchValue,
                    filterDisplayMap);
        } else if (ID_PUBLICGROUP.equals(optionType)) {
            // 公共群组，搜索公共群组.
            // return
            // ApplicationContextHolder.getBean("unitTreePublicGroupTypeDelegateService",
            // UnitTreePublicGroupTypeDelegateServiceImpl.class).searchXml(optionType,
            // all, login, searchValue,
            // filterCondition);
        } else if (ID_PRIVATEGROUP.equals(optionType)) {
            // 个人群组，搜索个人群组.
            // return
            // ApplicationContextHolder.getBean("unitTreePrivateGroupTypeDelegateService",
            // UnitTreePrivateGroupTypeDelegateServiceImpl.class).searchXml(optionType,
            // all, login, searchValue,
            // filterDisplayMap);
        } else if (ID_DUTY.equals(optionType)) {
            return ApplicationContextHolder.getBean("unitTreeDutyTypeDelegateService",
                    UnitTreeDutyTypeDelegateServiceImpl.class).searchXml(optionType, all, login, searchValue,
                    filterDisplayMap);
        } else if (ID_JOBDUTY.equals(optionType)) {
            return ApplicationContextHolder.getBean("unitTreeJobDutyTypeDelegateService",
                    UnitTreeJobDutyTypeDelegateServiceImpl.class).searchXml(optionType, all, login, searchValue,
                    filterDisplayMap);
        } else if (ID_MYUNIT1.equals(optionType)) {
            return ApplicationContextHolder.getBean("unitTreeMyUnit1TypeDelegateService",
                    UnitTreeMyUnit1TypeDelegateServiceImpl.class).searchXml(optionType, all, login, searchValue,
                    filterDisplayMap);
        } else if (ID_MYDEPT.equals(optionType)) {// 我的部门
            return ApplicationContextHolder.getBean("unitTreeMyDeptTypeDelegateService",
                    UnitTreeMyDeptTypeDelegateServiceImpl.class).searchXml(optionType, all, login, searchValue,
                    filterDisplayMap);
        } else if (ID_MYLEADER.equals(optionType)) {// 我的所有领导
            return ApplicationContextHolder.getBean("unitTreeMyLeaderTypeDelegateService",
                    UnitTreeMyLeaderTypeDelegateServiceImpl.class).searchXml(optionType, all, login, searchValue,
                    filterDisplayMap);
        } else if (ID_MYUNDERLING.equals(optionType)) {// 我的下属
            return ApplicationContextHolder.getBean("unitTreeMyUnderlingTypeDelegateService",
                    UnitTreeMyUnderlingTypeDelegateServiceImpl.class).searchXml(optionType, all, login, searchValue,
                    filterDisplayMap);
        } else if (ID_ONLINE_USER.equals(optionType)) {// 在线人员
            return ApplicationContextHolder.getBean("unitTreeOnlineUserTypeDelegateService",
                    UnitTreeOnlineUserTypeDelegateServiceImpl.class).searchXml(optionType, all, login, searchValue,
                    filterDisplayMap);
        } else if (ID_MYPARENTDEPT.equals(optionType)) {// 上级部门
            return ApplicationContextHolder.getBean("unitTreeMyParentDeptTypeDelegateService",
                    UnitTreeMyParentDeptTypeDelegateServiceImpl.class).searchXml(optionType, all, login, searchValue,
                    filterDisplayMap);
        } else if (ID_MIXTRUE.equals(optionType)) {// 混合模式
            return ApplicationContextHolder.getBean("unitTreeMixtrueTypeDelegateService",
                    UnitTreeMixtrueTypeDelegateServiceImpl.class).searchXml(optionType, all, login, searchValue,
                    filterCondition, filterDisplayMap);
        } else if (hasDataProvider(optionType)) {
            // 扩展接口实现类
            return unitTreeDataProviderMap.get(optionType).searchXml(optionType, all, login, searchValue,
                    filterCondition);
        }
        // 搜索业务单位通讯录
        return ApplicationContextHolder.getBean("unitTreeBusinessTypeDelegateService",
                UnitTreeBusinessTypeDelegateServiceImpl.class).searchXml(optionType, all, login, searchValue,
                filterDisplayMap);
    }

    /**
     * 获取组织单元结点XML
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.unit.service.UnitTreeService#parserUnit(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public Document parserUnit(String optionType, String all, String login, String filterCondition,
                               HashMap<String, String> displayMap) {
        if (ID_MYUNIT.equals(optionType)) {// 我的单位
            return ApplicationContextHolder.getBean("unitTreeMyUnitTypeDelegateService",
                    UnitTreeTypeDelegateService.class).parserUnit(optionType, all, login, filterCondition, displayMap);
        } else if (ID_DEPT.equals(optionType)) {// 部门
            return ApplicationContextHolder.getBean("unitTreeDeptTypeDelegateService",
                    UnitTreeTypeDelegateService.class).parserUnit(optionType, all, login, filterCondition, displayMap);
        } else if (ID_MYDEPT.equals(optionType)) {// 我的部门
            return ApplicationContextHolder.getBean("unitTreeMyDeptTypeDelegateService",
                    UnitTreeMyDeptTypeDelegateServiceImpl.class).parserUnit(optionType, all, login, filterCondition,
                    displayMap);
        } else if (ID_MYLEADER.equals(optionType)) {// 我的领导
            return ApplicationContextHolder.getBean("unitTreeMyLeaderTypeDelegateService",
                    UnitTreeMyLeaderTypeDelegateServiceImpl.class).parserUnit(optionType, all, login, filterCondition,
                    displayMap);
        } else if (ID_MYUNDERLING.equals(optionType)) {// 我的下属
            return ApplicationContextHolder.getBean("unitTreeMyUnderlingTypeDelegateService",
                    UnitTreeMyUnderlingTypeDelegateServiceImpl.class).parserUnit(optionType, all, login,
                    filterCondition, displayMap);
        } else if (ID_PUBLICGROUP.equals(optionType)) {// 公共群组
            // return
            // ApplicationContextHolder.getBean("unitTreePublicGroupTypeDelegateService",
            // UnitTreePublicGroupTypeDelegateServiceImpl.class).parserUnit(optionType,
            // all, login,
            // filterCondition, displayMap);
        } else if (ID_PRIVATEGROUP.equals(optionType)) {// 个人群组
            // return
            // ApplicationContextHolder.getBean("unitTreePrivateGroupTypeDelegateService",
            // UnitTreePrivateGroupTypeDelegateServiceImpl.class).parserUnit(optionType,
            // all, login,
            // filterCondition, displayMap);
        } else if (ID_MYPARENTDEPT.equals(optionType)) {// 上级部门
            return ApplicationContextHolder.getBean("unitTreeMyParentDeptTypeDelegateService",
                    UnitTreeMyParentDeptTypeDelegateServiceImpl.class).parserUnit(optionType, all, login,
                    filterCondition, displayMap);
        } else if (ID_ONLINE_USER.equals(optionType)) {// 在线人员
            return ApplicationContextHolder.getBean("unitTreeOnlineUserTypeDelegateService",
                    UnitTreeOnlineUserTypeDelegateServiceImpl.class).parserUnit(optionType, all, login,
                    filterCondition, displayMap);
        } else if (ID_ORG_UNIT.equals(optionType)) {// 组织单元
            return ApplicationContextHolder.getBean("unitTreeOrgUnitTypeDelegateService",
                    UnitTreeOrgUnitTypeDelegateServiceImpl.class).parserUnit(optionType, all, login, filterCondition,
                    displayMap);
        } else if (ID_COMMON_UNIT.equals(optionType)) {// 单位通讯录
            return ApplicationContextHolder.getBean("commonUnitTreeService", CommonUnitTreeServiceImpl.class)
                    .parseCommonUnitTree("", optionType, "");
        } else if (ID_COMMON_UNIT_USER.equals(optionType)) {// 集团通讯录
            // UserDetails userDetail = SpringSecurityUtils.getCurrentUser();
            return ApplicationContextHolder.getBean("commonUnitTreeService", CommonUnitTreeServiceImpl.class)
                    .parseCommonUnitTree("", optionType, "");
        } else if (ID_JOB.equals(optionType)) {// 职位
            return ApplicationContextHolder.getBean("unitTreeJobTypeDelegateService",
                    UnitTreeJobTypeDelegateServiceImpl.class).parserUnit(optionType, all, login, filterCondition,
                    displayMap);
        } else if (ID_DUTY.equals(optionType)) {// 职务
            return ApplicationContextHolder.getBean("unitTreeDutyTypeDelegateService",
                    UnitTreeDutyTypeDelegateServiceImpl.class).parserUnit(optionType, all, login, filterCondition,
                    displayMap);
        } else if (ID_JOBDUTY.equals(optionType)) {// 职位职务
            return ApplicationContextHolder.getBean("unitTreeJobDutyTypeDelegateService",
                    UnitTreeJobDutyTypeDelegateServiceImpl.class).parserUnit(optionType, all, login, filterCondition,
                    displayMap);
        } else if (ID_MYUNIT1.equals(optionType)) {// 我的单位
            return ApplicationContextHolder.getBean("unitTreeMyUnit1TypeDelegateService",
                    UnitTreeMyUnit1TypeDelegateServiceImpl.class).parserUnit(optionType, all, login, filterCondition,
                    displayMap);
        } else if (ID_MYDIRECTLEADER.equals(optionType)) {// 我的直接领导
            return ApplicationContextHolder.getBean("unitTreeMyDirectLeaderTypeDelegateService",
                    UnitTreeMyDirectLeaderTypeDelegateServiceImpl.class).parserUnit(optionType, all, login,
                    filterCondition, displayMap);
        } else if (ID_MIXTRUE.equals(optionType)) {
            return ApplicationContextHolder.getBean("unitTreeMixtrueTypeDelegateService",
                    UnitTreeMixtrueTypeDelegateServiceImpl.class).parserUnit(optionType, all, login, filterCondition,
                    displayMap);
        } else if (hasDataProvider(optionType)) {
            // 扩展接口实现类
            return unitTreeDataProviderMap.get(optionType).parserUnit(optionType, all, login, filterCondition,
                    displayMap);
        }
        return ApplicationContextHolder.getBean("unitTreeBusinessTypeDelegateService",
                UnitTreeBusinessTypeDelegateServiceImpl.class).parserUnit(optionType, all, login, filterCondition,
                displayMap);

    }

    /**
     * 展开组织单元结点XML
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.unit.service.UnitTreeService#toggleUnit(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Document toggleUnit(String type, String id, String all, String login,
                               HashMap<String, String> filterDisplayMap) {
        if (ID_MYUNIT.equals(type)) {// 我的单位
            return ApplicationContextHolder.getBean("unitTreeMyUnitTypeDelegateService",
                    UnitTreeMyUnitTypeDelegateServiceImpl.class).toggleUnit(type, id, all, login, filterDisplayMap);
        } else if (ID_DEPT.equals(type)) {// 部门
            return ApplicationContextHolder.getBean("unitTreeDeptTypeDelegateService",
                    UnitTreeDeptTypeDelegateServiceImpl.class).toggleUnit(type, id, all, login, filterDisplayMap);
        } else if (ID_MYDEPT.equals(type)) {// 我的部门
            return ApplicationContextHolder.getBean("unitTreeMyDeptTypeDelegateService",
                    UnitTreeMyDeptTypeDelegateServiceImpl.class).toggleUnit(type, id, all, login, filterDisplayMap);
        } else if (ID_MYPARENTDEPT.equals(type)) {// 上级部门
            return ApplicationContextHolder.getBean("unitTreeMyParentDeptTypeDelegateService",
                    UnitTreeMyParentDeptTypeDelegateServiceImpl.class).toggleUnit(type, id, all, login,
                    filterDisplayMap);
        } else if (ID_ORG_UNIT.equals(type)) {// 组织单元
            return ApplicationContextHolder.getBean("unitTreeOrgUnitTypeDelegateService",
                    UnitTreeOrgUnitTypeDelegateServiceImpl.class).toggleUnit(type, id, all, login, filterDisplayMap);
        } else if (ID_JOB.equals(type)) {// 岗位
            return ApplicationContextHolder.getBean("unitTreeJobTypeDelegateService",
                    UnitTreeJobTypeDelegateServiceImpl.class).toggleUnit(type, id, all, login, filterDisplayMap);
        } else if (ID_DUTY.equals(type)) {// 职务
            return ApplicationContextHolder.getBean("unitTreeDutyTypeDelegateService",
                    UnitTreeDutyTypeDelegateServiceImpl.class).toggleUnit(type, id, all, login, filterDisplayMap);
        } else if (ID_JOBDUTY.equals(type)) {// 职位职务
            return ApplicationContextHolder.getBean("unitTreeJobDutyTypeDelegateService",
                    UnitTreeJobDutyTypeDelegateServiceImpl.class).toggleUnit(type, id, all, login, filterDisplayMap);
        } else if (ID_MYUNIT1.equals(type)) {// 我的单位1
            return ApplicationContextHolder.getBean("unitTreeMyUnit1TypeDelegateService",
                    UnitTreeMyUnit1TypeDelegateServiceImpl.class).toggleUnit(type, id, all, login, filterDisplayMap);
        } else if (ID_PUBLICGROUP.equals(type)) {// 公共群组
            // return
            // ApplicationContextHolder.getBean("unitTreePublicGroupTypeDelegateService",
            // UnitTreePublicGroupTypeDelegateServiceImpl.class)
            // .toggleUnit(type, id, all, login, filterDisplayMap);
        } else if (ID_PRIVATEGROUP.equals(type)) {// 个人群组
            // return
            // ApplicationContextHolder.getBean("unitTreePrivateGroupTypeDelegateService",
            // UnitTreePrivateGroupTypeDelegateServiceImpl.class).toggleUnit(type,
            // id, all, login,
            // filterDisplayMap);
        } else if (hasDataProvider(type)) {
            // 扩展接口实现类
            return unitTreeDataProviderMap.get(type).toggleUnit(type, id, all, login, filterDisplayMap);
        }
        return null;
    }

    /**
     * 获取组织单元根结点XML
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.unit.service.UnitTreeService#leafUnit(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Document leafUnit(String type, String id, String leafType, String login,
                             HashMap<String, String> filterDisplayMap) {
        if (ID_MYUNIT.equals(type)) {// 我的单位
            return ApplicationContextHolder.getBean("unitTreeMyUnitTypeDelegateService",
                    UnitTreeMyUnitTypeDelegateServiceImpl.class).leafUnit(type, id, leafType, login, filterDisplayMap);
        } else if (ID_DEPT.equals(type)) {// 部门
            return ApplicationContextHolder.getBean("unitTreeDeptTypeDelegateService",
                    UnitTreeDeptTypeDelegateServiceImpl.class).leafUnit(type, id, leafType, login, filterDisplayMap);
        } else if (ID_MYDEPT.equals(type)) {// 我的部门
            return ApplicationContextHolder.getBean("unitTreeMyDeptTypeDelegateService",
                    UnitTreeMyDeptTypeDelegateServiceImpl.class).leafUnit(type, id, leafType, login, filterDisplayMap);
        } else if (ID_MYPARENTDEPT.equals(type)) {// 上级部门
            return ApplicationContextHolder.getBean("unitTreeMyParentDeptTypeDelegateService",
                    UnitTreeMyParentDeptTypeDelegateServiceImpl.class).leafUnit(type, id, leafType, login,
                    filterDisplayMap);
        } else if (ID_ORG_UNIT.equals(type)) {// 组织单元
            return ApplicationContextHolder.getBean("unitTreeOrgUnitTypeDelegateService",
                    UnitTreeOrgUnitTypeDelegateServiceImpl.class).leafUnit(type, id, all, login, filterDisplayMap);
        } else if (ID_JOB.equals(type)) {// 岗位
            return ApplicationContextHolder.getBean("unitTreeJobTypeDelegateService",
                    UnitTreeJobTypeDelegateServiceImpl.class).leafUnit(type, id, all, login, filterDisplayMap);
        } else if (ID_MYUNIT1.equals(type)) {// 我的单位1
            return ApplicationContextHolder.getBean("unitTreeMyUnit1TypeDelegateService",
                    UnitTreeMyUnit1TypeDelegateServiceImpl.class).leafUnit(type, id, leafType, login, filterDisplayMap);
        } else if (ID_PUBLICGROUP.equals(type)) {// 公共群组
            // return
            // ApplicationContextHolder.getBean("unitTreePublicGroupTypeDelegateService",
            // UnitTreePublicGroupTypeDelegateServiceImpl.class).leafUnit(type,
            // id,
            // all, login, filterDisplayMap);
        } else if (ID_PRIVATEGROUP.equals(type)) {// 个人群组
            // return
            // ApplicationContextHolder.getBean("unitTreePrivateGroupTypeDelegateService",
            // UnitTreePrivateGroupTypeDelegateServiceImpl.class).leafUnit(type,
            // id,
            // all, login, filterDisplayMap);
        } else if (ID_JOBDUTY.equals(type)) {// 职位职务
            return ApplicationContextHolder.getBean("unitTreeJobDutyTypeDelegateService",
                    UnitTreeJobDutyTypeDelegateServiceImpl.class).leafUnit(type, id, all, login, filterDisplayMap);
        } else if (ID_MIXTRUE.equals(type)) {
            return ApplicationContextHolder.getBean("unitTreeMixtrueTypeDelegateService",
                    UnitTreeMixtrueTypeDelegateServiceImpl.class).leafUnit(type, id, all, login, filterDisplayMap);
        } else if (hasDataProvider(type)) {
            // 扩展接口实现类
            return unitTreeDataProviderMap.get(type).leafUnit(type, id, leafType, login, filterDisplayMap);
        }
        return ApplicationContextHolder.getBean("unitTreeBusinessTypeDelegateService",
                UnitTreeBusinessTypeDelegateServiceImpl.class).leafUnit(type, id, leafType, login, filterDisplayMap);
    }

    @Override
    public List<TreeNode> parseUnitTree(String treeId, HashMap<String, String> filterDisplayMap) {
        return ApplicationContextHolder.getBean("unitTreeMyUnitTypeDelegateService",
                UnitTreeMyUnitTypeDelegateServiceImpl.class).parseUnitTree(treeId);
    }

    /**
     * 如何描述该方法
     *
     * @param optionType
     * @return
     */
    private boolean hasDataProvider(String optionType) {
        if (unitTreeDataProviders == null) {
            return false;
        }
        for (UnitTreeDataProviderDelegateService dataProvider : unitTreeDataProviders) {
            if (optionType.equals(dataProvider.getType())) {
                if (!unitTreeDataProviderMap.containsKey(optionType)) {
                    unitTreeDataProviderMap.put(optionType, dataProvider);
                }
                return true;
            }
        }
        return false;
    }
}
