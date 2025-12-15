/*
 * @(#)2017-11-21 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.bean;

import com.wellsoft.pt.multi.org.entity.MultiOrgVersion;
import org.apache.commons.lang.StringUtils;

/**
 * Description: 如何描述该类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-11-21.1	zyguo		2017-11-21		Create
 * </pre>
 * @date 2017-11-21
 */
public class OrgVersionDto extends MultiOrgVersion {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -82362297526360180L;

    private String orgName;

    /**
     * @return the fullName
     */
    public String getFullName() {
        String[] names = new String[]{getOrgName(), this.getFunctionTypeName(), this.getVersion()};
        return StringUtils.join(names, "-");
    }

    /**
     * @return the orgName
     */
    public String getOrgName() {
        return orgName;
    }

    /**
     * @param orgName 要设置的orgName
     */
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

}
