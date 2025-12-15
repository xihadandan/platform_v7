/*
 * @(#)2013-12-25 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.web.json;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-25.1	zhulh		2013-12-25		Create
 * </pre>
 * @date 2013-12-25
 */
@Component
public class JsonDataServicesConfigImpl implements JsonDataServicesConfig {

    @Value("#{'${jds.anonymousServices:certificateLoginService.getLoginName"
            + ",certificateLoginService.getLoginNameByKey" + ",commonUnitTreeService.getAsTreeList"
            + ",securityAuditFacadeService.isGranted" + ",appContextService.getFunctionByPath}'.split(',')}")
    private List<String> anonymousServices;

    @Value("#{'${jds.loginedAuthenticatedServices:appContextService.getFunctionByPath,appContextService.getJavaScriptModuleConfigScript,mongoFileService.getNonioFilesFromFolder}'.split(',')}")
    private Set<String> loginedAuthenticatedServices;

    /**
     * @return the anonymousServices
     */
    public List<String> getAnonymousServices() {
        return anonymousServices;
    }

    /**
     * @param anonymousServices 要设置的anonymousServices
     */
    public void setAnonymousServices(List<String> anonymousServices) {
        this.anonymousServices = anonymousServices;
    }

    public Set<String> getLoginedAuthenticatedServices() {
        return loginedAuthenticatedServices;
    }

    public void setLoginedAuthenticatedServices(Set<String> loginedAuthenticatedServices) {
        this.loginedAuthenticatedServices = loginedAuthenticatedServices;
    }
}
