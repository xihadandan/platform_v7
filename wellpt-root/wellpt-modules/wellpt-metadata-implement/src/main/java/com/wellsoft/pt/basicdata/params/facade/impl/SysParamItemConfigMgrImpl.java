/*
 * @(#)2015-07-20 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.params.facade.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryApi;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.service.CommonValidateService;
import com.wellsoft.pt.basicdata.params.bean.SysParamItemBean;
import com.wellsoft.pt.basicdata.params.entity.SysParamItem;
import com.wellsoft.pt.basicdata.params.facade.ModuleConfig;
import com.wellsoft.pt.basicdata.params.facade.SysParamItemConfigMgr;
import com.wellsoft.pt.basicdata.params.service.SysParamItemService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author Lmw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-07-20.1	Lmw		2015-07-20		Create
 * </pre>
 * @date 2015-07-20
 */
@Service
@Transactional
public class SysParamItemConfigMgrImpl extends BaseServiceImpl implements SysParamItemConfigMgr, Select2QueryApi {

    @Autowired
    private SysParamItemService sysParamItemService;

    @Autowired
    private CommonValidateService commonValidateService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.params.facade.SysParamItemConfigMgr#getBean(java.lang.String)
     */
    @Override
    public SysParamItemBean getBean(String uuid) {
        SysParamItem entity = sysParamItemService.get(uuid);
        SysParamItemBean bean = new SysParamItemBean();
        BeanUtils.copyProperties(entity, bean);
        return bean;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.params.facade.SysParamItemConfigMgr#saveBean(com.wellsoft.pt.params.bean.SysParamItemBean)
     */
    @Override
    public void saveBean(SysParamItemBean bean) {
        SysParamItem entity = new SysParamItem();
        if (StringUtils.isNotBlank(bean.getUuid())) {
            entity = sysParamItemService.get(bean.getUuid());
            // 类型非空唯一性判断
            if (StringUtils.isNotBlank(bean.getKey())
                    && !commonValidateService.checkUnique(bean.getUuid(), "sysParamItem", "key", bean.getKey())) {
                throw new RuntimeException("已经存在鍵为[" + bean.getKey() + "]的配置项!");
            }
        } else if (StringUtils.isNotBlank(bean.getKey())
                && commonValidateService.checkExists("sysParamItem", "key", bean.getKey())) {
            // 类型非空唯一性判断
            throw new RuntimeException("已经存在鍵为[" + bean.getKey() + "]的配置项!");
        }

        BeanUtils.copyProperties(bean, entity);
        sysParamItemService.save(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.params.facade.SysParamItemConfigMgr#remove(java.lang.String)
     */
    @Override
    public void remove(String uuid) {
        sysParamItemService.remove(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.params.facade.SysParamItemConfigMgr#removeAll(java.util.Collection)
     */
    @Override
    public void removeAll(Collection<String> uuids) {
        sysParamItemService.removeAllByPk(uuids);
    }

    @Override
    public List<SysParamItem> listByKey(String key) {
        SysParamItem example = new SysParamItem();
        example.setKey(key);
        return sysParamItemService.findByExample(example);
    }

    @Override
    public String getValueByKey(String key) {
        SysParamItem example = new SysParamItem();
        example.setKey(key);
        List<SysParamItem> list = sysParamItemService.findByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0).getValue();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, String> getModuleConfig(String module) {
        return ModuleConfig.getModuleConfig(module);
    }

    @Override
    public boolean saveModuleConfig(String module, Map<String, String> params) {
        return ModuleConfig.saveModuleConfig(module, params);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SysParamItem> query(String keyword, PagingInfo pagingInfo) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("keyword", null == keyword ? "" : keyword);
        String hql = "from SysParamItem t where (t.key like '%'||:keyword or t.name like '%'||:keyword)";
        List<SysParamItem> res = null;
        if (null == pagingInfo) {
            res = sysParamItemService.listByHQL(hql, params);
        } else {
            res = sysParamItemService.listByHQLAndPage(hql, params, pagingInfo);
        }
        return res;
    }

    @Override
    @Transactional(readOnly = true)
    public Select2QueryData loadSelectData(Select2QueryInfo queryInfo) {
        List<SysParamItem> params = query(queryInfo.getSearchValue(), queryInfo.getPagingInfo());
        Select2QueryData select2 = new Select2QueryData(queryInfo.getPagingInfo());
        for (SysParamItem param : params) {
            select2.addResultData(new Select2DataBean(param.getKey(), param.getName()));
        }
        return select2;
    }

    @Override
    @Transactional(readOnly = true)
    public Select2QueryData loadSelectDataByIds(Select2QueryInfo queryInfo) {
        Select2QueryData select2 = new Select2QueryData();
        if (null != queryInfo.getIds()) {
            for (String id : queryInfo.getIds()) {
                List<SysParamItem> params = listByKey(id);
                if (CollectionUtils.isNotEmpty(params)) {
                    select2.addResultData(new Select2DataBean(params.get(0).getKey(), params.get(0).getName()));
                }
            }
        }
        return select2;
    }
}
