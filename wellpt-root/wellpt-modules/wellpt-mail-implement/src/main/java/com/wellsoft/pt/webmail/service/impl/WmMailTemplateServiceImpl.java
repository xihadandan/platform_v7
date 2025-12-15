/*
 * @(#)2018年3月12日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.webmail.bean.WmMailTemplateDto;
import com.wellsoft.pt.webmail.dao.WmMailTemplateDao;
import com.wellsoft.pt.webmail.entity.WmMailTemplateEntity;
import com.wellsoft.pt.webmail.service.WmMailTemplateService;
import com.wellsoft.pt.webmail.support.MailTemplateExplainer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.rowset.serial.SerialClob;
import java.util.List;
import java.util.Map;

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
@Service
public class WmMailTemplateServiceImpl extends AbstractJpaServiceImpl<WmMailTemplateEntity, WmMailTemplateDao, String>
        implements WmMailTemplateService {

    @Override
    @Transactional
    public void updateMailTemplateDefaultStatus(Boolean isDefault, String uuid) {
        List<WmMailTemplateEntity> templateEntities = this.dao.listByFieldEqValue("userId", SpringSecurityUtils.getCurrentUserId());
        if (CollectionUtils.isNotEmpty(templateEntities)) {
            for (WmMailTemplateEntity entity : templateEntities) {
                entity.setIsDefault(false);
            }
            this.dao.saveAll(templateEntities);
        }
        if (BooleanUtils.isTrue(isDefault) && org.apache.commons.lang.StringUtils.isNotBlank(uuid)) {
            WmMailTemplateEntity entity = this.dao.getOne(uuid);
            entity.setIsDefault(true);
            this.dao.save(entity);
        }
    }

    @Override
    @Transactional
    public void updateMailTemplate(WmMailTemplateDto dto) {
        if (dto != null) {
            if (StringUtils.isBlank(dto.getUuid())) {
                throw new HibernateException("uuid无值");
            }
            WmMailTemplateEntity entity = getOne(dto.getUuid());
            if (StringUtils.isNotBlank(dto.getTemplateName())) {
                entity.setTemplateName(dto.getTemplateName());
            }
            if (StringUtils.isNotBlank(dto.getTemplateContent())) {
                try {
                    entity.setTemplateContent(new SerialClob(dto.getTemplateContent().toCharArray()));
                } catch (Exception e) {
                    logger.error("更新写信模板内容异常，", e);
                    throw new HibernateException("tempalteContent设置异常");
                }
            }
            if (dto.getIsDefault() != null) {
                entity.setIsDefault(dto.getIsDefault());
            }
            save(entity);
        }
    }

    @Override
    public WmMailTemplateDto getMailTemplateDetail(String uuid) {
        WmMailTemplateEntity entity = getOne(uuid);
        WmMailTemplateDto dto = new WmMailTemplateDto();
        BeanUtils.copyProperties(entity, dto, "templateContent");
        try {
            dto.setTemplateContent(IOUtils.toString(entity.getTemplateContent().getCharacterStream()));
        } catch (Exception e) {
            logger.error("读取写信模板内容异常，", e);
        }
        return dto;
    }

    @Override
    @Transactional
    public void addMailTemplate(WmMailTemplateDto dto) {
        WmMailTemplateEntity entity = new WmMailTemplateEntity();
        BeanUtils.copyProperties(dto, entity, "templateContent");
        entity.setUserId(SpringSecurityUtils.getCurrentUserId());
        entity.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        try {
            entity.setTemplateContent(new SerialClob(dto.getTemplateContent().toCharArray()));
        } catch (Exception e) {
            logger.error("更新写信模板内容异常，", e);
            throw new HibernateException("tempalteContent设置异常");
        }
        save(entity);
    }

    @Override
    public String renderMailTemplateContent(String uuid) {
        WmMailTemplateDto dto = this.getMailTemplateDetail(uuid);
        String templateName = String.format("%s_mail_template_key", uuid);
        return MailTemplateExplainer.renderMailTemplateContent(templateName, dto.getTemplateContent());
    }

    @Override
    public List<WmMailTemplateDto> listCurrentUserMailTemplates() {
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", SpringSecurityUtils.getCurrentUserId());
        List<WmMailTemplateEntity> entities = this.listByHQL(
                "from WmMailTemplateEntity where userId=:userId order by isDefault desc,modifyTime desc", params);
        if (CollectionUtils.isNotEmpty(entities)) {
            List<WmMailTemplateDto> dtos = Lists.newArrayList();
            for (WmMailTemplateEntity entity : entities) {
                WmMailTemplateDto dto = new WmMailTemplateDto();
                BeanUtils.copyProperties(entity, dto);
                dtos.add(dto);
            }
            return dtos;
        }
        return null;
    }

    @Override
    public WmMailTemplateDto getCurrentUserDefaultTemplate() {
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", SpringSecurityUtils.getCurrentUserId());
        List<WmMailTemplateEntity> entities = this.listByHQL(
                "from WmMailTemplateEntity where userId=:userId and isDefault=true", params);
        if (CollectionUtils.isNotEmpty(entities)) {
            WmMailTemplateEntity entity = entities.get(0);
            List<WmMailTemplateDto> dtos = Lists.newArrayList();
            WmMailTemplateDto dto = new WmMailTemplateDto();
            BeanUtils.copyProperties(entity, dto, "templateContent");
            try {
                dto.setTemplateContent(IOUtils.toString(entity.getTemplateContent().getCharacterStream()));
                dto.setContentRendered(MailTemplateExplainer.renderMailTemplateContent(
                        String.format("%s_mail_template_key", entity.getUuid()), dto.getTemplateContent()));
            } catch (Exception e) {
                logger.error("获取当前用户默认的写信模板异常：", e);
            }
            return dto;
        }
        return null;
    }

}
