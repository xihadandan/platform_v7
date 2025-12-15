/*
 * @(#)2019年5月22日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.component.select2;

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
public interface Select2UpdateApi {

    /**
     * 在前端校验，此方法不应该抛错
     * Select2DataBean.id非空时删除，否则Select2DataBean.text非空时添加，否则Select2QueryInfo.params.json
     * 添加时，必须把id回写到Select2DataBean.id
     *
     * @param bean
     * @param queryInfo
     */
    @Transactional(readOnly = false)
    public void update(Select2DataBean bean, Select2QueryInfo queryInfo);

}
