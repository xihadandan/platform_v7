/*
 * @(#)2018年3月12日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.facade.service.impl;

import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.webmail.bean.WmMailTemplateDto;
import com.wellsoft.pt.webmail.facade.service.WmMailTemplateFacadeService;
import com.wellsoft.pt.webmail.service.WmMailTemplateService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
@Service
public class WmMailTemplateFacadeServiceImpl extends AbstractApiFacade implements WmMailTemplateFacadeService {

    @Resource
    WmMailTemplateService wmMailTemplateService;

    @Override
    public void deleteMailTemplatesByUuids(List<String> uuids) {
        wmMailTemplateService.deleteByUuids(uuids);
    }

    @Override
    public void updateMailTemplateDefaultStatus(Boolean isDefault, String uuid) {
        wmMailTemplateService.updateMailTemplateDefaultStatus(isDefault, uuid);
    }

    @Override
    public void updateMailTemplate(WmMailTemplateDto dto) {
        wmMailTemplateService.updateMailTemplate(dto);
    }

    @Override
    public void addMailTemplate(WmMailTemplateDto dto) {
        wmMailTemplateService.addMailTemplate(dto);
    }

    @Override
    public WmMailTemplateDto getMailTemplateDetail(String uuid) {
        return wmMailTemplateService.getMailTemplateDetail(uuid);
    }

    @Override
    public String getRenderMailTemplateContent(String uuid) {
        return wmMailTemplateService.renderMailTemplateContent(uuid);
    }

    @Override
    public List<WmMailTemplateDto> listCurrentUserMailTemplates() {
        return wmMailTemplateService.listCurrentUserMailTemplates();
    }

    @Override
    public WmMailTemplateDto getCurrentUserDefaultTemplate() {
        return wmMailTemplateService.getCurrentUserDefaultTemplate();
    }

}
