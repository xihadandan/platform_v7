/*
 * @(#)2013-7-6 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.unit.service.impl;

import com.wellsoft.pt.org.dao.DepartmentDao;
import com.wellsoft.pt.org.dao.DepartmentUserJobDao;
import com.wellsoft.pt.org.dao.JobDao;
import com.wellsoft.pt.org.dao.UserDao;
import com.wellsoft.pt.org.entity.Department;
import com.wellsoft.pt.org.entity.User;
import com.wellsoft.pt.org.entity.UserJob;
import com.wellsoft.pt.org.service.DepartmentService;
import com.wellsoft.pt.org.service.UserService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.wellsoft.pt.org.unit.service.impl.UnitTreeServiceImpl.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-7-6.1	zhulh		2013-7-6		Create
 * </pre>
 * @date 2013-7-6
 */
@Service
@Transactional
public class UnitTreeOnlineUserTypeDelegateServiceImpl extends UnitTreeMyUnit1TypeDelegateServiceImpl {

    @Autowired
    private SessionRegistry sessionRegistry;

    @Autowired
    private UserDao userDao;
    @Autowired
    private UserService userService;
    @Autowired
    private DepartmentDao departmentDao;
    @Autowired
    private DepartmentUserJobDao departmentUserJobDao;
    @Autowired
    private JobDao jobDao;
    @Autowired
    private DepartmentService departmentService;

    private Logger logger = LoggerFactory.getLogger(UnitTreeMyUnit1TypeDelegateServiceImpl.class);

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService#parserUnit(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Document parserUnit(String type, String all, String login, String filterCondtion,
                               HashMap<String, String> filterDisplayMap) {
        List<Object> principals = sessionRegistry.getAllPrincipals();
        Set<UserDetails> userDetails = new HashSet<UserDetails>();
        String currentTenantId = SpringSecurityUtils.getCurrentTenantId();
        for (Object principal : principals) {
            if (principal instanceof UserDetails) {
                UserDetails user = (UserDetails) principal;
                if (currentTenantId.equals(user.getTenantId())) {
                    userDetails.add(user);
                }
            }
        }

        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(NODE_UNITS);

        List<UserDetails> list = new ArrayList<UserDetails>(userDetails);
        Collections.sort(list, new UserDetailsComparator());

        for (UserDetails userDetail : list) {
            User user = userService.get(userDetail.getUserUuid());
            if (user == null) {
                continue;
            }
            String path = user.getUserName();
            List<String> departmentPaths = new ArrayList<String>();// userDetail.getDepartmentPaths();
            if (!departmentPaths.isEmpty()) {
                path = departmentPaths.get(0) + "/" + path;
            }
            Element child = root.addElement(NODE_UNIT);
            setDepartmentUserJobElement(child, ATTRIBUTE_TYPE_USER, "1", user.getId(), user.getUserName(), path,
                    user.getSex(), userDetail.getMainJobName(), getUserEmail(user), user.getEmployeeNumber(),
                    user.getLoginName(), "", filterDisplayMap);
        }

        return document;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService#toggleUnit(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Document toggleUnit(String type, String id, String all, String login,
                               HashMap<String, String> filterDisplayMap) {
        Document document = DocumentHelper.createDocument();
        document.addElement(NODE_UNITS);
        return document;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService#leafUnit(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Document leafUnit(String type, String id, String leafType, String login,
                             HashMap<String, String> filterDisplayMap) {
        Document document = DocumentHelper.createDocument();
        document.addElement(NODE_UNITS);
        return document;
    }

    /**
     * 如何描述该方法
     * begin2105-01-16 @ yuyq
     *
     * @param type
     * @param all
     * @param login
     * @param searchValue
     */
    @Override
    public Document searchXml(String type, String all, String login, String searchValue,
                              HashMap<String, String> filterDisplayMap) {

        long time1 = System.currentTimeMillis();
        String rssearchValue = searchValue.toLowerCase();
        // 查找出所有符合条件的用户，可以用拼音或汉字查询
        Query query = this.userService.createSQLQuery(createSearchSql(rssearchValue));
        List idList = query.list();
        // 将object转化为User对象
        List<User> dataList = userService.getByIds(idList);
        // 创建一个xml文件
        Document document = DocumentHelper.createDocument();
        // 往xml文件添加节点
        Element root = document.addElement(NODE_UNITS);
        List<User> users = new ArrayList<User>();
        // 获取当前所有在线的用户
        List<Object> principals = sessionRegistry.getAllPrincipals();
        // 获取当前登录人id
        String currentTenantId = SpringSecurityUtils.getCurrentTenantId();
        for (Object principal : principals) {
            if (principal instanceof UserDetails) {
                // UserDetails是继承user实现Tenantable的实体
                UserDetails user = (UserDetails) principal;
                if (currentTenantId.equals(user.getTenantId())) {
                    for (User user1 : dataList) {
                        // 判断查找出来的用户书否是该登录人员的领导
                        if (user1.getId().equals(user.getUserId())) {
                            users.add(userService.getById(user.getUserId()));
                        }
                    }

                }
            }
        }

        // 遍历包含在我的所有领导中的用户，将信息存到xml文件中，以便前台js解析,显示
        for (User user : users) {
            User u = userService.get(user.getUuid());
            if (u.getEnabled() == false) {
                continue;
            }
            // 按职位搜索
            Set<UserJob> userjobs = u.getUserJobs();
            // System.out.println(user.getUserName());
            for (UserJob userJob : userjobs) {
                Department dept = departmentService.get(userJob.getJob().getDepartmentUuid());
                if (dept.getIsVisible() == true) {
                    Element child = root.addElement(NODE_UNIT);
                    String userPath = dept.getPath() + "/" + userJob.getJob().getName() + "/" + user.getUserName();

                    // setDepartmentUserJobElement(child, user, userPath,
                    // departmentUserJob.getJobName());
                    setDepartmentUserJobElement(child, ATTRIBUTE_TYPE_USER, "1", user.getId(), userPath, userPath,
                            user.getSex(), user.getJobName(), getUserEmail(user), user.getEmployeeNumber(),
                            user.getLoginName(), "", filterDisplayMap);
                }
            }
        }
        long time2 = System.currentTimeMillis();
        logger.info("load searchXml searchValue:[" + searchValue + "]and Data spent " + (time2 - time1) / 1000.0 + "s");
        return document;
    }

    private static class UserDetailsComparator implements Comparator<UserDetails> {

        /**
         *
         */
        public UserDetailsComparator() {
        }

        /**
         * (non-Javadoc)
         *
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(UserDetails userDetails1, UserDetails userDetails2) {
            if (StringUtils.isBlank(userDetails1.getCode()) || StringUtils.isBlank(userDetails2.getCode())) {
                return 0;
            }
            return userDetails1.getCode().compareTo(userDetails2.getCode());
        }
    }
}
