/*
 * @(#)2021-01-22 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.imgcategory.dto;

import com.wellsoft.pt.basicdata.imgcategory.entity.CdImgCategoryEntity;

import java.util.List;

/**
 * Description: 数据库表CD_IMG_CATEGORY的对应的DTO类
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
public class CdImgCategoryDto extends CdImgCategoryEntity {

    private static final long serialVersionUID = 1611281606517L;

    /**
     * 分类下的文件ID列表
     */
    private List<String> fileIDs;

    public List<String> getFileIDs() {
        return fileIDs;
    }

    public void setFileIDs(List<String> fileIDs) {
        this.fileIDs = fileIDs;
    }

}
