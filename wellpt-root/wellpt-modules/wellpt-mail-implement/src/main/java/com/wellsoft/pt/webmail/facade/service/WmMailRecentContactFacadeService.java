/*
 * @(#)2018年3月19日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.facade.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.webmail.bean.WmMailRecentContactDto;

import java.util.List;

/**
 * Description: 最近联系人facade服务接口
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
public interface WmMailRecentContactFacadeService extends BaseService {

    /**
     * 删除当前用户的所有最近联系人记录
     */
    void deleteCurrentUserRecentContact();

    /**
     * 查询当前用户的最近联系人记录，按最近联系时间降序查询
     */
    List<WmMailRecentContactDto> queryCurrentUserRecentContacts();
}
