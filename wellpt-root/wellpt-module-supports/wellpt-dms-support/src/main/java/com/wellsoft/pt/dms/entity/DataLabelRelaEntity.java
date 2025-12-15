package com.wellsoft.pt.dms.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;

import javax.persistence.MappedSuperclass;

/**
 * Description:数据标签实体
 *
 * @author chenq
 * @date 2018/6/11
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/6/11    chenq		2018/6/11		Create
 * </pre>
 */
@MappedSuperclass
public class DataLabelRelaEntity extends TenantEntity {


    private String dataUuid;//数据UUID

    private String labelUuid;//标签UUID


    public String getDataUuid() {
        return dataUuid;
    }

    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

    public String getLabelUuid() {
        return labelUuid;
    }

    public void setLabelUuid(String labelUuid) {
        this.labelUuid = labelUuid;
    }


}
