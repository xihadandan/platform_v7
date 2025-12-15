/*
 * @(#)2016-03-11 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.bean;

import com.wellsoft.pt.common.fdext.bean.CdFieldExtensionValue;
import com.wellsoft.pt.org.entity.OrgCorporation;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-03-11.1	zhongzh		2016-03-11		Create
 * </pre>
 * @date 2016-03-11
 */
public class OrgCorporationBean extends OrgCorporation {

    public final static String EXTVALUE_GROUP_TYPE = "001007001";
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1457678034025L;
    private CdFieldExtensionValue extValue;

    /**
     * @return the extValue
     */
    public CdFieldExtensionValue getExtValue() {
        return extValue;
    }

    /**
     * @param extValue 要设置的extValue
     */
    public void setExtValue(CdFieldExtensionValue extValue) {
        this.extValue = extValue;
    }

}
