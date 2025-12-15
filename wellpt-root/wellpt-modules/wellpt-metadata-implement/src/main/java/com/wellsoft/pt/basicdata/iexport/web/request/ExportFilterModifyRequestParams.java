package com.wellsoft.pt.basicdata.iexport.web.request;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年11月17日   chenq	 Create
 * </pre>
 */
public class ExportFilterModifyRequestParams implements Serializable {

    private List<String> typeUuids;

    private Integer modifyDays;

    public List<String> getTypeUuids() {
        return typeUuids;
    }

    public void setTypeUuids(List<String> typeUuids) {
        this.typeUuids = typeUuids;
    }

    public Integer getModifyDays() {
        return modifyDays;
    }

    public void setModifyDays(Integer modifyDays) {
        this.modifyDays = modifyDays;
    }
}
