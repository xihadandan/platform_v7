/*
 * @(#)5/5/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.material.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.basicdata.material.support.CdMaterialParams;

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
 * 5/5/23.1	zhulh		5/5/23		Create
 * </pre>
 * @date 5/5/23
 */
public interface CdMaterialFacadeService extends Facade {

    /**
     * 生成业务材料
     *
     * @param materialParams
     * @return
     */
    List<Long> generateMaterial(CdMaterialParams materialParams);

}
