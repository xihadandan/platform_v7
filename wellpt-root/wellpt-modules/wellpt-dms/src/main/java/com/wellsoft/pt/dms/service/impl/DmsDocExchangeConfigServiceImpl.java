/*
 * @(#)2021-07-13 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.dms.dao.DmsDocExchangeConfigDao;
import com.wellsoft.pt.dms.dto.DmsDocExchangeConfigDto;
import com.wellsoft.pt.dms.dto.DocExcConfig;
import com.wellsoft.pt.dms.entity.DmsDocExchangeConfigEntity;
import com.wellsoft.pt.dms.entity.DmsDocExchangeDyformEntity;
import com.wellsoft.pt.dms.service.DmsDocExchangeConfigService;
import com.wellsoft.pt.dms.service.DmsDocExchangeDyformService;
import com.wellsoft.pt.dms.service.DmsDocExchangeRecordService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Description: 数据库表DMS_DOC_EXCHANGE_CONFIG的service服务接口实现类
 *
 * @author yt
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-07-13.1	yt		2021-07-13		Create
 * </pre>
 * @date 2021-07-13
 */
@Service
public class DmsDocExchangeConfigServiceImpl extends AbstractJpaServiceImpl<DmsDocExchangeConfigEntity, DmsDocExchangeConfigDao, String> implements DmsDocExchangeConfigService {


    @Autowired
    private DmsDocExchangeRecordService dmsDocExchangeRecordService;
    @Autowired
    private DmsDocExchangeDyformService dmsDocExchangeDyformService;

    @Override
    public List<DocExcConfig> queryList() {
        Map<String, Object> values = Maps.newHashMap();
        values.put("systemUnitId", SpringSecurityUtils.getCurrentUserUnitId());
        List<DocExcConfig> docExcConfigList = this.listItemHqlQuery("select uuid as uuid,sequence as sequence,name as name from DmsDocExchangeConfigEntity where systemUnitId=:systemUnitId order by sequence ", DocExcConfig.class, values);
        return docExcConfigList;
    }

    @Override
    @Transactional
    public void del(String uuid) {
        if (StringUtils.isBlank(uuid)) {
            return;
        }
        Map<String, Object> values = Maps.newHashMap();
        values.put("configUuid", uuid);
        long count = dmsDocExchangeRecordService.getDao().countByHQL("select count(uuid) from DmsDocExchangeRecordEntity where configUuid=:configUuid ", values);
        if (count > 0) {
            throw new RuntimeException("该交换业务正在使用，已产生交换数据，不可删除。");
        }
        DmsDocExchangeConfigEntity configEntity = this.getOne(uuid);
        if (StringUtils.isNotBlank(configEntity.getDmsDocExchangeDyformUuid())) {
            DmsDocExchangeDyformEntity dyformEntity = dmsDocExchangeDyformService.getOne(configEntity.getDmsDocExchangeDyformUuid());
            dmsDocExchangeDyformService.delete(dyformEntity);
        }
        values.put("systemUnitId", SpringSecurityUtils.getCurrentUserUnitId());
        values.put("sequence", configEntity.getSequence());
        this.getDao().updateByHQL("update DmsDocExchangeConfigEntity set sequence = sequence - 1 where systemUnitId=:systemUnitId and sequence > :sequence ", values);
        this.delete(configEntity);
    }

    @Override
    @Transactional
    public String saveOrUpdate(DmsDocExchangeConfigDto configDto) {
        DmsDocExchangeConfigEntity configEntity = new DmsDocExchangeConfigEntity();
        if (StringUtils.isNotBlank(configDto.getUuid())) {
            configEntity = this.getOne(configDto.getUuid());
        } else {
            Map<String, Object> values = Maps.newHashMap();
            values.put("systemUnitId", SpringSecurityUtils.getCurrentUserUnitId());
            long count = this.getDao().countByHQL("select count(uuid) from DmsDocExchangeConfigEntity where systemUnitId=:systemUnitId ", values);
            configEntity.setSequence(Integer.valueOf(String.valueOf(count + 1)));
        }
        if (StringUtils.isBlank(configDto.getDmsDocExchangeDyformUuid())) {
            configDto.setDmsDocExchangeDyformUuid(configEntity.getDmsDocExchangeDyformUuid());
        }
        BeanUtils.copyProperties(configDto, configEntity);
        this.save(configEntity);
        return configEntity.getUuid();
    }


    @Override
    @Transactional
    public void sequence(List<DocExcConfig> configList) {
        if (configList == null) {
            return;
        }
        for (DocExcConfig docExcConfig : configList) {
            DmsDocExchangeConfigEntity configEntity = this.getOne(docExcConfig.getUuid());
            configEntity.setSequence(docExcConfig.getSequence());
            this.update(configEntity);
        }
    }
}
