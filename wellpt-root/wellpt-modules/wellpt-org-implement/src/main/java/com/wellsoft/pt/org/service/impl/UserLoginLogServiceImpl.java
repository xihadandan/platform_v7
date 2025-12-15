package com.wellsoft.pt.org.service.impl;

import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.jdbc.support.Page;
import com.wellsoft.context.util.web.ServletUtils;
import com.wellsoft.pt.cache.config.CacheName;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.bean.UserLoginLogVo;
import com.wellsoft.pt.org.bean.UserLoginLogBean;
import com.wellsoft.pt.org.dao.UserLoginLogDao;
import com.wellsoft.pt.org.entity.Department;
import com.wellsoft.pt.org.entity.DepartmentUserJob;
import com.wellsoft.pt.org.entity.User;
import com.wellsoft.pt.org.entity.UserLoginLog;
import com.wellsoft.pt.org.service.UserLoginLogService;
import com.wellsoft.pt.org.service.UserService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
@Transactional
public class UserLoginLogServiceImpl extends BaseServiceImpl implements UserLoginLogService {

    //    @Autowired
    private UserLoginLogDao userLoginLogDao;

    @Autowired
    private UserService userService;

    @Override
    public void saveBean(UserLoginLog bean) {
        userLoginLogDao.save(bean);
    }

    @Override
    public JqGridQueryData query(String userName, JqGridQueryInfo queryInfo) {
        Page<UserLoginLog> pageData = new Page<UserLoginLog>();
        pageData.setPageNo(queryInfo.getPage());
        pageData.setPageSize(queryInfo.getRows());

        StringBuilder hql = new StringBuilder(
                "select log from UserLoginLog log, User user where 1=1 and log.userUuid = user.uuid ");
        if (userName != null && !userName.equals("")) {
            hql.append("and (user.userName like '%" + userName + "%' or user.loginName like '%" + userName
                    + "%' or user.departmentName like '%" + userName + "%' or log.loginIp like '%" + userName + "%' )");
        }
        if ("loginName".equals(queryInfo.getSidx()) || "userName".equals(queryInfo.getSidx())
                || "departmentName".equals(queryInfo.getSidx())) {
            pageData.setOrderBy("user." + queryInfo.getSidx());
        } else {
            pageData.setOrderBy("log." + queryInfo.getSidx());
        }
        pageData.setOrder(queryInfo.getSord());
        userLoginLogDao.findPage(pageData, hql.toString());

        List<UserLoginLog> userLoginLogs = pageData.getResult();
        List<UserLoginLogBean> userLoginLogBeans = new ArrayList<UserLoginLogBean>();
        for (UserLoginLog log : userLoginLogs) {
            UserLoginLogBean logBean = new UserLoginLogBean();
            User user = userService.get(log.getUserUuid());
            logBean.setUuid(log.getUuid());
            logBean.setLoginName(user.getLoginName());
            logBean.setUserName(user.getUserName());
            if (user.getDepartmentUsers() != null && !user.getDepartmentUsers().isEmpty()) {
                StringBuilder userDeptStr = new StringBuilder();
                for (DepartmentUserJob userDept : user.getDepartmentUsers()) {
                    Department department = userDept.getDepartment();
                    if (department != null) {
                        userDeptStr.append(department.getPath()).append(";");
                    }
                }
                if (userDeptStr.indexOf(";") > 0) {
                    userDeptStr.deleteCharAt(userDeptStr.lastIndexOf(";"));
                }
                logBean.setDepartmentName(userDeptStr.toString());
            }
            logBean.setLoginTime(log.getLoginTime());
            logBean.setLoginIp(log.getLoginIp());
            userLoginLogBeans.add(logBean);
        }
        JqGridQueryData queryData = new JqGridQueryData();
        queryData.setCurrentPage(queryInfo.getPage());
        queryData.setDataList(userLoginLogBeans);
        queryData.setRepeatitems(false);
        queryData.setTotalPages(pageData.getTotalPages());
        queryData.setTotalRows(pageData.getTotalCount());
        return queryData;
    }

    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public void deleteByUuids(Collection<String> uuids) {
        for (String uuid : uuids) {
            this.userLoginLogDao.delete(uuid);
        }
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserLoginLogService#find(java.lang.String, java.util.HashMap)
     */
    @Override
    public List<String> find(String hql, HashMap hashMap) {
        return userLoginLogDao.find(hql, hashMap);
    }

    @Override
    public void loginLog(HttpServletRequest request, UserLoginLogVo vo) {
        UserLoginLog loginLog = getUserLoginLog(request, vo);
        loginLog.setLoginTime(Calendar.getInstance().getTime());
        //userLoginLogDao.save(loginLog);
    }

    private UserLoginLog getUserLoginLog(HttpServletRequest request, UserLoginLogVo vo) {
        UserLoginLog loginLog = new UserLoginLog();
        BeanUtils.copyProperties(vo, loginLog);
        loginLog.setUserUuid(SpringSecurityUtils.getCurrentUserUuid());
        if (StringUtils.isBlank(vo.getLoginIp())) {
            loginLog.setLoginIp(ServletUtils.getRemoteAddr(request));
        } else {
            loginLog.setLoginIp(vo.getLoginIp());
        }
        loginLog.setLoginSource(vo.getLoginSource());
        return loginLog;
    }

    @Override
    public void logoutLog(HttpServletRequest request, UserLoginLogVo vo) {
        UserLoginLog loginLog = getUserLoginLog(request, vo);
        loginLog.setLogoutTime(Calendar.getInstance().getTime());
        userLoginLogDao.save(loginLog);
    }
}
