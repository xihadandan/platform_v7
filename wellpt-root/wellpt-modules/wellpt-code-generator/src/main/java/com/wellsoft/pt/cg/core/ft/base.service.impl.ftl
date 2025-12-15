/*
* @(#)${createDate} V1.0
*
* Copyright 2018 WELL-SOFT, Inc. All rights reserved.
*/
package ${package}.service.impl;

import org.springframework.stereotype.Service;
import ${package}.entity.${entity}Entity;
import ${package}.service.${entity}Service;
import ${package}.dao.${entity}Dao;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;

/**
* Description: 数据库表${tableName}的service服务接口实现类
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
@Service
public class ${entity}ServiceImpl extends AbstractJpaServiceImpl
<${entity}Entity, ${entity}Dao, String> implements ${entity}Service {


}
