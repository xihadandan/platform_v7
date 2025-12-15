package com.wellsoft.pt.basicdata.rtx.service.impl;

import com.wellsoft.pt.basicdata.rtx.bean.RtxBean;
import com.wellsoft.pt.basicdata.rtx.dao.RtxDao;
import com.wellsoft.pt.basicdata.rtx.entity.Rtx;
import com.wellsoft.pt.basicdata.rtx.service.RtxService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.message.support.Message;
import com.wellsoft.pt.org.entity.Department;
import com.wellsoft.pt.org.entity.User;
import com.wellsoft.pt.org.service.DepartmentService;
import com.wellsoft.pt.org.service.UserService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rtx.RTXSvrApi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Description: RTX设置服务层实现类
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-6-17.1	zhouyq		2013-6-17		Create
 * </pre>
 * @date 2013-6-17
 */
@Service
public class RtxServiceImpl extends AbstractJpaServiceImpl<Rtx, RtxDao, String> implements RtxService {
    Map<String, String> rtxAllDepsFullPathMap = new HashMap<String, String>();// rtx所有部门全路径
    Map<String, String> oaAllDepsFullPathMap = new HashMap<String, String>();// oa所有部门全路径
    Map<String, String> rtxAllUsersFullPathMap = new HashMap<String, String>();// rtx所有用户全路径
    Map<String, String> oaAllUsersFullPathMap = new HashMap<String, String>();// oa所有用户全路径
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private UserService userService;

    /**
     * 判断是否启用rtx
     *
     * @return
     */
    @Override
    public Boolean isRtxEnable() {
        Boolean result = false;
        List<Rtx> rtxs = listAll();
        if (CollectionUtils.isNotEmpty(rtxs)) {
            result = rtxs.get(0).getIsEnable();
        }
        return result;
    }

    /**
     * 通过uuid获取Rtx设置
     *
     * @param uuid
     * @return
     */
    @Override
    public Rtx get(String uuid) {
        return dao.getOne(uuid);
    }

    /**
     * 通过uuid获取Rtx设置VO对象
     *
     * @param id
     * @return
     */

    @Override
    public RtxBean getBeanByUuid(String uuid) {
        Rtx rtx = null;
        if (StringUtils.isBlank(uuid)) {
            rtx = getAll().get(0);
            System.out.println("rtxuuid为空，得到第一个");
        } else {
            rtx = this.dao.getOne(uuid);
            System.out.println("rtxuuid不为空，直接去");
        }
        RtxBean bean = new RtxBean();
        BeanUtils.copyProperties(rtx, bean);
        return bean;
    }

    /**
     * 保存Rtx设置bean
     */

    @Override
    @Transactional
    public void saveBean(RtxBean bean) {
        Rtx rtx = new Rtx();
        // 保存新rtx 设置id值
        if (StringUtils.isBlank(bean.getUuid())) {
            bean.setUuid(null);
            // ID唯一性判断
            rtx.setUuid(bean.getUuid());
        } else {
            rtx = this.dao.getOne(bean.getUuid());
        }
        BeanUtils.copyProperties(bean, rtx);

        this.dao.save(rtx);
    }

    /**
     * 删除Rtx设置
     */
    @Override
    @Transactional
    public void remove(String uuid) {
        this.dao.delete(uuid);
    }

    /**
     * 获取全部的Rtx设置
     *
     * @return
     */
    @Override
    public List<Rtx> getAll() {
        return listAll();
    }

