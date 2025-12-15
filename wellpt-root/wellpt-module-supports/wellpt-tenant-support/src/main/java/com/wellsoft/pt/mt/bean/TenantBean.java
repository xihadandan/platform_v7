package com.wellsoft.pt.mt.bean;

import com.wellsoft.pt.mt.entity.Tenant;

/**
 * Description: 单位注册VO类
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-8-16.1	zhouyq		2013-8-16		Create
 * </pre>
 * @date 2013-8-16
 */
public class TenantBean extends Tenant {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private boolean createDatabase;

    /**
     * 如何描述tenantTemplateName
     */
    private String tenantTemplateName;

    public String getTenantTemplateName() {
        return tenantTemplateName;
    }

    public void setTenantTemplateName(String tenantTemplateName) {
        this.tenantTemplateName = tenantTemplateName;
    }

    /**
     * @return the createDatabase
     */
    public boolean isCreateDatabase() {
        return createDatabase;
    }

    /**
     * @param createDatabase 要设置的createDatabase
     */
    public void setCreateDatabase(boolean createDatabase) {
        this.createDatabase = createDatabase;
    }

}
