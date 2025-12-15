package com.wellsoft.pt.datastore.manager.entity;

import com.wellsoft.pt.manager.commons.reference.entity.ModuleFunctionConfigRefEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 引用数据仓库
 *
 * @author chenq
 * @date 2019/6/5
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/6/5    chenq		2019/6/5		Create
 * </pre>
 */
@Entity
@Table(name = "CD_DATA_STORE_DEFINITION_REF")
@DynamicUpdate
@DynamicInsert
public class CdDatastoreDefinitionRefEntity extends ModuleFunctionConfigRefEntity {


    private static final long serialVersionUID = 4886541017228159L;
}
