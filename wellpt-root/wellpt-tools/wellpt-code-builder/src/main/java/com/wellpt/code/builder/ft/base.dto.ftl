/*
 * @(#)${createDate} V1.0
 * 
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package ${package}.dto;

import java.io.Serializable;


${propTypePackages}

/**
 * Description: 数据库表${tableName}的对应的DTO类
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
public class ${className}Dto implements Serializable {

	private static final long serialVersionUID = ${timestamp}L;
	
	<#list props as prop> 
	${generateProperty(prop)}
	</#list> 
	<#list props as prop> 
	${generateGet(prop)}
	${generateSet(prop)}
	</#list> 
}
