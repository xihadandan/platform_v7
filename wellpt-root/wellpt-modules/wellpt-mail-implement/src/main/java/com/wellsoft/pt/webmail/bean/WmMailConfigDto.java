/*
 * @(#)2016-06-03 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.bean;

import com.wellsoft.pt.multi.org.entity.MultiOrgOption;
import com.wellsoft.pt.webmail.entity.WmMailConfigEntity;

import java.util.List;

/**
 * Description: 邮件配置dto
 *
 * @author t
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-06-03.1	t		2016-06-03		Create
 * </pre>
 * @date 2016-06-03
 */
public class WmMailConfigDto extends WmMailConfigEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1464933721032L;
    private List<MultiOrgOption> allowOrgOptionList;
    private List<WmMailUserDto> senterMailAddresses;

    public List<MultiOrgOption> getAllowOrgOptionList() {
        return allowOrgOptionList;
    }

    public void setAllowOrgOptionList(List<MultiOrgOption> allowOrgOptionList) {
        this.allowOrgOptionList = allowOrgOptionList;
    }

    public List<WmMailUserDto> getSenterMailAddresses() {
        return senterMailAddresses;
    }

    public void setSenterMailAddresses(List<WmMailUserDto> senterMailAddresses) {
        this.senterMailAddresses = senterMailAddresses;
    }
}
