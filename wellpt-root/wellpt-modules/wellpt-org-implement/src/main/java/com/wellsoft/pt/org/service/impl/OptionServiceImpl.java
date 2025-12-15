/*

 * @(#)2013-1-14 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service.impl;

import com.wellsoft.context.service.CommonValidateService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.org.bean.OptionBean;
import com.wellsoft.pt.org.dao.OptionDao;
import com.wellsoft.pt.org.entity.Option;
import com.wellsoft.pt.org.service.OptionService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-14.1	zhulh		2013-1-14		Create
 * </pre>
 * @date 2013-1-14
 */
@Service
@Transactional
public class OptionServiceImpl extends BaseServiceImpl implements OptionService {

    @Autowired
    private OptionDao optionDao;

    @Autowired
    private CommonValidateService commonValidateService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.OptionService#getBean(java.lang.String)
     */
    @Override
    public OptionBean getBean(String uuid) {
        Option option = optionDao.get(uuid);
        OptionBean bean = new OptionBean();
        BeanUtils.copyProperties(option, bean);
        return bean;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.OptionService#saveBean(com.wellsoft.pt.org.bean.OptionBean)
     */
    @Override
    public void saveBean(OptionBean bean) {
        Option option = new Option();
        if (StringUtils.isBlank(bean.getUuid())) {
            option.setUuid(null);
        } else {
            option = optionDao.get(bean.getUuid());
        }
        BeanUtils.copyProperties(bean, option);
        if (!this.checkUnique(option)) {
            throw new RuntimeException("已经存在ID为[" + bean.getId() + "]的组织选择项!");
        }
        optionDao.save(option);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.OptionService#remove(java.lang.String)
     */
    @Override
    public void remove(String uuid) {
        optionDao.delete(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.OptionService#removeAll(java.util.Collection)
     */
    @Override
    public void removeAll(Collection<String> uuids) {
        for (String uuid : uuids) {
            this.remove(uuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.OptionService#getAll()
     */
    @Override
    public List<Option> getAll() {
        return this.optionDao.getAllOptions();
    }

    @Override
    public Boolean checkUnique(Option option) {
        HashMap<String, Object> queryMap = new HashMap<String, Object>();
        StringBuilder queryHqlBuff = new StringBuilder();
        queryHqlBuff.append("from Option option where option.tenantId=:tenantId and option.id=:id ");
        queryMap.put("tenantId", SpringSecurityUtils.getCurrentTenantId());
        queryMap.put("id", option.getId());
        if (StringUtils.isNotEmpty(option.getUuid())) {
            queryMap.put("uuid", option.getUuid());
            queryHqlBuff.append(" and uuid<>:uuid");
        }
        List<Option> options = this.dao.find(queryHqlBuff.toString(), queryMap, Option.class);
        if (options.size() > 0)
            return false;
        return true;
    }

}
