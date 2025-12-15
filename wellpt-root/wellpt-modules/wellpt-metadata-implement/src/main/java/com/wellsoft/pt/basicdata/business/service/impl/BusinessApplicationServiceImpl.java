/*
 * @(#)2019-02-21 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.business.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.pt.basicdata.business.dao.BusinessApplicationConfigDao;
import com.wellsoft.pt.basicdata.business.dao.BusinessApplicationDao;
import com.wellsoft.pt.basicdata.business.dto.BusinessApplicationConfigDto;
import com.wellsoft.pt.basicdata.business.dto.BusinessApplicationDto;
import com.wellsoft.pt.basicdata.business.entity.BusinessApplicationConfigEntity;
import com.wellsoft.pt.basicdata.business.entity.BusinessApplicationEntity;
import com.wellsoft.pt.basicdata.business.service.BusinessApplicationService;
import com.wellsoft.pt.basicdata.datadict.dto.CdDataDictionaryItemDto;
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 数据库表BUSINESS_APPLICATION的service服务接口实现类
 *
 * @author leo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019-02-21.1	leo		2019-02-21		Create
 * </pre>
 * @date 2019-02-21
 */
@Service
public class BusinessApplicationServiceImpl extends
        AbstractJpaServiceImpl<BusinessApplicationEntity, BusinessApplicationDao, String> implements
        BusinessApplicationService {

    public final static String BASIC_DATA_FORM_APP = "BASIC_DATA_FORM_APP";
    @Autowired
    private BusinessApplicationConfigDao businessApplicationConfigDao;
    @Autowired
    private BusinessApplicationDao businessApplicationDao;
    @Autowired
    private BasicDataApiFacade basicDataApiFacade;

    public Select2QueryData querySelectDataForFormApp(Select2QueryInfo select2QueryInfo) {

        String queryValue = select2QueryInfo.getSearchValue();

        List<CdDataDictionaryItemDto> first = basicDataApiFacade.getDataDictionariesByType(BASIC_DATA_FORM_APP);
        List<Select2DataBean> beans = new ArrayList<Select2DataBean>();
        for (CdDataDictionaryItemDto entity : first) {
            boolean isShow = queryValue == null || "".equals(queryValue) ? true : false;
            if (!isShow && entity.getLabel().indexOf(queryValue) != -1) {
                isShow = true;
            }
            List<CdDataDictionaryItemDto> childrens = basicDataApiFacade.getDataDictionariesByType(entity.getValue());
            if (!isShow) {
                for (CdDataDictionaryItemDto children : childrens) {
                    if (children.getLabel().indexOf(queryValue) != -1) {
                        isShow = true;
                        break;
                    }
                }
            }

            if (isShow) {
                beans.add(new Select2DataBean("-1", entity.getLabel()));

                for (CdDataDictionaryItemDto children : childrens) {
                    if (queryValue == null || "".equals(queryValue) || children.getLabel().indexOf(queryValue) != -1) {
                        JSONObject json = new JSONObject();
                        json.put("dictType", entity.getValue());
                        json.put("dictCode", children.getValue());
                        beans.add(new Select2DataBean(json.toString(), "--" + children.getLabel()));
                    }
                }
            }
        }
        return new Select2QueryData(beans);

    }

    public Select2QueryData loadSelectDataForFormApp(Select2QueryInfo select2QueryInfo) {
        String[] uuids = select2QueryInfo.getIds();
        if (uuids.length == 0 || "-1".equals(uuids[0]) || StringUtils.isBlank(uuids[0])) {
            return new Select2QueryData();
        }
        try {
            JSONObject json = JSONObject.fromObject(uuids[0]);
            List<CdDataDictionaryItemDto> dataDictionarys = basicDataApiFacade.getDataDictionaries(json.getString("dictType"),
                    json.getString("dictCode"));
            for (CdDataDictionaryItemDto dataDictionary : dataDictionarys) {
                List<Select2DataBean> beans = new ArrayList<Select2DataBean>();
                beans.add(new Select2DataBean(uuids[0], "--" + dataDictionary.getLabel()));
                return new Select2QueryData(beans);
            }
        } catch (Exception e) {
            logger.error("异常： uuids值为：" + uuids);
        }

        return new Select2QueryData();
    }

    public List<BusinessApplicationConfigDto> findBusinessApplicationConfig(String uuid) {
        List<BusinessApplicationConfigEntity> list = businessApplicationConfigDao.listByFieldEqValue(
                "businessApplicationUuid", uuid);
        List<BusinessApplicationConfigDto> dtos = new ArrayList<BusinessApplicationConfigDto>();
        for (BusinessApplicationConfigEntity po : list) {
            BusinessApplicationConfigDto dto = new BusinessApplicationConfigDto();
            BeanUtils.copyProperties(po, dto);

            JSONObject json = new JSONObject();
            json.put("dictType", po.getDictType());
            json.put("dictCode", po.getDictCode());
            dto.setDict(json.toString());
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    @Transactional
    public void save(BusinessApplicationDto dto) {
        if (StringUtils.isBlank(dto.getUuid())) {
            BusinessApplicationEntity po = new BusinessApplicationEntity();
            po.setBusinessCategoryUuid(dto.getBusinessCategoryUuid());
            dao.save(po);
            for (BusinessApplicationConfigEntity config : dto.getConfigs()) {
                config.setBusinessApplicationUuid(po.getUuid());
                businessApplicationConfigDao.save(config);
            }
        } else {
            BusinessApplicationEntity po = dao.getOne(dto.getUuid());
            po.setBusinessCategoryUuid(dto.getBusinessCategoryUuid());
            dao.update(po);
            List<String> uuids = new ArrayList<String>();
            for (BusinessApplicationConfigEntity config : dto.getConfigs()) {
                if (StringUtils.isBlank(config.getUuid())) {
                    config.setBusinessApplicationUuid(po.getUuid());
                    businessApplicationConfigDao.save(config);
                } else {
                    BusinessApplicationConfigEntity dbConfig = businessApplicationConfigDao.getOne(config.getUuid());
                    dbConfig.setRuleUuid(config.getRuleUuid());
                    dbConfig.setFormUuid(config.getFormUuid());
                    dbConfig.setDictType(config.getDictType());
                    dbConfig.setDictCode(config.getDictCode());
                    config.setBusinessApplicationUuid(po.getUuid());
                    businessApplicationConfigDao.update(dbConfig);
                }
                uuids.add(config.getUuid());

            }

            List<BusinessApplicationConfigEntity> configs = businessApplicationConfigDao.listByFieldEqValue(
                    "businessApplicationUuid", dto.getUuid());
            for (BusinessApplicationConfigEntity config : configs) {
                if (!uuids.contains(config.getUuid())) {
                    businessApplicationConfigDao.delete(config);
                }
            }
        }

    }

    @Override
    @Transactional
    public void deleteByIds(String[] uuids) {
        for (String uuid : uuids) {
            List<BusinessApplicationConfigEntity> list = businessApplicationConfigDao.listByFieldEqValue(
                    "businessApplicationUuid", uuid);
            for (BusinessApplicationConfigEntity config : list) {
                businessApplicationConfigDao.delete(config);
            }
            dao.delete(uuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.business.service.BusinessApplicationService#findBusinessApplicationConfigByCategoryUuid(java.lang.String)
     */
    @Override
    public List<BusinessApplicationConfigDto> findBusinessApplicationConfigByCategoryUuid(String categoryUuid) {
        List<BusinessApplicationEntity> applicationEntities = businessApplicationDao.listByFieldEqValue(
                "businessCategoryUuid", categoryUuid);
        List<BusinessApplicationConfigDto> businessApplicationConfigDtos = Lists.newArrayList();
        for (BusinessApplicationEntity businessApplicationEntity : applicationEntities) {
            businessApplicationConfigDtos.addAll(findBusinessApplicationConfig(businessApplicationEntity.getUuid()));
        }
        return businessApplicationConfigDtos;
    }

}
