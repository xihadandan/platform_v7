/*
* @(#)${createDate} V1.0
*
* Copyright 2015 WELL-SOFT, Inc. All rights reserved.
*/
package ${package}.facade.service;

import java.util.Collection;

import ${package}.bean.${bean};
import com.wellsoft.pt.core.service.BaseService;

/**
* Description: 如何描述该类
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
public interface ${entity}ViewMaintain extends BaseService {

/**
* 获取
*
* @param uuid
* @return
*/
${bean} getBean(String uuid);

/**
* 保存
*
* @param bean
* @return
*/
void saveBean(${bean} bean);

/**
* 删除
*
* @param uuid
* @return
*/
void remove(String uuid);

/**
* 批量删除
*
* @param uuid
* @return
*/
void removeAll(Collection
<String> uuids);

    }
