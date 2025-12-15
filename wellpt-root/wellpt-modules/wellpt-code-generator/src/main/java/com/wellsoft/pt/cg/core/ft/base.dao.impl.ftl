/*
* @(#)${createDate} V1.0
*
* Copyright 2018 WELL-SOFT, Inc. All rights reserved.
*/
package ${package}.dao.impl;

import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

import ${package}.entity.${entity}Entity;
import ${package}.dao.${entity}Dao;


/**
* Description: 数据库表${tableName}的DAO接口实现类
*
* @author ${author}
* @date ${createDate}
* @version 1.0
*
*
*
<pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * ${createDate}.1    ${author}        ${createDate}		Create
 * </pre>
*
*/
@Repository
public class ${entity}DaoImpl extends AbstractJpaDaoImpl
<${entity}Entity, String> implements ${entity}Dao {


}

