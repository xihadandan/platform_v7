package com.wellsoft.pt.security.access;

import com.wellsoft.pt.user.dto.UserDetailsVo;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年02月06日   chenq	 Create
 * </pre>
 */
public class RequestUserResolved {

    private static ThreadLocal<UserDetailsVo> USER = new ThreadLocal<>();

    public static UserDetailsVo userDetails() {
        return USER.get();
    }

    public static void userDetails(UserDetailsVo vo) {
        // 通过缓存获取并设置用户的组织信息
        USER.set(vo);
    }

    public static void clear() {
        USER.remove();
    }

}
