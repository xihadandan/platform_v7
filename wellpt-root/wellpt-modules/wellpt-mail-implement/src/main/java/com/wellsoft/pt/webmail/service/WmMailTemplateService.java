/*
 * @(#)2018年3月12日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.webmail.bean.WmMailTemplateDto;
import com.wellsoft.pt.webmail.dao.WmMailTemplateDao;
import com.wellsoft.pt.webmail.entity.WmMailTemplateEntity;

import java.util.List;

/**
 * Description: 写信模板服务
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月12日.1	chenqiong		2018年3月12日		Create
 * </pre>
 * @date 2018年3月12日
 */
public interface WmMailTemplateService extends JpaService<WmMailTemplateEntity, WmMailTemplateDao, String> {

    /**
     * 更新写信模板的默认状态
     *
     * @param isDefault
     * @param uuid
     */
    void updateMailTemplateDefaultStatus(Boolean isDefault, String uuid);

    /**
     * 更新写信模板内容
     *
     * @param dto
     */
    void updateMailTemplate(WmMailTemplateDto dto);

    /**
     * 获取写信模板的详情（包含大字段内容）
     *
     * @param uuid
     * @return
     */
    WmMailTemplateDto getMailTemplateDetail(String uuid);

    /**
     * 新增写信模板
     *
     * @param dto
     */
    void addMailTemplate(WmMailTemplateDto dto);

    /**
     * 返回渲染模板的实际内容
     *
     * @param
     */
    String renderMailTemplateContent(String uuid);

    /**
     * 查询当前用户的所有写信模板，不包含大字段内容
     */
    List<WmMailTemplateDto> listCurrentUserMailTemplates();

    WmMailTemplateDto getCurrentUserDefaultTemplate();

}
