package com.wellsoft.pt.dyform.manager.entity;

import com.wellsoft.pt.manager.commons.reference.entity.ModuleFunctionConfigRefEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 引用表单
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
@Table(name = "DYFORM_FORM_DEFINITION_REF")
@DynamicUpdate
@DynamicInsert
public class FormDefinitionRefEntity extends ModuleFunctionConfigRefEntity {

}
