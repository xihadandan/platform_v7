/*
 * @(#)8/15/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.document.store;

import com.wellsoft.pt.basicdata.datastore.support.DataStoreInterfaceField;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreInterfaceFieldElement;
import com.wellsoft.pt.jpa.criteria.InterfaceParam;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 8/15/25.1	    zhulh		8/15/25		    Create
 * </pre>
 * @date 8/15/25
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MongoDocumentDataStoreInterfaceParam implements InterfaceParam {

    @DataStoreInterfaceField(name = "集合名称", domType = DataStoreInterfaceFieldElement.SELECT, service = "mongoDocumentService.getCollectionNames")
    private String collectionName;

    /**
     * @return the collectionName
     */
    public String getCollectionName() {
        return collectionName;
    }

    /**
     * @param collectionName 要设置的collectionName
     */
    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

}
