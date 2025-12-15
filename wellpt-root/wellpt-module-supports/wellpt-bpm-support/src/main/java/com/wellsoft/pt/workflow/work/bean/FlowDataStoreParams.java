/*
 * @(#)2020年12月8日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.work.bean;

import com.wellsoft.pt.basicdata.datastore.bean.DataStoreParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年12月8日.1	zhulh		2020年12月8日		Create
 * </pre>
 * @date 2020年12月8日
 */
@ApiModel("流程数据仓库查询参数")
public class FlowDataStoreParams extends DataStoreParams {

    @ApiModelProperty("流程加载规则")
    private FlowLoadingRules loadingRules;

    /**
     * @return the loadingRules
     */
    public FlowLoadingRules getLoadingRules() {
        return loadingRules;
    }

    /**
     * @param loadingRules 要设置的loadingRules
     */
    public void setLoadingRules(FlowLoadingRules loadingRules) {
        this.loadingRules = loadingRules;
    }

}
