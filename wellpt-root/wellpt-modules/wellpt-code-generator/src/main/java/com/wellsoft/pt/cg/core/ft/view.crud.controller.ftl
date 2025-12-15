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

import com.wellsoft.pt.core.web.BaseController;
import ${package}.bean.${bean};
import ${package}.facade.service.${entity}ViewMaintain;

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
@RequestMapping(value = "/${moduleRequestPath!}${classRequestMappingPath}")
public class ${entity}ViewMaintainController extends BaseController {

@Autowired
private ${entity}ViewMaintain ${entityLowFisrt}ViewMaintain;

/**
* @param model
* @return
*/
@RequestMapping(value = "/new")
public String openNewDialog(Model model) {
// 绑定下拉列表等数据
bindSelectData(model);

model.addAttribute("${entityLowFisrt}Bean", new ${bean}());
return forward("/${moduleRequestPath!}/${jspViewName}_new");
}

/**
* @param model
* @return
*/
@RequestMapping(value = "/edit")
public String openEditDialog(Model model) {
// 绑定下拉列表等数据
bindSelectData(model);

model.addAttribute("${entityLowFisrt}Bean", new ${bean}());
return forward("/${moduleRequestPath!}/${jspViewName}_edit");
}

/**
* 如何描述该方法
*
* @param model
*/
private void bindSelectData(Model model) {
/*// 1、获取职级（数据字典）
List
<String> levelList = orgDutyExpandService.getDutyLevelList();
    Map
    <String
    , Object> levelMap = new LinkedHashMap
    <String
    , Object>();
    for (String level : levelList) {
    levelMap.put(level, level);
    }
    model.addAttribute("levels", levelMap);

    // 2、 职位序列（数据字典）
    List
    <Map
    <String
    , Object>> serieses = orgDutyExpandService.getDutySeriesList();
    Map
    <String
    , Object> serieseMap = new LinkedHashMap
    <String
    , Object>();
    for (Map
    <String
    , Object> map : serieses) {
    serieseMap.put(map.get("uuid").toString(), map.get("name"));
    }
    model.addAttribute("dutySeries", serieseMap);

    // 3、 获取直间接分类
    List
    <DataDictionary> list = orgDutyExpandService.getDirIndClassifyList();
        Map
        <String
        , Object> classifyMap = new LinkedHashMap
        <String
        , Object>();
        for (DataDictionary dataDict : list) {
        String name = dataDict.getName();
        String code = dataDict.getCode();
        classifyMap.put(code, name);
        }
        model.addAttribute("classifies", classifyMap);

        // 4、 是否启用
        Map
        <String
        , Object> isActiveMap = new LinkedHashMap
        <String
        , Object>();
        isActiveMap.put("1", "启用");
        isActiveMap.put("0", "停用");
        model.addAttribute("isActives", isActiveMap);*/
        }

        }
