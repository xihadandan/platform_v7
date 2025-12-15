/*
* @(#)${createDate} V1.0
*
* Copyright 2015 WELL-SOFT, Inc. All rights reserved.
*/
package ${package}.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wellsoft.pt.core.web.BaseController;

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
 * 2013-1-14.1	${author}        ${createDate}		Create
 * </pre>
*
*/
@Controller
@RequestMapping(value = "/${moduleRequestPath!}${classRequestMappingPath}")
public class ${entity}MgrController extends BaseController {

/**
* 打开列表界面
*
* @return
*/
@RequestMapping(value = "/list")
public String ${entityLowFisrt}List() {
return forward("/${moduleRequestPath!}/${jspViewName}_list");
}

}
