/*
 * @(#)2021-01-22 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.imgcategory.facade.service.impl;

import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.basicdata.imgcategory.facade.service.CdImgCategoryFacadeService;
import com.wellsoft.pt.basicdata.imgcategory.service.CdImgCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description: 数据库表CD_IMG_CATEGORY的门面服务实现类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-01-22.1	zhongzh		2021-01-22		Create
 * </pre>
 * @date 2021-01-22
 */
@Service
public class CdImgCategoryFacadeServiceImpl extends AbstractApiFacade implements CdImgCategoryFacadeService {

    @Autowired
    private CdImgCategoryService cdImgCategoryService;

}
