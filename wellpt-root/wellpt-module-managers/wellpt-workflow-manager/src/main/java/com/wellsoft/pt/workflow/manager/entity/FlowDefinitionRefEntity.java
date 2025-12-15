package com.wellsoft.pt.workflow.manager.entity;

import com.wellsoft.pt.manager.commons.reference.entity.ModuleFunctionConfigRefEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 引用流程
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
@Table(name = "WF_FLOW_DEFINITION_REF")
@DynamicUpdate
@DynamicInsert
public class FlowDefinitionRefEntity extends ModuleFunctionConfigRefEntity {

    private static final long serialVersionUID = 9026306576722551655L;

}
