/*
 * @(#)2019年5月31日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.support.tpl;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.jpa.template.freemarker.CustomFreemarkerTemplateSharedVariable;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description: 当前用户归属单位ID条件模板变量
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年5月31日.1	zhulh		2019年5月31日		Create
 * </pre>
 * @date 2019年5月31日
 */
@Component
public class CurrentUserUnitIdConditionSharedVariable implements CustomFreemarkerTemplateSharedVariable {
    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.template.freemarker.CustomFreemarkerTemplateSharedVariable#getName()
     */
    @Override
    public String getName() {
        return "currentUserUnitIdCondition";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.template.freemarker.CustomFreemarkerTemplateSharedVariable#getValue()
     */
    @Override
    public TemplateModel getValue() {
        return new CurrentUserUnitIdConditionModel();
    }

    private class CurrentUserUnitIdConditionModel implements TemplateMethodModelEx {

        /**
         * (non-Javadoc)
         *
         * @see freemarker.template.TemplateMethodModelEx#exec(java.util.List)
         */
        @Override
        public Object exec(List arguments) throws TemplateModelException {
            StringBuilder sb = new StringBuilder();
            sb.append(Separator.SPACE.getValue());
            sb.append("(");
            if (CollectionUtils.isNotEmpty(arguments)) {
                String tableAlias = StringUtils.trim(ObjectUtils.toString(arguments.get(0), StringUtils.EMPTY));
                if (StringUtils.isNotBlank(tableAlias)) {
                    sb.append(tableAlias);
                    sb.append(".");
                }
            }
            sb.append("system_unit_id = :currentUserUnitId");
            sb.append(")");
            sb.append(Separator.SPACE.getValue());
            return sb.toString();
        }

    }

}
