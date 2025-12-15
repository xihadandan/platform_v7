/*
* @(#)${createDate} V1.0
*
* Copyright 2015 WELL-SOFT, Inc. All rights reserved.
*/
package ${package}.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wellsoft.pt.cg.core.support.BizData;
import com.wellsoft.pt.core.web.BaseController;
import com.wellsoft.pt.dyform.facade.DyFormApiFacade;

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
@Controller
@RequestMapping(value = "/${moduleRequestPath!}${classRequestMappingPath}/maintain")
public class ${entity}DyformMaintainController extends BaseController {

@Autowired
private DyFormApiFacade dyFormApiFacade;

/**
* 如何描述该方法
*
* @param formUuid
* @param model
* @return
*/
@RequestMapping(value = "/new")
public String new${entity}(@RequestParam(value = "formDefId", defaultValue = "${formDefId}") String formDefId, Model model) {
// 根据表单定义ID获取UUID
String formUuid = dyFormApiFacade.getFormUuidById(formDefId);
// 业务数据
BizData bizData = new BizData();
bizData.setFormUuid(formUuid);
model.addAttribute(bizData);
return forward("/${moduleRequestPath!}/${jspViewName}_form");
}

/**
* 如何描述该方法
*
* @param formUuid
* @param dataUuid
* @param model
* @return
*/
@RequestMapping(value = "/view")
public String view${entity}(@RequestParam(value = "formUuid") String formUuid,
@RequestParam(value = "dataUuid") String dataUuid, Model model) {
// 业务数据
BizData bizData = new BizData();
bizData.setFormUuid(formUuid);
bizData.setDataUuid(dataUuid);
model.addAttribute(bizData);
return forward("/${moduleRequestPath!}/${jspViewName}_form");
}

}
