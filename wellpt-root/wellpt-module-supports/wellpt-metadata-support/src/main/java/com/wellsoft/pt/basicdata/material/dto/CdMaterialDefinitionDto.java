/*
 * @(#)4/28/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.material.dto;

import com.google.common.collect.Lists;
import com.wellsoft.pt.basicdata.material.entity.CdMaterialDefinitionEntity;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import io.swagger.annotations.ApiModelProperty;

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
 * 4/28/23.1	zhulh		4/28/23		Create
 * </pre>
 * @date 4/28/23
 */
public class CdMaterialDefinitionDto extends CdMaterialDefinitionEntity {

    @ApiModelProperty("材料形式列表")
    private List<String> mediumTypes = Lists.newArrayListWithExpectedSize(0);

    @ApiModelProperty("电子材料格式")
    private List<String> formats = Lists.newArrayListWithExpectedSize(0);

    @ApiModelProperty("样例文件信息列表")
    private List<LogicFileInfo> sampleFileInfos = Lists.newArrayListWithExpectedSize(0);

    /**
     * @return the mediumTypes
     */
    public List<String> getMediumTypes() {
        return mediumTypes;
    }

    /**
     * @param mediumTypes 要设置的mediumTypes
     */
    public void setMediumTypes(List<String> mediumTypes) {
        this.mediumTypes = mediumTypes;
    }

    /**
     * @return the formats
     */
    public List<String> getFormats() {
        return formats;
    }

    /**
     * @param formats 要设置的formats
     */
    public void setFormats(List<String> formats) {
        this.formats = formats;
    }

    /**
     * @return the sampleFileInfos
     */
    public List<LogicFileInfo> getSampleFileInfos() {
        return sampleFileInfos;
    }

    /**
     * @param sampleFileInfos 要设置的sampleFileInfos
     */
    public void setSampleFileInfos(List<LogicFileInfo> sampleFileInfos) {
        this.sampleFileInfos = sampleFileInfos;
    }
}
