/*
 * @(#)2018年3月12日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.facade.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.webmail.bean.WmMailTemplateDto;

import java.util.List;

/**
 * Description: 写信模板门面服务
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
public interface WmMailTemplateFacadeService extends BaseService {

    /**
     * 查询当前用户定义的所有写信模板
     */
    List<WmMailTemplateDto> listCurrentUserMailTemplates();

    /**
     * 获取当前用户默认的写信模板,包含模板的真实渲染内容
     */
    WmMailTemplateDto getCurrentUserDefaultTemplate();

    /**
     * 删除模板
     *
     * @param uuids
     */
    void deleteMailTemplatesByUuids(List<String> uuids);

    /**
     * 更新模板的是否默认状态
     *
     * @param isDefault
     * @param uuid
     */
    void updateMailTemplateDefaultStatus(Boolean isDefault, String uuid);

    /**
     * 更新模板内容
     *
     * @param dto
     */
    void updateMailTemplate(WmMailTemplateDto dto);

    /**
     * 新增写信模板
     *
     * @param dto
     */
    void addMailTemplate(WmMailTemplateDto dto);

    /**
     * 获取写信模板内容（包含大字段的模板内容）
     *
     * @param uuid
     * @return
     */
    WmMailTemplateDto getMailTemplateDetail(String uuid);

    /**
     * 获取模板渲染内容
     */
    String getRenderMailTemplateContent(String uuid);

}
