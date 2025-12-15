package com.wellsoft.pt.org.adsycn.service;

import com.wellsoft.pt.org.adsycn.vo.ADUser;

/**
 * Description: 如何描述该类
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-10-13.1  zhengky	2014-10-13	  Create
 * </pre>
 * @date 2014-10-13
 */
public interface ADUserService {

    public void add(ADUser adUser) throws Exception;

    public void update(ADUser adUser) throws Exception;

    public void delete(ADUser adUser) throws Exception;

    public boolean checkIsExist(ADUser adUser) throws Exception;

    /**
     * 根据DN修改密码
     *
     * @param userDn
     * @param pwd
     * @throws Exception
     */
    public void changePwdByDn(String userDn, String pwd) throws Exception;

    public void renameDn(ADUser oldadUser, ADUser newadUser) throws Exception;

    /**
     * 重命名用户CN
     *
     * @param oldadUserDn
     * @param newadUserDn
     * @throws Exception
     */
    public void renameDn(String oldadUserDn, String newadUserDn) throws Exception;

    /**
     * 通过登录名获得DN
     *
     * @param loginName
     * @return
     */
    public String getDnByLoginName(String loginName) throws Exception;

    /**
     * @param loginName
     * @return
     * @author liyb
     * date 2015.01.13
     * 判断OU=OU中是否存在用户，若存在，则返回用户的全路径
     */
    public String getOUDnByLoginName(String loginName) throws Exception;

    /**
     * 校验用户名和密码是否匹配
     *
     * @param userDn
     * @param pwd
     * @return
     */
    public boolean checkUserPwd(String userDn, String pwd) throws Exception;

    /**
     * 获得用户DN
     *
     * @param adUser
     * @return
     * @throws Exception
     */
    public String getDnByAdUser(ADUser adUser) throws Exception;
}
