/*
 * @(#)2018年3月19日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.webmail.bean.WmMailRecentContactDto;
import com.wellsoft.pt.webmail.dao.WmMailRecentContactDao;
import com.wellsoft.pt.webmail.entity.WmMailRecentContactEntity;

import java.util.List;

/**
 * Description: 最近联系人接口类
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
public interface WmMailRecentContactService extends
        JpaService<WmMailRecentContactEntity, WmMailRecentContactDao, String> {

    /**
     * 根据用户id，删除用户下的所有最近联系人
     */
    void deleteUserRecentContact(String currentUserId);

    /**
     * 查询当前用户的最近联系人记录，按最近联系时间降序查询
     */
    List<WmMailRecentContactDto> queryRecentContactByUserId(String currentUserId);

    void saveRecentContact(List<WmMailRecentContactDto> dtoList);

}
