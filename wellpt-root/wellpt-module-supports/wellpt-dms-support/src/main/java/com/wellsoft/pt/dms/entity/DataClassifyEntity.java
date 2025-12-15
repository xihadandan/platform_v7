package com.wellsoft.pt.dms.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;

import javax.persistence.MappedSuperclass;

/**
 * Description:分类实体
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
public class DataClassifyEntity extends TenantEntity {


    private String classifyName;//分类名称

    private String parentUuid;//父级分类UUID

    private String userId;//分类归属用户id


}
