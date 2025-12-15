/*
 * @(#)2018年2月27日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.calendar.facade.service;

import com.wellsoft.context.component.select2.Select2GroupData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.app.calendar.entity.MyCalendarGroupEntity;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;

import java.util.List;

/**
 * Description: 公共通讯录标签门面服务
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年2月27日.1	chenqiong		2018年2月27日		Create
 * </pre>
 * @date 2018年2月27日
 */
public interface AttentionFacade extends BaseService {

    // 获取我关注的用户列表
    public List<OrgUserVo> queryMyAttentionUserList();

    // 关注用户
    public boolean addAttentionUsers(String[] userId);

    // 取消关注用户
    public boolean cancelAttentionUser(String userId);

    // 同时获取我的日历本列表和负责的他人日历本列表，以分组selec2方式展示
    public Select2GroupData queryMyAttentionListForSelect2Group(Select2QueryInfo queryInfo);

    // 关注群组
    boolean addAttentionGroup(String groupName, String[] userIds, String[] userNames);

    // 取消关注群组
    public boolean cancelAttentionGroup(String groupUuid);

    // 获取我关注的群组列表
    public List<MyCalendarGroupEntity> queryMyAttentionGroupList();

    // 变更群组成员信息
    boolean modifyGroupMembers(String groupUuid, String[] userIds, String[] userNames, String groupName);
}
