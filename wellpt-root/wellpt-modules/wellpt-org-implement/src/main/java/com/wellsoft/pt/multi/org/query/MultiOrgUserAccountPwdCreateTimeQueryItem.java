package com.wellsoft.pt.multi.org.query;

import com.wellsoft.context.jdbc.support.BaseQueryItem;

import java.util.Date;

/**
 * Description: 账号密码创建时间查询对象
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/4/1.1	    zenghw		2021/4/1		    Create
 * </pre>
 * @date 2021/4/1
 */
public class MultiOrgUserAccountPwdCreateTimeQueryItem implements BaseQueryItem {

    private String uuid;
    // 账号ID,也是用户ID
    private String id;
    // 密码创建重置时间
    private Date pwdCreateTime;
    // 用户名
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getPwdCreateTime() {
        return pwdCreateTime;
    }

    public void setPwdCreateTime(Date pwdCreateTime) {
        this.pwdCreateTime = pwdCreateTime;
    }
}
