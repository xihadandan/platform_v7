/*
* @(#)${createDate} V1.0
*
* Copyright 2015 WELL-SOFT, Inc. All rights reserved.
*/
package ${package}.facade.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ${package}.facade.service.${entity}DyformMaintain;
import com.wellsoft.pt.cg.core.support.BizData;
import com.wellsoft.pt.core.service.impl.BaseServiceImpl;
import com.wellsoft.pt.core.web.ResultMessage;
import com.wellsoft.pt.dyform.facade.DyFormApiFacade;
import com.wellsoft.pt.dyform.support.DyFormData;

/**
* Description: 动态表单 ${displayName}(${tableName}) 维护实现类
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
@Transactional
public class ${entity}DyformMaintainImpl extends BaseServiceImpl implements ${entity}DyformMaintain {

@Autowired
private DyFormApiFacade dyFormApiFacade;

/**
* 如何描述该方法
*
* (non-Javadoc)
* @see ${package}.facade.service.${entity}DyformMaintain#getData(com.wellsoft.pt.cg.core.support.BizData)
*/
@Override
public BizData getData(BizData data) {
// 1、获取动态表单数据
String formUuid = data.getFormUuid();
String dataUuid = data.getDataUuid();
DyFormData dyFormData = dyFormApiFacade.getDyFormData(formUuid, dataUuid);
data.setDyFormData(dyFormData);

// 2、获取业务数据
// TODO
return data;
}

/**
* 如何描述该方法
*
* (non-Javadoc)
* @see ${package}.facade.service.${entity}DyformMaintain#saveData(com.wellsoft.pt.cg.core.support.BizData)
*/
@Override
public ResultMessage saveData(BizData data) {
// 1、保存动态表单数据
DyFormData dyFormData = data.getDyFormData();
String dataUuid = dyFormApiFacade.saveFormData(dyFormData);

// 2、保存业务数据
// TODO

// 返回数据
ResultMessage msg = new ResultMessage();
msg.setData(dataUuid);
return msg;
}

/**
* 如何描述该方法
*
* (non-Javadoc)
* @see ${package}.facade.service.${entity}DyformMaintain#submitData(com.wellsoft.pt.cg.core.support.BizData)
*/
@Override
public void submitData(BizData data) {
// 1、保存动态表单数据
DyFormData dyFormData = data.getDyFormData();
dyFormApiFacade.saveFormData(dyFormData);

// 2、保存业务数据
// TODO
}

/**
* 如何描述该方法
*
* (non-Javadoc)
* @see ${package}.facade.service.${entity}DyformMaintain#deleteData(com.wellsoft.pt.cg.core.support.BizData)
*/
@Override
public void deleteData(BizData data) {
// 1、删除动态表单数据
String formUuid = data.getFormUuid();
String dataUuid = data.getDataUuid();
dyFormApiFacade.delFullFormData(formUuid, dataUuid);

// 2、删除业务数据
// TODO
}

}
