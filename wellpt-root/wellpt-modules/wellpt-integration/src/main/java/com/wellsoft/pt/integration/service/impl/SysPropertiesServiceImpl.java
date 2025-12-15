/*
 * @(#)2018年4月17日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.service.impl;

import com.wellsoft.pt.integration.dao.SysPropertiesDao;
import com.wellsoft.pt.integration.entity.SysProperties;
import com.wellsoft.pt.integration.service.SysPropertiesService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月17日.1	chenqiong		2018年4月17日		Create
 * </pre>
 * @date 2018年4月17日
 */
@Service
public class SysPropertiesServiceImpl extends AbstractJpaServiceImpl<SysProperties, SysPropertiesDao, String> implements
        SysPropertiesService {
    @Override
    public List<SysProperties> getAllSysProperties(String moduleId) {
        SysProperties example = new SysProperties();
        if (StringUtils.isNotBlank(moduleId)) {
            example.setModuleId(moduleId);
        }
        return findByExample(example);
        //		String hql = "from SysProperties s ";
        //		if (!StringUtils.isBlank(moduleId)) {
        //			hql += "where s.moduleId=:moduleId";
        //		}
        //		Map<String, Object> valueMap = new HashMap<String, Object>();
        //		valueMap.put("moduleId", moduleId);
        //		return listByHQL(hql, valueMap);
    }

    @Override
    @Transactional()
    public Boolean saveSysProperties(List<SysProperties> sysPropertiesList) {
        try {
            boolean flag = false;
            for (int i = 0; i < sysPropertiesList.size(); i++) {
                SysProperties s = sysPropertiesList.get(i);
                String proEnName = s.getProEnName();
                List<SysProperties> sysProperties = this.dao.listByFieldEqValue("proEnName", proEnName);
                SysProperties entity = CollectionUtils.isNotEmpty(sysProperties) ? sysProperties.get(0) : null;
                // 没有的数据保存，有的数据update
                // 判断是否有脏数据插入数据库中
                if (entity == null) {
                    // save
                    String proEn = s.getProEnName();
                    if (StringUtils.isEmpty(proEn)) {
                        // 跳过脏数据不保存
                        flag = true;
                        continue;
                    }
                    entity = new SysProperties();
                    entity.setProCnName(s.getProCnName());
                    entity.setProEnName(s.getProEnName());
                    entity.setProValue(s.getProValue());
                    entity.setModuleId(s.getModuleId());
                    this.save(entity);
                } else {
                    // update
                    // 判断是否有值变化
                    String proValue = s.getProValue();
                    String dProValue = entity.getProValue();
                    if ((proValue == null && dProValue != null) || (proValue != null && !proValue.equals(dProValue))) {
                        if (!(StringUtils.isEmpty(proValue) && StringUtils.isEmpty(dProValue))) {
                            entity.setProValue(proValue);
                            entity.setModifyTime(new Date());
                            this.update(entity);
                        }
                    }
                }
                if (flag) {
                    logger.error("保存的数据中有pro_en_name 为空，该数据保存");
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            logger.info(e.getMessage());
            return false;
        }
    }

    @Override
    public List<SysProperties> findByExample(SysProperties example) {
        return this.dao.listByEntity(example);
    }
}
