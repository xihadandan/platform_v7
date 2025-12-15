package com.wellsoft.pt.org.bean;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.pt.org.entity.Duty;
import com.wellsoft.pt.security.audit.entity.Role;

import java.util.HashSet;
import java.util.Set;

/**
 * Description: 职务VO类
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-8-22.1  zhengky	2014-8-22	  Create
 * </pre>
 * @date 2014-8-22
 */
public class DutyBean extends Duty {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -3355226102774784704L;
    @UnCloneable
    private Set<Role> roles = new HashSet<Role>(0);

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }


}
