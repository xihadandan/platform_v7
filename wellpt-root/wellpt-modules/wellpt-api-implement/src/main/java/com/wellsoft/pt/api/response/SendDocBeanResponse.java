package com.wellsoft.pt.api.response;

import com.wellsoft.pt.api.WellptResponse;

public class SendDocBeanResponse extends WellptResponse {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 4623176981925213014L;

    private String dataUuid;

    public String getDataUuid() {
        return dataUuid;
    }

    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

}