    /**
     * 同步组织
     */
    @Override
    @Transactional
    public void synchronizedOrganization(RtxBean bean) {
        Rtx rtx = getAll().get(0);
        System.out.println("同步操作为：" + rtx.getSynchronizationOperation());
        // 清除rtx中所有后再添加
        if ("CLEAR_ALL".equals(rtx.getSynchronizationOperation())) {
            delAllUser();
            delAllDeps();
            addAllDeps();
            addAllUser();
        } else if // 只增加新的部门和用户
        ("ONLY_ADD_NEW".equals(rtx.getSynchronizationOperation())) {
            // 仅增加新部门、用户,不删除OA中不存在的
            addNewDeps();
            addNewUser();
        } else if // 清除oa中不存在的部门和用户
        ("REMOVE_NO_EXIST".equals(rtx.getSynchronizationOperation())) {
            // 增加新部门、用户,删除OA中不存在的
            addNewDeps();
            addNewUser();
            // 1、部门改变
            Map<String, String> rtxAllDepsFullPathMap = getRtxDepsFullPath();
            Map<String, String> oaAllDepsFullPathMap = getOaDepsFullPath();
            Iterator rtxDepsMap = rtxAllDepsFullPathMap.keySet().iterator();
            while (rtxDepsMap.hasNext()) {
                String rtxFullPathKey = (String) rtxDepsMap.next();
                if ((oaAllDepsFullPathMap.containsKey(rtxFullPathKey))) {
                    System.out.println("部门" + rtxFullPathKey + "在oa中存在，不用删除");
                } else {
                    delDepts(rtxFullPathKey, "1");
                    System.out.println("部门" + rtxFullPathKey + "在oa中不存在，删除");
                }
            }
            // 2、离职人员
            Map<String, String> rtxAllUsersFullPathMap = getRtxUsersFullPath();
            Map<String, String> oaAllUsersFullPathMap = getOaUsersFullPath();
            Iterator rtxUsersMap = rtxAllUsersFullPathMap.keySet().iterator();
            while (rtxUsersMap.hasNext()) {
                String rtxFullPathKey = (String) rtxUsersMap.next();
                if ((oaAllUsersFullPathMap.containsKey(rtxFullPathKey))) {
                    System.out.println("用户" + rtxFullPathKey + "在oa中存在，不用删除");
                } else {
                    delUser(rtxFullPathKey);
                    System.out.println("用户" + rtxFullPathKey + "在oa中不存在，删除");
                }
            }
        }
    }

    /**
     * 获取OA所有用户的全路径
     */
    public Map<String, String> getOaUsersFullPath() {
        // Oa根部门集合
        List<Department> topDepartments = departmentService.getTopLevel();
        for (Department department : topDepartments) {
            String departmentId = department.getId().substring(1);
            oaAllDepsFullPathMap.put(department.getName(), department.getName());// 将部门id、部门名存进map集合中
            List<String> deptUserList = departmentService.getUserIdsByDepartmentId(department.getId());
            // 判断当前用户部门下是否有用户
            if (deptUserList.size() != 0) {
                for (String deptUser : deptUserList) {
                    User user = userService.getById(deptUser);
                    // 获取全路径需判断是否启用简称
                    if (getAll().get(0).getIsEnableAbbreviation()) {
                        String topUserFullPath = department.getName() + "/" + user.getLoginName();
                        oaAllUsersFullPathMap.put(user.getLoginName(), topUserFullPath);// 当前部门下的用户全路径
                    } else {
                        String topUserFullPath = department.getName() + "/" + user.getUserName();
                        oaAllUsersFullPathMap.put(user.getUserName(), topUserFullPath);// 当前部门下的用户全路径
                    }
                }
            }
            // 遍历oa子部门全路径
            if (department.getChildren().size() != 0) {
                getOaChildUsersFullPath(department);
            } else {
                System.out.println("该全路径部门下无子部门");
            }
        }
        return oaAllUsersFullPathMap;
    }

