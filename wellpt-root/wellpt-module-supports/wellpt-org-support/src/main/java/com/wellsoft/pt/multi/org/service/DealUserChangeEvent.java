package com.wellsoft.pt.multi.org.service;

import com.wellsoft.pt.multi.org.bean.OrgUserVo;

public interface DealUserChangeEvent {

    //处理添加用户事件, 是否异步处理
    public void dealAddUserEvent(OrgUserVo userVo);

    //处理用户信息变更事件
    public void dealModifyUserInfoEvent(OrgUserVo oldVo, OrgUserVo newVo);

    //处理删除用户事件
    public void dealDeleteUserEvent(OrgUserVo userVo);
}
