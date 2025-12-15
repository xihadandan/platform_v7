/*
 * @(#)2018年3月19日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.facade.service.impl;

import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.webmail.bean.WmMailRecentContactDto;
import com.wellsoft.pt.webmail.facade.service.WmMailRecentContactFacadeService;
import com.wellsoft.pt.webmail.service.WmMailRecentContactService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Description: 最近联系人facade服务实现类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月19日.1	chenqiong		2018年3月19日		Create
 * </pre>
 * @date 2018年3月19日
 */
@Service
public class WmMailRecentContactFacadeServiceImpl extends AbstractApiFacade implements WmMailRecentContactFacadeService {

    @Resource
    WmMailRecentContactService wmMailRecentContactService;

    @Override
    public void deleteCurrentUserRecentContact() {
        wmMailRecentContactService.deleteUserRecentContact(SpringSecurityUtils.getCurrentUserId());
    }

    @Override
    public List<WmMailRecentContactDto> queryCurrentUserRecentContacts() {
        return wmMailRecentContactService.queryRecentContactByUserId(SpringSecurityUtils.getCurrentUserId());
    }

}
