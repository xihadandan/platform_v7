package com.wellsoft.pt.api.response;

import com.wellsoft.pt.api.WellptResponse;

/**
 * 查询用户服务响应
 *
 * @author Lmw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-3-16.1	lmw		2015-3-16		Create
 * </pre>
 * @date 2015-3-16
 */
public class QueryForUsersResponse extends WellptResponse {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 5277540925453441065L;

    // 结果集
    private Object users;

    public Object getUsers() {
        return users;
    }

    public void setUsers(Object users) {
        this.users = users;
    }
}
