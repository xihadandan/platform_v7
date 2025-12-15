/*
 * @(#)2018年3月1日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.calendar.facade.service.impl;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2GroupData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.app.calendar.entity.MyAttentionEntity;
import com.wellsoft.pt.app.calendar.entity.MyCalendarGroupEntity;
import com.wellsoft.pt.app.calendar.facade.service.AttentionFacade;
import com.wellsoft.pt.app.calendar.service.MyAttentionService;
import com.wellsoft.pt.app.calendar.service.MyCalendarGroupService;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Description: 关注
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月1日.1	zyguo		2018年3月1日		Create
 * </pre>
 * @date 2018年3月1日
 */
@Service
public class AttentionFacadeImpl extends AbstractApiFacade implements AttentionFacade {

    @Resource
    MyAttentionService myAttentionService;

    @Resource
    MyCalendarGroupService calendarGroupService;

    @Resource
    OrgApiFacade orgApiFacade;

    @Override
    public List<OrgUserVo> queryMyAttentionUserList() {
        List<OrgUserVo> users = Lists.newArrayList();
        List<MyAttentionEntity> list = myAttentionService.queryMyAttentionListByType(MyAttentionEntity.TYPE_USER);
        if (CollectionUtils.isNotEmpty(list)) {
            for (MyAttentionEntity myAttentionEntity : list) {
                OrgUserVo u = orgApiFacade.getUserVoById(myAttentionEntity.getAttentionObjId());
                users.add(u);
            }
        }
        return users;
    }

    @Override
    public boolean addAttentionUsers(String[] userIds) {
        String currUserId = SpringSecurityUtils.getCurrentUserId();
        List<MyAttentionEntity> myAttentionlist = this.myAttentionService
                .queryMyAttentionListByType(MyAttentionEntity.TYPE_USER);
        List<String> myAttentionUsers = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(myAttentionlist)) {
            myAttentionUsers = Lists.transform(myAttentionlist, new Function<MyAttentionEntity, String>() {
                @Override
                public String apply(MyAttentionEntity arg0) {
                    return arg0.getAttentionObjId();
                }
            });
        }

        for (String uId : userIds) {
            if (uId.equals(currUserId) || myAttentionUsers.contains(uId)) {
                // 自己不能关注自己, 已经关注过的无需再关注
                continue;
            }
            MyAttentionEntity e = new MyAttentionEntity();
            e.setAttentionObjType(MyAttentionEntity.TYPE_USER);
            e.setAttentionObjId(uId);
            e.setCreator(currUserId);
            this.myAttentionService.save(e);
        }
        return true;
    }

    @Override
    public boolean cancelAttentionUser(String userId) {
        return myAttentionService.cancelAttentionUser(userId);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.calendar.facade.service.impl.AttentionFacade#queryMyAttentionListForSelect2Group(com.wellsoft.context.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2GroupData queryMyAttentionListForSelect2Group(Select2QueryInfo queryInfo) {
        List<OrgUserVo> myUserList = this.queryMyAttentionUserList();
        Select2GroupData data = new Select2GroupData();
        if (CollectionUtils.isNotEmpty(myUserList)) {
            for (OrgUserVo user : myUserList) {
                Select2DataBean bean = new Select2DataBean(user.getId(), user.getUserName());
                data.addResultData("个人", bean);
            }
        }
        List<MyCalendarGroupEntity> groupList = this.queryMyAttentionGroupList();
        if (CollectionUtils.isNotEmpty(groupList)) {
            for (MyCalendarGroupEntity group : groupList) {
                Select2DataBean bean = new Select2DataBean(group.getUuid(), group.getGroupName());
                data.addResultData("群组", bean);
            }
        }
        return data;
    }

    //
    @Override
    public boolean addAttentionGroup(String groupName, String[] userIds, String[] userNames) {
        // 检查群组是否重名
        String currUserId = SpringSecurityUtils.getCurrentUserId();
        Object obj = calendarGroupService.getGroupByName(currUserId, groupName);
        if (obj != null) {
            throw new RuntimeException("该群组已存在，请换一个组名");
        }
        if (userIds == null || userIds.length == 0) {
            throw new RuntimeException("请选择群组成员");
        }

        // 不存在，则创建一个群组
        MyCalendarGroupEntity g = new MyCalendarGroupEntity();
        g.setGroupName(groupName);
        g.setGroupMembers(StringUtils.join(userIds, ";"));
        g.setGroupMembersName(StringUtils.join(userNames, ";"));
        g.setCreator(currUserId);
        this.calendarGroupService.save(g);

        // 关注该群组
        MyAttentionEntity attention = new MyAttentionEntity();
        attention.setAttentionObjType(MyAttentionEntity.TYPE_GROUP);
        attention.setAttentionObjId(g.getUuid());
        attention.setCreator(currUserId);
        this.myAttentionService.save(attention);
        return true;
    }

    @Override
    public boolean cancelAttentionGroup(String groupUuid) {
        MyCalendarGroupEntity g = this.calendarGroupService.getOne(groupUuid);
        if (g == null) {
            throw new RuntimeException("对应的群组不存在");
        }
        // 解除关注关系
        this.myAttentionService.cancelAttentionGroup(groupUuid);
        // 删除群
        calendarGroupService.delete(g);
        return true;
    }

    // 获取我关注的群组列表
    @Override
    public List<MyCalendarGroupEntity> queryMyAttentionGroupList() {
        List<MyCalendarGroupEntity> groups = Lists.newArrayList();
        List<MyAttentionEntity> list = myAttentionService.queryMyAttentionListByType(MyAttentionEntity.TYPE_GROUP);
        if (CollectionUtils.isNotEmpty(list)) {
            for (MyAttentionEntity myAttentionEntity : list) {
                MyCalendarGroupEntity g = calendarGroupService.getOne(myAttentionEntity.getAttentionObjId());
                groups.add(g);
            }
        }
        return groups;
    }

    @Override
    public boolean modifyGroupMembers(String groupUuid, String[] userIds, String[] userNames, String groupName) {
        MyCalendarGroupEntity obj = calendarGroupService.getOne(groupUuid);
        if (obj == null) {
            throw new RuntimeException("对应的群组不存在");
        }
        if (userIds == null || userIds.length == 0) {
            throw new RuntimeException("请选择群组成员");
        }
        obj.setGroupName(groupName);
        obj.setGroupMembers(StringUtils.join(userIds, ";"));
        obj.setGroupMembersName(StringUtils.join(userNames, ";"));
        this.calendarGroupService.save(obj);
        return true;
    }

}
