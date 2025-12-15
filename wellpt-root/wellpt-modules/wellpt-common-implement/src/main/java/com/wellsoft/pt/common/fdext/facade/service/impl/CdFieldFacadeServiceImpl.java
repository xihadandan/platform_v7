/*
 * @(#)2016-03-11 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.fdext.facade.service.impl;

import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.common.fdext.bean.CdFieldExtensionValue;
import com.wellsoft.pt.common.fdext.entity.CdFieldExtDefinition;
import com.wellsoft.pt.common.fdext.facade.service.CdFieldFacade;
import com.wellsoft.pt.common.fdext.service.CdFieldExtDefinitionService;
import com.wellsoft.pt.common.fdext.service.CdFieldExtValueService;
import com.wellsoft.pt.common.fdext.support.DyFieldRender;
import com.wellsoft.pt.common.fdext.support.ICdFieldRender;
import com.wellsoft.pt.common.fdext.support.RenderFactory;
import org.apache.commons.lang.StringUtils;
import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-03-11.1	zhongzh		2016-03-11		Create
 * </pre>
 * @date 2016-03-11
 */
@Component
public class CdFieldFacadeServiceImpl extends AbstractApiFacade implements CdFieldFacade {

    public final static String HQL_CDFIELD_DEFINE = "from CdFieldExtDefinition t where t.enabled = 1 and t.groupCode = :groupCode and t.tenantId = :tenantId order by t.sortOrder asc";

    @Autowired
    private CdFieldExtValueService extValueService;

    @Autowired
    private CdFieldExtDefinitionService extDefinitionService;

    // 保存扩展字段值
    public void saveData(String dataUuid, String groupType, CdFieldExtensionValue bean) {
        if (StringUtil.isBlank(bean.getDataUuid()) && StringUtils.isNotBlank(dataUuid)) {
            bean.setDataUuid(dataUuid);
        }
        if (StringUtil.isBlank(bean.getGroupCode()) && StringUtils.isNotBlank(groupType)) {
            bean.setGroupCode(groupType);
        }
        extValueService.saveBean(bean);
    }

    // 加载扩展字段值
    public CdFieldExtensionValue getData(String dataUuid, String groupType) {
        return extValueService.getBean(dataUuid, groupType);
    }

    public List<ICdFieldRender> getFields(String tenantId, String groupCode) {
        Map<String, Object> example = new HashMap<String, Object>();
        example.put("tenantId", tenantId);
        example.put("groupCode", groupCode);
        List<CdFieldExtDefinition> definitions = extDefinitionService.find(HQL_CDFIELD_DEFINE, example);
        if (definitions == null || definitions.size() <= 0) {
            return Collections.emptyList();
        }
        List<ICdFieldRender> defs = new ArrayList<ICdFieldRender>(definitions.size());
        for (CdFieldExtDefinition definition : definitions) {
            ICdFieldRender def = RenderFactory.createRender(definition);
            defs.add(def);
        }
        return defs;
    }

    public DyFieldRender getDyFieldRender(String tenantId, String groupCode) {
        return new DyFieldRender(this.getFields(tenantId, groupCode));
    }

    public DyFieldRender getDyFieldRender(String tenantId, String groupCode, String dataUuid) {
        if (StringUtils.isBlank(dataUuid)) {
            return this.getDyFieldRender(tenantId, groupCode);
        }
        List<ICdFieldRender> renders = this.getFields(tenantId, groupCode);
        CdFieldExtensionValue data = extValueService.getBean(dataUuid, groupCode);
        return new DyFieldRender(renders, data);
    }
}