    /**
     * 获取oa子部门用户的全路径
     */
    public void getOaChildUsersFullPath(Department department) {
        List<Department> childDepsList = department.getChildren();
        if (childDepsList.size() > 0) {
            for (Department childDep : childDepsList) {
                String parentDepartmentId = department.getId().substring(1);
                String childDepartmentId = childDep.getId().substring(1);
                String childDepFullPath = oaAllDepsFullPathMap.get(department.getName()) + "/" + childDep.getName();
                oaAllDepsFullPathMap.put(childDep.getName(), childDepFullPath);

                List<String> childDeptUserList = departmentService.getUserIdsByDepartmentId(childDep.getId());
                if (childDeptUserList.size() != 0) {
                    for (String deptUser : childDeptUserList) {
                        User user = userService.getById(deptUser);
                        // 获取全路径需判断是否启用简称
                        if (getAll().get(0).getIsEnableAbbreviation()) {
                            String childUserFullPath = oaAllDepsFullPathMap.get(department.getName()) + "/"
                                    + childDep.getName() + "/" + user.getLoginName();
                            oaAllUsersFullPathMap.put(user.getLoginName(), childUserFullPath);
                        } else {
                            String childUserFullPath = oaAllDepsFullPathMap.get(department.getName()) + "/"
                                    + childDep.getName() + "/" + user.getUserName();
                            oaAllUsersFullPathMap.put(user.getUserName(), childUserFullPath);
                        }

                    }
                }

                if (childDep.getChildren().size() != 0) {
                    getOaChildUsersFullPath(childDep);
                }
            }
        } else {
            System.out.println("部门" + department.getId() + "没有子部门");
        }

    }

    /**
     * 获取RTX所有用户的全路径
     */
    public Map<String, String> getRtxUsersFullPath() {
        // Rtx根部门集合
        List<String> rtxTopDepsList = getChildDepts("0");
        for (String rtxTopDep : rtxTopDepsList) {
            List<String> deptUserList = getDeptUsers(rtxTopDep);
            // 判断当前用户部门下是否有用户
            if (deptUserList.size() != 0) {
                for (String deptUser : deptUserList) {
                    String topUserFullPath = getDeptName(rtxTopDep) + "/" + deptUser;
                    rtxAllUsersFullPathMap.put(deptUser, topUserFullPath);// 当前部门下的用户全路径
                }
            }
            rtxAllDepsFullPathMap.put(getDeptName(rtxTopDep), getDeptName(rtxTopDep));// 将部门id、部门名存进map集合中
            //
            // 遍历rtx子部门全路径
            List<String> rtxChildDepsList = getChildDepts(rtxTopDep);
            if (rtxChildDepsList.size() != 0) {
                getRtxChildUsersFullPath(rtxTopDep);
            }
        }
        return rtxAllUsersFullPathMap;

    }

    /**
     * 获取rtx子部门用户的全路径
     */
    public void getRtxChildUsersFullPath(String rtxParentDep) {
        List<String> rtxChildDepsList = getChildDepts(rtxParentDep);
        for (String rtxChildDep : rtxChildDepsList) {
            String childDepFullPath = rtxAllDepsFullPathMap.get(getDeptName(rtxParentDep)) + "/"
                    + getDeptName(rtxChildDep);
            rtxAllDepsFullPathMap.put(getDeptName(rtxChildDep), childDepFullPath);

            List<String> childDeptUserList = getDeptUsers(rtxChildDep);
            if (childDeptUserList.size() != 0) {
                for (String deptUser : childDeptUserList) {
                    String childUserFullPath = rtxAllDepsFullPathMap.get(getDeptName(rtxParentDep)) + "/"
                            + getDeptName(rtxChildDep) + "/" + deptUser;
                    rtxAllUsersFullPathMap.put(deptUser, childUserFullPath);
                }
            }
            // 遍历rtx子部门
            List<String> rtxDepsList = getChildDepts(rtxChildDep);
            if (rtxDepsList.size() != 0) {
                getRtxChildUsersFullPath(rtxChildDep);
            } else {
                System.out.println("该全路径部门下无子部门");
            }
        }

    }

