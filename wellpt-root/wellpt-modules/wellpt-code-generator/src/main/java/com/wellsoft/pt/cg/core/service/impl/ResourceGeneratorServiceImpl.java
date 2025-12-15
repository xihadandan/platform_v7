/*
 * @(#)2015-8-13 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.cg.core.service.impl;

import com.wellsoft.pt.cg.core.Context;
import com.wellsoft.pt.cg.core.service.ResourceGeneratorService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.audit.bean.ResourceBean;
import com.wellsoft.pt.security.audit.entity.Resource;
import com.wellsoft.pt.security.audit.facade.service.ResourceFacadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 * 2015-8-13.1	zhulh		2015-8-13		Create
 * </pre>
 * @date 2015-8-13
 */
@Service
public class ResourceGeneratorServiceImpl extends BaseServiceImpl implements ResourceGeneratorService {

    @Autowired
    private ResourceFacadeService resourceFacadeService;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.cg.core.service.ResourceGeneratorService#generate(com.wellsoft.pt.cg.core.Context, java.lang.String)
     */
    @Override
    @Transactional
    public void generate(Context context, String tableName) {
        String uuid = context.getConfigJson().getResourceTemplateUuid();
        Resource resource = this.dao.get(Resource.class, uuid);
        ResourceBean bean = new ResourceBean();
        BeanUtils.copyProperties(resource, bean);
        bean.setName(context.getName());
        String code = bean.getCode() + "_" + tableName;
        bean.setCode(code);
        bean.setUuid(null);
        resourceFacadeService.saveBean(bean);

        Resource parent = resource.getParent();
        if (parent != null) {
            List<Resource> resources = this.dao.findBy(Resource.class, "code", code);
            for (Resource res : resources) {
                res.setParent(parent);
                this.dao.save(res);
            }
        }
    }

}
