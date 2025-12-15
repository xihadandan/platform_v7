/*
 * @(#)${createDate} V1.0
 * 
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package ${package}.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

<#if extendsEntity??>
import com.wellsoft.context.jdbc.entity.${extendsEntity};
</#if>


${propTypePackages}

/**
 * Description: 数据库表${tableName}的实体类
 *  
 * @author ${author}
 * @date ${createDate}
 * @version 1.0
 * 
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * ${createDate}.1	${author}		${createDate}		Create
 * </pre>
 *
 */
@Entity
@Table(name = "${tableName}")
@DynamicUpdate
@DynamicInsert
public class ${className}Entity <#if extendsEntity??>extends ${extendsEntity}</#if> {

	private static final long serialVersionUID = ${timestamp}L;
	
	<#list props as prop> 
	${generateProperty(prop)}
	</#list> 
	<#list props as prop> 
	${generateGet(prop)}
	${generateSet(prop)}
	</#list> 
}
