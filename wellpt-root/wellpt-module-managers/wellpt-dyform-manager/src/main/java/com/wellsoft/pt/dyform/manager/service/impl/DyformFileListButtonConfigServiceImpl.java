package com.wellsoft.pt.dyform.manager.service.impl;

import com.wellsoft.pt.app.entity.AppDefElementI18nEntity;
import com.wellsoft.pt.app.service.AppDefElementI18nService;
import com.wellsoft.pt.dyform.manager.dao.DyformFileListButtonConfigDao;
import com.wellsoft.pt.dyform.manager.dto.DyformFileListButtonConfigDto;
import com.wellsoft.pt.dyform.manager.entity.DyformFileListButtonConfig;
import com.wellsoft.pt.dyform.manager.service.DyformFileListButtonConfigService;
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
public class DyformFileListButtonConfigServiceImpl extends AbstractJpaServiceImpl<DyformFileListButtonConfig, DyformFileListButtonConfigDao, String>
        implements DyformFileListButtonConfigService {

    private static final String DYFORM_CACHE_NAME = "Dynamic Table";

    @Autowired
    private AppDefElementI18nService appDefElementI18nService;

    @Override
    @Cacheable(value = DYFORM_CACHE_NAME)
    public List<DyformFileListButtonConfig> getAllBean() {
        List<DyformFileListButtonConfig> list = this.listAllByOrderPage(null, "orderIndex");
        if (CollectionUtils.isNotEmpty(list)) {
            for (DyformFileListButtonConfig config : list) {
                config.setI18ns(appDefElementI18nService.getI18ns(config.getUuid(), null, new BigDecimal(1), "dyformFileListButtonConfig"));
            }
        }
        return list;
    }

    @Override
    @Transactional
    @CacheEvict(value = DYFORM_CACHE_NAME, allEntries = true)
    public void saveAllBean(List<DyformFileListButtonConfigDto> listButtonConfigDtoList) {
        int orderIndex = 0;
        for (DyformFileListButtonConfigDto dyformFileListButtonConfigDto : listButtonConfigDtoList) {
            if (DyformFileListButtonConfigDto.ROW_STATUS_DELETED.equalsIgnoreCase(dyformFileListButtonConfigDto.getRowStatus())) {
                //删除
                if (StringUtils.isNotBlank(dyformFileListButtonConfigDto.getUuid())) {
                    this.delete(dyformFileListButtonConfigDto.getUuid());
                    appDefElementI18nService.deleteAllI18n(null, dyformFileListButtonConfigDto.getUuid(), new BigDecimal(1), "dyformFileListButtonConfig");
                }
            } else {
                //非删除
                dyformFileListButtonConfigDto.setOrderIndex(orderIndex++);
                if (StringUtils.isBlank(dyformFileListButtonConfigDto.getUuid())) {
                    DyformFileListButtonConfig entity = new DyformFileListButtonConfig();
                    BeanUtils.copyProperties(dyformFileListButtonConfigDto, entity);
                    this.save(entity);
                } else {
                    DyformFileListButtonConfig entity = this.getOne(dyformFileListButtonConfigDto.getUuid());
                    BeanUtils.copyProperties(dyformFileListButtonConfigDto, entity);
                    this.update(entity);
                }
                if (CollectionUtils.isNotEmpty(dyformFileListButtonConfigDto.getI18ns())) {
                    appDefElementI18nService.deleteAllI18n(null, dyformFileListButtonConfigDto.getUuid(), new BigDecimal(1), "dyformFileListSourceConfig");
                    for (AppDefElementI18nEntity i : dyformFileListButtonConfigDto.getI18ns()) {
                        i.setDefId(dyformFileListButtonConfigDto.getUuid());
                        i.setApplyTo("dyformFileListButtonConfig");
                        i.setVersion(new BigDecimal(1));
                    }
                    appDefElementI18nService.saveAll(dyformFileListButtonConfigDto.getI18ns());
                }
            }
        }
    }

    @Override
    public List<DyformFileListButtonConfig> getBeansByIds(List<String> ids) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("uuids", ids);
        return this.listByHQL("from DyformFileListButtonConfig where uuid in :uuids order by orderIndex", params);
    }

}
