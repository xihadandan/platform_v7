package com.wellsoft.pt.print.template.manager.entity;

import com.wellsoft.pt.manager.commons.reference.entity.ModuleFunctionConfigRefEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 引用打印模板
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
@Table(name = "cd_print_template_ref")
@DynamicUpdate
@DynamicInsert
public class CdPrintTemplateRefEntity extends ModuleFunctionConfigRefEntity {


    private static final long serialVersionUID = -4113950588836042910L;
}
