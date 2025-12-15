/*
 * @(#)2015-6-18 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.cg.facade.impl;

import com.wellsoft.pt.cg.bean.CodeGeneratorConfigBean;
import com.wellsoft.pt.cg.entity.CodeGeneratorConfig;
import com.wellsoft.pt.cg.facade.CodeGeneratorConfigMgr;
import com.wellsoft.pt.cg.service.CodeGeneratorConfigService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-18.1	zhulh		2015-6-18		Create
 * </pre>
 * @date 2015-6-18
 */
@Service
@Transactional
public class CodeGeneratorConfigMgrImpl extends BaseServiceImpl implements CodeGeneratorConfigMgr {

    @Autowired
    private CodeGeneratorConfigService codeGeneratorConfigService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.cg.facade.CodeGeneratorConfigMgr#getBean(java.lang.String)
     */
    @Override
    public CodeGeneratorConfigBean getBean(String uuid) {
        CodeGeneratorConfig config = codeGeneratorConfigService.get(uuid);
        CodeGeneratorConfigBean bean = new CodeGeneratorConfigBean();
        BeanUtils.copyProperties(config, bean);
        return bean;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.cg.facade.CodeGeneratorConfigMgr#saveBean(com.wellsoft.pt.cg.bean.CodeGeneratorConfigBean)
     */
    @Override
    public void saveBean(CodeGeneratorConfigBean bean) {
        String uuid = bean.getUuid();
        CodeGeneratorConfig codeGeneratorConfig = new CodeGeneratorConfig();
        if (StringUtils.isNotBlank(uuid)) {
            codeGeneratorConfig = codeGeneratorConfigService.get(uuid);
        }
        BeanUtils.copyProperties(bean, codeGeneratorConfig);
        codeGeneratorConfigService.save(codeGeneratorConfig);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.cg.facade.CodeGeneratorConfigMgr#remove(java.lang.String)
     */
    @Override
    public void remove(String uuid) {
        codeGeneratorConfigService.remove(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.cg.facade.CodeGeneratorConfigMgr#removeAll(java.util.Collection)
     */
    @Override
    public void removeAll(Collection<String> uuids) {
        codeGeneratorConfigService.removeAll(uuids);
    }

}
