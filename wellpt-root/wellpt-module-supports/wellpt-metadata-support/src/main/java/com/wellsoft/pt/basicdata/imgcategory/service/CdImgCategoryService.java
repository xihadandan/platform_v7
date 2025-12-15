/*
 * @(#)2021-01-22 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.imgcategory.service;

import com.wellsoft.pt.basicdata.imgcategory.dao.CdImgCategoryDao;
import com.wellsoft.pt.basicdata.imgcategory.entity.CdImgCategoryEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 数据库表CD_IMG_CATEGORY的service服务接口
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
public interface CdImgCategoryService extends JpaService<CdImgCategoryEntity, CdImgCategoryDao, String> {

    public abstract CdImgCategoryEntity saveBean(CdImgCategoryEntity bean);

    public abstract List<CdImgCategoryEntity> queryAllCatetory(String systemUnitId);

}