    /**
     * 获取OA所有部门id的全路径
     */
    public Map<String, String> getOaDepsFullPath() {
        // Oa根部门集合
        List<Department> topDepartments = departmentService.getTopLevel();
        for (Department department : topDepartments) {
            String departmentId = department.getId().substring(1);
            oaAllDepsFullPathMap.put(delPrefixZero(departmentId), delPrefixZero(departmentId));// 将部门id、部门名存进map集合中
            // 遍历oa子部门全路径
            if (department.getChildren().size() != 0) {
                getOaChildDepsFullPath(department);
            } else {
                System.out.println("该全路径部门下无子部门");
            }
        }
        return oaAllDepsFullPathMap;
    }

    /**
     * 获取oa子部门id的全路径
     */
    public void getOaChildDepsFullPath(Department department) {
        List<Department> childDepsList = department.getChildren();
        if (childDepsList.size() > 0) {
            for (Department childDep : childDepsList) {
                String parentDepartmentId = department.getId().substring(1);
                String childDepartmentId = childDep.getId().substring(1);
                String childDepFullPath = oaAllDepsFullPathMap.get(delPrefixZero(parentDepartmentId)) + "/"
                        + delPrefixZero(childDepartmentId);
                oaAllDepsFullPathMap.put(delPrefixZero(childDepartmentId), childDepFullPath);
                if (childDep.getChildren().size() != 0) {
                    getOaChildDepsFullPath(childDep);
                }
            }
        } else {
            System.out.println("部门" + department.getUuid() + "没有子部门");
        }
    }

    /**
     * 获取RTX所有部门id的全路径
     */
    public Map<String, String> getRtxDepsFullPath() {
        // Rtx根部门集合
        List<String> rtxTopDepsList = getChildDepts("0");
        for (String rtxTopDep : rtxTopDepsList) {
            rtxAllDepsFullPathMap.put(rtxTopDep, rtxTopDep);// 将部门id、部门名存进map集合中
            // rtxAllDepsFullPathMap.put(getDeptName(rtxTopDep),
            // getDeptName(rtxTopDep));//将部门id、部门名存进map集合中
            // 遍历rtx子部门全路径
            List<String> rtxChildDepsList = getChildDepts(rtxTopDep);
            if (rtxChildDepsList.size() != 0) {
                getRtxChildDepsFullPath(rtxTopDep);
            } else {
                System.out.println("该全路径部门下无子部门");
            }
        }
        return rtxAllDepsFullPathMap;
    }

    /**
     * 获取rtx子部门id的全路径
     */
    public void getRtxChildDepsFullPath(String rtxParentDep) {
        List<String> rtxChildDepsList = getChildDepts(rtxParentDep);
        for (String rtxChildDep : rtxChildDepsList) {
            String childDepFullPath = rtxAllDepsFullPathMap.get(rtxParentDep) + "/" + rtxChildDep;
            rtxAllDepsFullPathMap.put(rtxChildDep, childDepFullPath);
            // String childDepFullPath =
            // rtxAllDepsFullPathMap.get(getDeptName(rtxParentDep)) + "/"
            // + getDeptName(rtxChildDep);
            // rtxAllDepsFullPathMap.put(getDeptName(rtxChildDep),
            // childDepFullPath);
            // 遍历rtx子部门
            List<String> rtxDepsList = getChildDepts(rtxChildDep);
            if (rtxDepsList.size() != 0) {
                getRtxChildDepsFullPath(rtxChildDep);
            } else {
                System.out.println("该全路径部门下无子部门");
            }
        }

    }

    /**
     * 添加新用户
     */
    public void addNewUser() {
        Rtx rtx = getAll().get(0);
        List<User> userList = userService.getAll();
        for (User user : userList) {
            Department department = userService.getDepartmentByUserId(user.getId());
            // 判断该用户是否属于某部门
            if (department != null) {
                String departmentId = department.getId().substring(1);
                if (userIsExist(user.getLoginName()) != 0) {
                    addUser(user.getLoginName(), departmentId, user.getUserName(), user.getPassword());
                }
            }
        }
    }

