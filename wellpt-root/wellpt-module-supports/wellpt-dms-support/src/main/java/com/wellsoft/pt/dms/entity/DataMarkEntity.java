package com.wellsoft.pt.dms.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;

import javax.persistence.MappedSuperclass;

/**
 * Description:数据标记实体
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
public class DataMarkEntity extends TenantEntity {


    private String dataUuid;//数据UUID

    private String userId;//用户ID


    public String getDataUuid() {
        return dataUuid;
    }

    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
