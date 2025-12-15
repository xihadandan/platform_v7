package com.wellsoft.pt.message.manager.entity;

import com.wellsoft.pt.manager.commons.reference.entity.ModuleFunctionConfigRefEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/6/12
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/6/12    chenq		2019/6/12		Create
 * </pre>
 */
@Entity
@Table(name = "msg_message_template_ref")
@DynamicUpdate
@DynamicInsert
public class MessageTemplateRefEntity extends ModuleFunctionConfigRefEntity {
    private static final long serialVersionUID = -3921276074162517161L;
}
