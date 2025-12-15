/*
 * @(#)2021-03-23 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.multi.org.dto.MultiOrgPwdSettingDto;

/**
 * Description: 数据库表MULTI_ORG_PWD_SETTING的门面服务接口，提供给其他模块以及前端调用的业务接口
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-03-23.1	zenghw		2021-03-23		Create
 * </pre>
 * @date 2021-03-23
 */
public interface MultiOrgPwdSettingFacadeService extends Facade {

    /**
     * 保存密码配置
     *
     * @param saveMultiOrgPwdSetting 密码配置对象
     * @return void
     **/
    public void saveMultiOrgPwdSetting(MultiOrgPwdSettingDto saveMultiOrgPwdSetting);

    /**
     * 获取密码配置
     *
     * @param
     * @return 不存在则返回空对象
     **/
    public MultiOrgPwdSettingDto getMultiOrgPwdSetting();

    /**
     * 密码有效期提醒
     * 查找出符合条件的数据-提醒日=到期日-提醒天数（提醒日=当天的日期才提醒）
     * 发送信息 账号表有userId
     * messageClientApiFacade.sendByParams(params);
     **/
    public Integer pwdValidityWarn();

    /**
     * 密码解锁定
     **/
    public void pwdUnlock();

}
