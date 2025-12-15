package com.wellsoft.pt.org.service;

import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.pt.multi.org.bean.UserLoginLogVo;
import com.wellsoft.pt.org.entity.UserLoginLog;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Deprecated
public interface UserLoginLogService {
    public void saveBean(UserLoginLog bean);

    public JqGridQueryData query(String userName, JqGridQueryInfo queryInfo);

    public void deleteByUuids(Collection<String> uuids);

    /**
     * 如何描述该方法
     *
     * @param hql
     * @param hashMap
     * @return
     */
    public List<String> find(String hql, HashMap hashMap);

    /**
     * 登录日志
     *
     * @param vo
     */
    void loginLog(HttpServletRequest request, UserLoginLogVo vo);

    /**
     * 登出日志
     *
     * @param vo
     */
    void logoutLog(HttpServletRequest request, UserLoginLogVo vo);
}