    /**
     * 添加新部门
     */
    public void addNewDeps() {

        List<Department> topDepartments = departmentService.getTopLevel();
        for (Department department : topDepartments) {
            String departmentId = department.getId().substring(1);
            if (DeptIsExist(departmentId) == 0) {// 返回0为不存在
                addDept(departmentId, department.getRemark(), department.getName(), "0");
            }
            if (department.getChildren().size() != 0) {
                getAndAddChildDeps(department);
            }
        }
    }

    /**
     * 同步所有部门
     */
    public void addAllDeps() {
        // 获取所有根部门
        List<Department> topDepartments = departmentService.getTopLevel();
        for (Department department : topDepartments) {
            // 添加根部门
            System.out.println("根部门id:" + department.getId());
            System.out.println("根部门信息:" + department.getRemark());
            System.out.println("根部门名字:" + department.getName());
            String departmentId = department.getId().substring(1);
            addDept(departmentId, department.getRemark(), department.getName(), "0");

            if (department.getChildren().size() != 0) {
                getChildDeps(department);
            }
        }
    }

    /**
     * 从oa向rtx发送在线消息
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.rtx.service.RtxService#sendMessage(com.wellsoft.pt.message.support.Message)
     */
    @Override
    public void sendMessage(Message msg) {
        try {
            sendNotify(msg);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 删除根部门及其以下所有部门及用户
     */
    public void delAllDeps() {
        // 获取所有根部门
        List<Department> topDepartments = departmentService.getTopLevel();
        for (Department department : topDepartments) {
            String deptId = department.getId().substring(1);
            String type = "1";// 删除部门下用户

            int iRet = -1;

            RTXSvrApi RtxsvrapiObj = new RTXSvrApi();
            if (RtxsvrapiObj.Init()) {
                setSvrIpPort(RtxsvrapiObj);// 设置服务器ip和端口号
                iRet = RtxsvrapiObj.deleteDept(deptId, type);
                if (iRet == 0) {
                    System.out.println("删除部门" + department.getName() + "成功");
                } else {
                    System.out.println("删除部门" + department.getName() + "失败");
                }
            }
            RtxsvrapiObj.UnInit();
        }
    }

    /**
     * 删除rtx中所有用户
     */
    public void delAllUser() {
        List<User> userList = userService.getAll();
        for (User user : userList) {
            int iRet = -1;
            RTXSvrApi RtxsvrapiObj = new RTXSvrApi();
            if (RtxsvrapiObj.Init()) {
                setSvrIpPort(RtxsvrapiObj);// 设置服务器ip和端口号
                iRet = RtxsvrapiObj.deleteUser(user.getLoginName());
                if (iRet == 0) {
                    System.out.println("删除用户" + user.getLoginName() + "成功");
                } else {
                    System.out.println("删除用户" + user.getLoginName() + "失败");
                }

            }
            RtxsvrapiObj.UnInit();
        }
    }

    /**
     * 同步所有用户
     */
    public void addAllUser() {
        Rtx rtx = getAll().get(0);
        List<User> userList = userService.getAll();
        for (User user : userList) {
            List<String> departmentIdList = userService.getDepartmentIdsByUserId(user.getId());// 获取当前用户所属的所有部门id
            if (departmentIdList.size() != 0) {
                for (String departmentId : departmentIdList) {
                    // 判断该用户是否属于某部门
                    if (departmentId != null) {
                        String deptId = departmentId.substring(1);
                        System.out.println("用户名：" + user.getUserName() + "；所在部门id为：" + deptId);
                        // 判断是否启用用户简称
                        if (rtx.getIsEnableAbbreviation()) {
                            addUser(user.getLoginName(), deptId, user.getUserName(), user.getPassword());
                        } else {
                            addUser(user.getUserName(), deptId, user.getUserName(), user.getPassword());
                        }
                    }
                }
            }
        }
    }

    /**
     * 根据父部门uuid来查找并添加所有子部门（递归）
     */
    public void getChildDeps(Department department) {
        List<Department> childDepsList = department.getChildren();
        if (childDepsList.size() > 0) {
            for (Department childDep : childDepsList) {
                String parentDepartmentId = department.getId().substring(1);
                String childDepartmentId = childDep.getId().substring(1);
                System.out.println(childDepartmentId + "；" + childDep.getRemark() + "；" + childDep.getName() + "；"
                        + parentDepartmentId);
                addDept(childDepartmentId, childDep.getRemark(), childDep.getName(), parentDepartmentId);

                if (childDep.getChildren().size() != 0) {
                    getChildDeps(childDep);
                }

            }
        } else {
            System.out.println("部门" + department.getUuid() + "没有子部门");
        }
    }

    /**
     * 根据父部门uuid来查找并添加rtx中不存在的部门
     */
    public void getAndAddChildDeps(Department department) {
        List<Department> childDepsList = department.getChildren();
        if (childDepsList.size() > 0) {
            for (Department childDep : childDepsList) {
                String parentDepartmentId = department.getId().substring(1);
                String childDepartmentId = childDep.getId().substring(1);
                if (DeptIsExist(childDepartmentId) == 0) {// 返回0为不存在
                    addDept(childDepartmentId, childDep.getRemark(), childDep.getName(), parentDepartmentId);
                }
                if (childDep.getChildren().size() != 0) {
                    getAndAddChildDeps(childDep);
                }
            }
        } else {
            System.out.println("部门" + department.getUuid() + "没有子部门");
        }
    }

    /**
     * rtx添加部门
     *
     * @param deptId
     * @param DetpInfo
     * @param DeptName
     * @param ParentDeptId
     */
    public void addDept(String deptId, String DetpInfo, String DeptName, String ParentDeptId) {
        int iRet = -1;

        RTXSvrApi RtxsvrapiObj = new RTXSvrApi();
        if (RtxsvrapiObj.Init()) {
            setSvrIpPort(RtxsvrapiObj);// 设置服务器ip和端口号
            iRet = RtxsvrapiObj.addDept(deptId, DetpInfo, DeptName, ParentDeptId);
            if (iRet == 0) {
                System.out.println("添加部门：" + DeptName + "成功！父部门id为：" + ParentDeptId);
            } else {
                System.out.println("添加部门失败");
            }
        }
        RtxsvrapiObj.UnInit();
    }

    /**
     * rtx添加用户
     *
     * @param userName
     * @param deptID
     * @param chsName
     * @param Pwd
     */
    public void addUser(String userName, String deptID, String chsName, String pwd) {
        int iRet = -1;

        RTXSvrApi RtxsvrapiObj = new RTXSvrApi();
        if (RtxsvrapiObj.Init()) {
            setSvrIpPort(RtxsvrapiObj);// 设置服务器ip和端口号
            iRet = RtxsvrapiObj.addUser(userName, deptID, chsName, pwd);
            if (iRet == 0) {
                System.out.println("添加用户：" + chsName + "成功！所属部门id为：" + deptID);
            } else {
                System.out.println("添加失败");
            }
        }
        RtxsvrapiObj.UnInit();
    }

    /**
     * rtx查看部门是否存在
     */
    public int DeptIsExist(String deptId) {
        int iRet = -1;

        RTXSvrApi RtxsvrapiObj = new RTXSvrApi();
        if (RtxsvrapiObj.Init()) {
            setSvrIpPort(RtxsvrapiObj);// 设置服务器ip和端口号
            iRet = RtxsvrapiObj.deptIsExist(deptId);
            if (iRet == -5) {
                System.out.println("部门" + deptId + "存在");
            } else if (iRet == 0) {
                System.out.println("部门" + deptId + "不存在");
            }
        }
        RtxsvrapiObj.UnInit();
        return iRet;
    }

    /**
     * rtx查看用户是否存在
     */
    public int userIsExist(String userName) {
        int iRet = -1;

        RTXSvrApi RtxsvrapiObj = new RTXSvrApi();
        if (RtxsvrapiObj.Init()) {
            setSvrIpPort(RtxsvrapiObj);// 设置服务器ip和端口号
            iRet = RtxsvrapiObj.userIsExist(userName);
            if (iRet == 0) {
                System.out.println("用户" + userName + "存在");
            } else {
                System.out.println("用户" + userName + "不存在");
            }
        }
        RtxsvrapiObj.UnInit();
        return iRet;
    }

    /**
     * oa往rtx发送消息
     */
    public void sendNotify(Message msg) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
        String currentTime = df.format(new Date());

        int iRet = -1;
        RTXSvrApi RtxsvrapiObj = new RTXSvrApi();
        if (RtxsvrapiObj.Init()) {
            setSvrIpPort(RtxsvrapiObj);// 设置服务器ip和端口号
            List<String> recipients = msg.getRecipients();// 所有接收用户
            for (String recipient : recipients) {
                User user = userService.getById(recipient);
                System.out.println(user.getLoginName() + ";" + msg.getSubject() + ";" + msg.getBody() + ";"
                        + msg.getType());
                String tempSubject = "";
                if (msg.getSubject() != null) {
                    iRet = RtxsvrapiObj.sendNotify(user.getLoginName(), msg.getSubject(), msg.getBody(), msg.getType(),
                            currentTime);
                } else {
                    iRet = RtxsvrapiObj.sendNotify(user.getLoginName(), tempSubject, msg.getBody(), msg.getType(),
                            currentTime);
                }
                if (iRet == 0) {
                    System.out.println("发送消息给" + user.getLoginName() + "成功");
                } else {
                    System.out.println("发送消息给" + user.getLoginName() + "失败");
                }
            }
        }
        RtxsvrapiObj.UnInit();
    }

    /**
     * 获取子部门列表
     *
     * @param DeptID
     * @return
     */
    public List<String> getChildDepts(String deptID) {
        List<String> childDeptsList = new ArrayList<String>();

        String depts[] = null;
        int iRet = -1;

        RTXSvrApi RtxsvrapiObj = new RTXSvrApi();
        if (RtxsvrapiObj.Init()) {
            setSvrIpPort(RtxsvrapiObj);// 设置服务器ip和端口号
            depts = RtxsvrapiObj.getChildDepts(deptID);
            if (depts != null) {
                for (int i = 0; i < depts.length; i++) {
                    System.out.println(depts[i]);
                    childDeptsList.add(depts[i]);
                }
            } else {
                System.out.println("该部门没有子部门");
            }
        }
        RtxsvrapiObj.UnInit();
        return childDeptsList;
    }

    /**
     * 单点登录
     *
     * @return
     */
    @Override
    public String singleSignOn(RtxBean bean) {
        String currentUserid = SpringSecurityUtils.getCurrentUserId();
        System.out.println("当前登录用户id:" + currentUserid);
        User user = userService.getById(currentUserid);
        String sessionKey = getSessionKey(user.getLoginName());
        System.out.println("返回的sessionKey为：" + sessionKey);
        return sessionKey;
    }

    /**
     * rtx单点登录获取sessionKey
     *
     * @param strUser
     * @return
     */
    public String getSessionKey(String strUser) {
        String strSessionKey = "";
        String strURL = "http://127.0.0.1:8012/GetSession.cgi?receiver=" + strUser;
        BufferedReader reader = null;
        try {
            java.net.URL url = new URL(strURL);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();

            reader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            strSessionKey = reader.readLine();

        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        } finally {
            IOUtils.closeQuietly(reader);
        }
        return strSessionKey;
    }

    /**
     * rtx根据部门ID查部门名称
     *
     * @param deptId
     */
    public String getDeptName(String deptId) {
        String deptName = null;
        RTXSvrApi RtxsvrapiObj = new RTXSvrApi();
        if (RtxsvrapiObj.Init()) {
            setSvrIpPort(RtxsvrapiObj);// 设置服务器ip和端口号
            deptName = RtxsvrapiObj.GetDeptName(deptId);

            if (deptName == null) {
                System.out.println("获取失败");
            } else {
                System.out.println("部门名称:" + deptName);
            }
        }
        RtxsvrapiObj.UnInit();
        return deptName;
    }

    /**
     * 删除部门
     */
    public void delDepts(String deptId, String type) {

        int iRet = -1;

        RTXSvrApi RtxsvrapiObj = new RTXSvrApi();
        if (RtxsvrapiObj.Init()) {
            setSvrIpPort(RtxsvrapiObj);// 设置服务器ip和端口号
            iRet = RtxsvrapiObj.deleteDept(deptId, type);

            if (iRet == 0) {
                System.out.println("删除部门" + deptId + "成功");

            } else {
                System.out.println("删除部门" + deptId + "失败");
            }

        }
        RtxsvrapiObj.UnInit();
    }

    /**
     * 获取部门下的所有用户
     *
     * @param DeptID
     * @return
     */
    public List<String> getDeptUsers(String deptId) {
        List<String> deptUserList = new ArrayList<String>();

        String users[] = null;
        int iRet = -1;

        RTXSvrApi RtxsvrapiObj = new RTXSvrApi();
        if (RtxsvrapiObj.Init()) {
            setSvrIpPort(RtxsvrapiObj);// 设置服务器ip和端口号
            users = RtxsvrapiObj.getDeptUsers(deptId);
            if (users != null) {
                for (int i = 0; i < users.length; i++) {
                    deptUserList.add(users[i]);
                }
            }
        }
        RtxsvrapiObj.UnInit();
        return deptUserList;
    }

    /**
     * 删除用户
     *
     * @param userName
     */
    public void delUser(String userName) {
        int iRet = -1;
        RTXSvrApi RtxsvrapiObj = new RTXSvrApi();
        if (RtxsvrapiObj.Init()) {
            setSvrIpPort(RtxsvrapiObj);// 设置服务器ip和端口号
            iRet = RtxsvrapiObj.deleteUser(userName);
            if (iRet == 0) {
                System.out.println("删除成功用户" + userName + "成功~");
            } else {
                System.out.println("删除成功用户" + userName + "失败~");
            }

        }
        RtxsvrapiObj.UnInit();
    }

    /**
     * 删除字符串前缀的所有零
     */
    public String delPrefixZero(String str) {
        String newStr = str.replaceAll("^(0+)", "");
        return newStr;
    }

    /**
     * 获取数据库中默认的rtx对象
     *
     * @return
     */
    public Rtx getRtx() {
        Rtx rtx = null;
        if (getAll().size() != 0) {
            rtx = getAll().get(0);
            System.out.println("rtx默认数据库不为空");
        } else {
            System.out.println("RTX默认数据库为空！");
        }
        return rtx;
    }

    /**
     * 设置rtx服务器的ip与端口
     *
     * @param RtxsvrapiObj
     */

    public void setSvrIpPort(RTXSvrApi RtxsvrapiObj) {
        if (getRtx() != null) {
            String SvrIP = getRtx().getRtxServerIp();
            Integer iPort = getRtx().getRtxServerPort();
            RtxsvrapiObj.setServerIP(SvrIP);
            RtxsvrapiObj.setServerPort(iPort);
            System.out.println("操作成功" + "\n" + "服务器地址:" + SvrIP + "\n" + "SDK服务器端口:" + iPort);
        } else {
            System.out.println("rtx数据库默认为空！");
        }
    }
}
