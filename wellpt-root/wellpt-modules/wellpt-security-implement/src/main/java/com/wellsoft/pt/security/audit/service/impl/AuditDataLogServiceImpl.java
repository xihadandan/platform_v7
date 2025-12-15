package com.wellsoft.pt.security.audit.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.util.web.ServletUtils;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.audit.dao.AuditDataItemLogDao;
import com.wellsoft.pt.security.audit.dao.AuditDataLogDao;
import com.wellsoft.pt.security.audit.dto.AuditDataItemLogDto;
import com.wellsoft.pt.security.audit.dto.AuditDataLogDto;
import com.wellsoft.pt.security.audit.entity.AuditDataItemLogEntity;
import com.wellsoft.pt.security.audit.entity.AuditDataLogEntity;
import com.wellsoft.pt.security.audit.service.AuditDataLogService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年12月26日   chenq	 Create
 * </pre>
 */
@Service
public class AuditDataLogServiceImpl extends AbstractJpaServiceImpl<AuditDataLogEntity, AuditDataLogDao, Long> implements AuditDataLogService {
    @Autowired
    AuditDataItemLogDao dataItemLogDao;

    @Override
    @Transactional
    public void saveAuditDataLog(AuditDataLogDto logDto) {
        try {
            AuditDataLogEntity logEntity = new AuditDataLogEntity();
            BeanUtils.copyProperties(logDto, logEntity);
            if (StringUtils.isBlank(logEntity.getModifierName())) {
                logEntity.setModifierName(SpringSecurityUtils.getCurrentUserName());
            }
            if (StringUtils.isBlank(logDto.getIp())) {
                try {
                    ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
                    HttpServletRequest request = requestAttributes.getRequest();
                    logEntity.setIp(ServletUtils.getRemoteAddr(request));
                } catch (Exception e) {
                    logger.error("设置审计日志IP异常", e);
                }
            }
            save(logEntity);
            if (CollectionUtils.isNotEmpty(logDto.getDataItems())) {
                List<AuditDataItemLogEntity> items = Lists.newArrayList();
                for (AuditDataItemLogDto dataItemLogDto : logDto.getDataItems()) {
                    AuditDataItemLogEntity dataItemLogEntity = new AuditDataItemLogEntity();
                    BeanUtils.copyProperties(dataItemLogDto, dataItemLogEntity);
                    dataItemLogEntity.setAuditUuid(logEntity.getUuid());
                    items.add(dataItemLogEntity);
                }
                dataItemLogDao.saveAll(items);
            }

            if (CollectionUtils.isNotEmpty(logDto.getChildren())) {
                for (AuditDataLogDto child : logDto.getChildren()) {
                    child.setParentUuid(logEntity.getUuid());
                    if (StringUtils.isBlank(child.getCategory())) {
                        child.setCategory(logEntity.getCategory());
                    }
                    saveAuditDataLog(child);
                }
            }
        } catch (Exception e) {
            logger.error("记录数据审计日志异常:{}", e);
        }
    }
}
