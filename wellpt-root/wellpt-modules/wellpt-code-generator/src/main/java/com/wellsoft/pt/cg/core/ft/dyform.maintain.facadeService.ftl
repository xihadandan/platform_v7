/*
* @(#)${createDate} V1.0
*
* Copyright 2015 WELL-SOFT, Inc. All rights reserved.
*/
package ${package}.facade.service;

import com.wellsoft.pt.cg.core.support.BizData;
import com.wellsoft.pt.core.service.BaseService;
import com.wellsoft.pt.core.web.ResultMessage;

/**
* Description: 动态表单 ${displayName}(${tableName}) 维护接口
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
public interface ${entity}DyformMaintain extends BaseService {

/**
* 获取数据
*
* @param data
* @return
*/
BizData getData(BizData data);

/**
* 保存数据
*
* @param data
*/
ResultMessage saveData(BizData data);

/**
* 提交数据
*
* @param data
*/
void submitData(BizData data);

/**
* 删除数据
*
* @param data
*/
void deleteData(BizData data);

}