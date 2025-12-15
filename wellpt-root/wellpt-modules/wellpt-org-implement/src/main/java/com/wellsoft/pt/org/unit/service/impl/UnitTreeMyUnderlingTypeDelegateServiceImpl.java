/*
 * @(#)2013-2-18 V1.0
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
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
 * 2013-1-18.1	zhulh		2013-1-18		Create
 * </pre>
 * @date 2013-1-18
 */
@Service
@Transactional
public class UnitTreeMyUnderlingTypeDelegateServiceImpl extends UnitTreeMyUnit1TypeDelegateServiceImpl {

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

    /*
     * all=2(全部树型节点) all=1(当只有一个根节点时多返回一级子节点，否则返回多个根节点即可) all=0(返回包括给定节点和其直接子节点)
     * (non-Javadoc)
     *
     * @see
     * com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService#parserUnit
     * (java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Document parserUnit(String type, String all, String login, String filterCondtion, HashMap<String, String> filterDisplayMap) {
        List<User> users = new ArrayList<User>();
        List<String> ids = userService.getSubordinate_new(((UserDetails) SpringSecurityUtils.getCurrentUser())
                .getUserUuid());
        for (String id : ids) {
            users.add(userService.getById(id));
        }
        return parseUsers(users, login, filterDisplayMap);
    }

    private Document parseUsers(List<User> users, String login, HashMap<String, String> filterDisplayMap) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(NODE_UNITS);
        for (User user : users) {
            Element element = root.addElement(NODE_UNIT);
            setDepartmentUserJobElement(element, user, user.getUserName(), "", filterDisplayMap);
        }
        return document;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService#toggleUnit
     * (java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Document toggleUnit(String type, String id, String all, String login, HashMap<String, String> filterDisplayMap) {
        return null;
    }

    /*
     * all=2(全部树型节点) all=1(当只有一个根节点时多返回一级子节点，否则返回多个根节点即可) all=0(返回包括给定节点和其直接子节点)
     *
     * (non-Javadoc)
     *
     * @see
     * com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService#leafUnit
     * (java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Document leafUnit(String type, String id, String leafType, String login, HashMap<String, String> filterDisplayMap) {
        return null;
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
    public Document searchXml(String type, String all, String login, String searchValue, HashMap<String, String> filterDisplayMap) {

        long time1 = System.currentTimeMillis();
        String rssearchValue = searchValue.toLowerCase();
        //查找出所有符合条件的用户，可以用拼音或汉字查询
        Query query = this.userService.createSQLQuery(createSearchSql(rssearchValue));
        List idList = query.list();
        //将object转化为User对象
        List<User> dataList = userService.getByIds(idList);
        //创建一个xml文件
        Document document = DocumentHelper.createDocument();
        //往xml文件添加节点
        Element root = document.addElement(NODE_UNITS);
        List<User> users = new ArrayList<User>();
        //获取当前登陆用户的所有领导
        List<String> ids = userService.getSubordinate_new(((UserDetails) SpringSecurityUtils.getCurrentUser())
                .getUserUuid());
        for (String id : ids) {
            for (User user : dataList) {
                //判断查找出来的用户书否是该登录人员的领导
                if (user.getId().equals(id)) {
                    users.add(userService.getById(id));
                }
            }

        }
        //遍历包含在我的所有领导中的用户，将信息存到xml文件中，以便前台js解析,显示
        for (User user : users) {
            User u = userService.get(user.getUuid());
            if (u.getEnabled() == false) {
                continue;
            }
            //按职位搜索
            Set<UserJob> userjobs = u.getUserJobs();
            //System.out.println(user.getUserName());
            for (UserJob userJob : userjobs) {
                Department dept = departmentService.get(userJob.getJob().getDepartmentUuid());
                if (dept.getIsVisible() == true) {
                    Element child = root.addElement(NODE_UNIT);
                    String userPath = dept.getPath() + "/" + userJob.getJob().getName() + "/" + user.getUserName();

                    // setDepartmentUserJobElement(child, user, userPath, departmentUserJob.getJobName());
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

}
