/*
 * @(#)5/28/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.store;

import com.wellsoft.pt.basicdata.datastore.support.DataStoreInterfaceField;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreInterfaceFieldElement;
import com.wellsoft.pt.jpa.criteria.InterfaceParam;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 5/28/24.1	zhulh		5/28/24		Create
 * </pre>
 * @date 5/28/24
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DmsFileDataStoreInterfaceParam implements InterfaceParam {

    @DataStoreInterfaceField(name = "左连接数据模型", domType = DataStoreInterfaceFieldElement.CUSTOM, defaultValue = "file-datastore-left-join-config")
    private DmsFileLeftJoinConfig leftJoinConfig;

    /**
     * @return the leftJoinConfig
     */
    public DmsFileLeftJoinConfig getLeftJoinConfig() {
        return leftJoinConfig;
    }

    /**
     * @param leftJoinConfig 要设置的leftJoinConfig
     */
    public void setLeftJoinConfig(DmsFileLeftJoinConfig leftJoinConfig) {
        this.leftJoinConfig = leftJoinConfig;
    }

}
