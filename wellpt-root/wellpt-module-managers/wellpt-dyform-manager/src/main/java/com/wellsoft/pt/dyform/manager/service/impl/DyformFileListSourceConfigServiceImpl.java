package com.wellsoft.pt.dyform.manager.service.impl;

import com.wellsoft.pt.app.entity.AppDefElementI18nEntity;
import com.wellsoft.pt.app.service.AppDefElementI18nService;
import com.wellsoft.pt.dyform.manager.dao.DyformFileListSourceConfigDao;
import com.wellsoft.pt.dyform.manager.dto.DyformFileListSourceConfigDto;
import com.wellsoft.pt.dyform.manager.entity.DyformFileListSourceConfig;
import com.wellsoft.pt.dyform.manager.service.DyformFileListSourceConfigService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

@Service
public class DyformFileListSourceConfigServiceImpl extends AbstractJpaServiceImpl<DyformFileListSourceConfig, DyformFileListSourceConfigDao, String>
        implements DyformFileListSourceConfigService {

    private static final String DYFORM_CACHE_NAME = "Dynamic Table";

    @Autowired
    private AppDefElementI18nService appDefElementI18nService;

    @Override
    @Cacheable(value = DYFORM_CACHE_NAME)
    public List<DyformFileListSourceConfig> getAllBean() {
        List<DyformFileListSourceConfig> list = this.listAllByOrderPage(null, "orderIndex");
        if (CollectionUtils.isNotEmpty(list)) {
            for (DyformFileListSourceConfig config : list) {
                config.setI18ns(appDefElementI18nService.getI18ns(config.getUuid(), null, new BigDecimal(1), "dyformFileListSourceConfig"));
            }
        }
        return list;
    }

    @Override
    @Transactional
    @CacheEvict(value = DYFORM_CACHE_NAME, allEntries = true)
    public void saveAllBean(List<DyformFileListSourceConfigDto> listButtonConfigDtoList) {
        int orderIndex = 0;
        for (DyformFileListSourceConfigDto dyformFileListSourceConfigDto : listButtonConfigDtoList) {
            if (DyformFileListSourceConfigDto.ROW_STATUS_DELETED.equalsIgnoreCase(dyformFileListSourceConfigDto.getRowStatus())) {
                //删除
                if (StringUtils.isNotBlank(dyformFileListSourceConfigDto.getUuid())) {
                    this.delete(dyformFileListSourceConfigDto.getUuid());
                    appDefElementI18nService.deleteAllI18n(null, dyformFileListSourceConfigDto.getUuid(), new BigDecimal(1), "dyformFileListSourceConfig");
                }
            } else {
                //非删除
                dyformFileListSourceConfigDto.setOrderIndex(orderIndex++);
                if (StringUtils.isBlank(dyformFileListSourceConfigDto.getUuid())) {
                    DyformFileListSourceConfig entity = new DyformFileListSourceConfig();
                    BeanUtils.copyProperties(dyformFileListSourceConfigDto, entity);
                    this.save(entity);
                } else {
                    DyformFileListSourceConfig entity = this.getOne(dyformFileListSourceConfigDto.getUuid());
                    BeanUtils.copyProperties(dyformFileListSourceConfigDto, entity);
                    this.update(entity);
                }
                if (CollectionUtils.isNotEmpty(dyformFileListSourceConfigDto.getI18ns())) {
                    appDefElementI18nService.deleteAllI18n(null, dyformFileListSourceConfigDto.getUuid(), new BigDecimal(1), "dyformFileListSourceConfig");
                    for (AppDefElementI18nEntity i : dyformFileListSourceConfigDto.getI18ns()) {
                        i.setDefId(dyformFileListSourceConfigDto.getUuid());
                        i.setApplyTo("dyformFileListSourceConfig");
                        i.setVersion(new BigDecimal(1));
                    }
                    appDefElementI18nService.saveAll(dyformFileListSourceConfigDto.getI18ns());
                }
            }
        }
    }

    @Override
    public List<DyformFileListSourceConfig> getBeansByIds(List<String> ids) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("uuids", ids);
        return this.listByHQL("from DyformFileListSourceConfig where uuid in :uuids order by orderIndex", params);
    }

}
