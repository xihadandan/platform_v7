package com.wellsoft.pt.dms.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;

import javax.persistence.MappedSuperclass;

/**
 * Description:数据分类关系实体
 *
 * @author chenq
 * @date 2018/6/29
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/6/29    chenq		2018/6/29		Create
 * </pre>
 */
@MappedSuperclass
public class DataClassifyRelaEntity extends TenantEntity {


    private String dataUuid;//数据UUID

    private String classifyUuid;//分类UUID


    public String getDataUuid() {
        return dataUuid;
    }

    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

    public String getClassifyUuid() {
        return classifyUuid;
    }

    public void setClassifyUuid(String classifyUuid) {
        this.classifyUuid = classifyUuid;
    }


}
