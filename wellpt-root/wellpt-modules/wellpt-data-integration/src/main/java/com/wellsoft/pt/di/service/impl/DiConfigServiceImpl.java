/*
 * @(#)2019-07-23 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.di.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.di.dao.DiConfigDao;
import com.wellsoft.pt.di.dto.DiConfigDto;
import com.wellsoft.pt.di.dto.DiDataConsumerEndpointDto;
import com.wellsoft.pt.di.dto.DiDataProcessorDto;
import com.wellsoft.pt.di.dto.DiDataProducerEndpointDto;
import com.wellsoft.pt.di.entity.DiConfigEntity;
import com.wellsoft.pt.di.entity.DiDataConsumerEndpointEntity;
import com.wellsoft.pt.di.entity.DiDataProcessorEntity;
import com.wellsoft.pt.di.entity.DiDataProducerEndpointEntity;
import com.wellsoft.pt.di.service.DiConfigService;
import com.wellsoft.pt.di.service.DiDataConsumerEndpointService;
import com.wellsoft.pt.di.service.DiDataProcessorService;
import com.wellsoft.pt.di.service.DiDataProducerEndpointService;
import com.wellsoft.pt.di.util.CamelContextUtils;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description: 数据库表DI_CONFIG的service服务接口实现类
 *
 * @author chenq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019-07-23.1	chenq		2019-07-23		Create
 * </pre>
 * @date 2019-07-23
 */
@Service
public class DiConfigServiceImpl extends
        AbstractJpaServiceImpl<DiConfigEntity, DiConfigDao, String> implements DiConfigService {


    @Autowired
    DiDataConsumerEndpointService consumerEndpointService;
    @Autowired
    DiDataProducerEndpointService producerEndpointService;
    @Autowired
    DiDataProcessorService processorService;


    @Override
    @Transactional
    public void saveConfig(DiConfigDto diConfigDto) {
        DiConfigEntity configEntity = new DiConfigEntity();
        boolean isNew = StringUtils.isBlank(diConfigDto.getUuid());
        if (!isNew) {
            configEntity = getOne(diConfigDto.getUuid());
        }

        BeanUtils.copyProperties(diConfigDto, configEntity,
                !isNew ? IdEntity.BASE_FIELDS : new String[0]);

        save(configEntity);//保存基本配置


        List<DiDataProcessorEntity> saveProcessors = Lists.newArrayList();
        List<DiDataProcessorEntity> deleteProcessors = Lists.newArrayList();
        List<String> existProcessorUuids = Lists.newArrayList();
        for (DiDataProcessorDto processorDto : diConfigDto.getProcessorDtos()) {
            DiDataProcessorEntity processorEntity = new DiDataProcessorEntity();
            if (StringUtils.isNotBlank(processorDto.getUuid())) {
                processorEntity = processorService.getOne(
                        processorDto.getUuid());
                existProcessorUuids.add(processorDto.getUuid());
            }
            BeanUtils.copyProperties(processorDto, processorEntity,
                    StringUtils.isNotBlank(
                            processorDto.getUuid()) ? IdEntity.BASE_FIELDS : new String[0]);
            processorEntity.setDiConfigUuid(configEntity.getUuid());
            saveProcessors.add(processorEntity);
        }
        if (!isNew) {
            List<DiDataProcessorEntity> exists = processorService.listByDiConfigUuidOrderBySeqAsc(
                    diConfigDto.getUuid());
            for (DiDataProcessorEntity e : exists) {
                if (!existProcessorUuids.contains(e.getUuid())) {
                    deleteProcessors.add(e);
                }
            }
        }

        processorService.saveAll(saveProcessors);
        processorService.deleteByEntities(deleteProcessors);

        if (diConfigDto.getConsumerEndpoint() != null) {
            DiDataConsumerEndpointEntity consumerEndpointEntity = new DiDataConsumerEndpointEntity();
            DiDataConsumerEndpointDto endpointDto = diConfigDto.getConsumerEndpoint();
            if (StringUtils.isNotBlank(endpointDto.getUuid())) {
                consumerEndpointEntity = consumerEndpointService.getOne(endpointDto.getUuid());
            }
            BeanUtils.copyProperties(endpointDto, consumerEndpointEntity, StringUtils.isNotBlank(
                    endpointDto.getUuid()) ? IdEntity.BASE_FIELDS : new String[0]);
            consumerEndpointEntity.setDiConfigUuid(configEntity.getUuid());
            consumerEndpointService.save(consumerEndpointEntity);
        }

        if (diConfigDto.getProducerEndpoint() != null) {
            DiDataProducerEndpointEntity producerEndpointEntity = new DiDataProducerEndpointEntity();
            DiDataProducerEndpointDto endpointDto = diConfigDto.getProducerEndpoint();
            if (StringUtils.isNotBlank(endpointDto.getUuid())) {
                producerEndpointEntity = producerEndpointService.getOne(endpointDto.getUuid());
            }
            BeanUtils.copyProperties(endpointDto, producerEndpointEntity, StringUtils.isNotBlank(
                    endpointDto.getUuid()) ? IdEntity.BASE_FIELDS : new String[0]);
            producerEndpointEntity.setDiConfigUuid(configEntity.getUuid());
            producerEndpointService.save(producerEndpointEntity);
        }

        diConfigDto.setUuid(configEntity.getUuid());

    }

    @Override
    public List<DiConfigEntity> listByJobUuid(String jobUuid) {
        return this.dao.listByFieldEqValue("jobUuid", jobUuid);
    }

    @Override
    public DiConfigDto getDetails(String uuid) {
        DiConfigEntity configEntity = this.getOne(uuid);
        DiConfigDto dto = new DiConfigDto();
        BeanUtils.copyProperties(configEntity, dto);

        DiDataConsumerEndpointDto consumerEndpointDto = new DiDataConsumerEndpointDto();
        DiDataConsumerEndpointEntity consumerEndpointEntity = consumerEndpointService.getByDiConfigUuid(
                uuid);
        BeanUtils.copyProperties(consumerEndpointEntity, consumerEndpointDto);
        dto.setConsumerEndpoint(consumerEndpointDto);

        DiDataProducerEndpointEntity producerEndpointEntity = producerEndpointService.getByDiConfigUuid(
                uuid);
        DiDataProducerEndpointDto producerEndpointDto = new DiDataProducerEndpointDto();
        BeanUtils.copyProperties(producerEndpointEntity, producerEndpointDto);
        dto.setProducerEndpoint(producerEndpointDto);

        List<DiDataProcessorEntity> processorEntityList = processorService.listByDiConfigUuidOrderBySeqAsc(
                uuid);
        for (DiDataProcessorEntity entity : processorEntityList) {
            DiDataProcessorDto processorDto = new DiDataProcessorDto();
            BeanUtils.copyProperties(entity, processorDto);
            dto.getProcessorDtos().add(processorDto);
        }
        return dto;
    }

    @Override
    @Transactional
    public void deleteDiConfigs(List<String> uuids) {
        for (String uuid : uuids) {
            CamelContextUtils.removeRoute(uuid);
            this.dao.delete(uuid);
            consumerEndpointService.deleteByDiConfUuid(uuid);
            producerEndpointService.deleteByDiConfUuid(uuid);
            processorService.deleteByDiConfUuid(uuid);
        }
    }

    @Override
    public DiConfigEntity getById(String id) {
        List<DiConfigEntity> entities = this.dao.listByFieldEqValue("id", id);
        return CollectionUtils.isNotEmpty(entities) ? entities.get(0) : null;
    }

}
