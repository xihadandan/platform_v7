/*
 * @(#)2019年8月29日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository.rest.client.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.dyform.implement.repository.rest.client.response.SubformDataDeleteResponse;
import com.wellsoft.pt.dyform.implement.repository.rest.client.support.RepositoryFormDataApiServiceNames;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年8月29日.1	zhulh		2019年8月29日		Create
 * </pre>
 * @date 2019年8月29日
 */
public class SubformDataDeleteRequest extends WellptRequest<SubformDataDeleteResponse> {

    // 主表ID
    private String mainformId;

    // 主表数据UUID
    private String mainformDataUuid;

    // 从表ID
    private String subformId;

    /**
     * @return the mainformId
     */
    public String getMainformId() {
        return mainformId;
    }

    /**
     * @param mainformId 要设置的mainformId
     */
    public void setMainformId(String mainformId) {
        this.mainformId = mainformId;
    }

    /**
     * @return the mainformDataUuid
     */
    public String getMainformDataUuid() {
        return mainformDataUuid;
    }

    /**
     * @param mainformDataUuid 要设置的mainformDataUuid
     */
    public void setMainformDataUuid(String mainformDataUuid) {
        this.mainformDataUuid = mainformDataUuid;
    }

    /**
     * @return the subformId
     */
    public String getSubformId() {
        return subformId;
    }

    /**
     * @param subformId 要设置的subformId
     */
    public void setSubformId(String subformId) {
        this.subformId = subformId;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptRequest#getApiServiceName()
     */
    @Override
    public String getApiServiceName() {
        return RepositoryFormDataApiServiceNames.SUBFORM_DATA_DELETE;
    }

}
