/*
 * @(#)2019年5月22日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.component.select2;

import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: select2编辑接口
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年5月22日.1	zhongzh		2019年5月22日		Create
 * </pre>
 * @date 2019年5月22日
 */
public class AbstractSelect2Update implements Select2UpdateApi {

    @Transactional(readOnly = false)
    public void update(Select2DataBean bean, Select2QueryInfo queryInfo) {
        String id = bean.getId();
        if (StringUtils.isNotBlank(id)) {
            doDel(id, bean);
        } else if (StringUtils.isNoneBlank(bean.getText())) {
            String uuid = doAdd(bean.getText(), bean);
            if (StringUtils.isNotBlank(uuid)) {
                bean.setId(uuid);
            }
        } else {
            // 修改
        }
    }

    /**
     * 添加
     * 非抽象方法，适用可选
     *
     * @param text
     * @param bean
     * @return 添加的唯一主键
     */
    public String doAdd(String text, Select2DataBean bean) {
        return null;
    }

    /**
     * 删除
     * 非抽象方法，适用可选
     *
     * @param uuid
     * @param bean
     */
    public void doDel(String uuid, Select2DataBean bean) {

    }

}
