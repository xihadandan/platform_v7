/*
* @(#)${createDate} V1.0
*
* Copyright 2018 WELL-SOFT, Inc. All rights reserved.
*/
package ${package}.service;


import ${package}.entity.${entity}Entity;
import ${package}.dao.${entity}Dao;
import com.wellsoft.pt.jpa.service.JpaService;

/**
* Description: 数据库表${tableName}的service服务接口
*
* @author ${author}
* @date ${createDate}
* @version 1.0
*
*
<pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * ${createDate}.1	${author}        ${createDate}		Create
 * </pre>
*
*/
public interface ${entity}Service extends JpaService
<${entity}Entity, ${entity}Dao, String> {

}
