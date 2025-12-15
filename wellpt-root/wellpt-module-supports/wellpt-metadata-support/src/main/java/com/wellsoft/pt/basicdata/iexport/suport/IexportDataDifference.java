/*
 * @(#)2016年8月7日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.suport;

import java.io.Serializable;
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
 * 2016年8月7日.1	zhulh		2016年8月7日		Create
 * </pre>
 * @date 2016年8月7日
 */
public class IexportDataDifference implements Serializable {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 7886351972287439093L;

    // 数据UUID
    private String uuid;
    // 数据类型
    private String type;
    // 不同的详细信息
    private List<IexportDataDifferenceDetail> dataDifferenceDetails;

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid 要设置的uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the dataDifferenceDetails
     */
    public List<IexportDataDifferenceDetail> getDataDifferenceDetails() {
        return dataDifferenceDetails;
    }

    /**
     * @param dataDifferenceDetails 要设置的dataDifferenceDetails
     */
    public void setDataDifferenceDetails(List<IexportDataDifferenceDetail> dataDifferenceDetails) {
        this.dataDifferenceDetails = dataDifferenceDetails;
    }

}
