/*
 * @(#)${createDate} V1.0
 * 
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package ${package}.facade.service.impl;

import org.springframework.stereotype.Service;
import ${package}.facade.service.${entity}FacadeService;
import com.wellsoft.context.service.AbstractApiFacade;
import org.springframework.beans.factory.annotation.Autowired;
import ${package}.service.${entity}Service;

/**
 * Description: 数据库表${tableName}的门面服务实现类
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
@Service
public class ${entity}FacadeServiceImpl extends AbstractApiFacade implements ${entity}FacadeService {

    @Autowired
    private ${entity}Service ${entity?uncap_first}Service;





}
