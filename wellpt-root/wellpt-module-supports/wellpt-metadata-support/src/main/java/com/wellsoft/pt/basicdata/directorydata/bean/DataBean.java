package com.wellsoft.pt.basicdata.directorydata.bean;

import com.wellsoft.pt.basicdata.directorydata.entity.Data;
import org.springframework.security.acls.model.Permission;

import java.util.List;
import java.util.Map;

/**
 * Description: Data封装类
 *
 * @author huanglinchuan
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-1-15.1	huanglinchuan		2015-1-15		Create
 * </pre>
 * @date 2015-1-15
 */
public class DataBean extends Data {

    private static final long serialVersionUID = -302359295085633L;

    /**
     * acl权限配置信息
     */
    private Map<Permission, List<String>> aclConfigInfo;

    public Map<Permission, List<String>> getAclConfigInfo() {
        return aclConfigInfo;
    }

    public void setAclConfigInfo(Map<Permission, List<String>> aclConfigInfo) {
        this.aclConfigInfo = aclConfigInfo;
    }

}
