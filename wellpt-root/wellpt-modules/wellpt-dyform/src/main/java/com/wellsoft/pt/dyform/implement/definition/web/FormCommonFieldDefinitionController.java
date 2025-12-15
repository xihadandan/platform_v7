package com.wellsoft.pt.dyform.implement.definition.web;

import com.wellsoft.context.component.jqgrid.JqGridQueryController;
import com.wellsoft.context.web.controller.IgnoreResultAdvice;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.dyform.implement.definition.entity.FormCommonFieldDefinition;
import com.wellsoft.pt.dyform.implement.definition.entity.FormCommonFieldRef;
import com.wellsoft.pt.dyform.implement.definition.service.FormCommonFieldDefinitionService;
import com.wellsoft.pt.dyform.implement.definition.service.FormCommonFieldRefService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 表单公共字段字义Controller类
 *
 * @author qiufy
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年4月23日.1	qiufy		2019年4月23日		Create
 * </pre>
 * @date 2019年4月23日
 */
@Api(tags = "表单公共字段定义接口")
@IgnoreResultAdvice
@Controller
@RequestMapping({"/pt/dyform/field", "/api/dyform/field"})
public class FormCommonFieldDefinitionController extends JqGridQueryController {

    @Autowired
    private FormCommonFieldRefService formFormCommonFieldService;

    @Autowired
    private FormCommonFieldDefinitionService formCommonFieldService;

    /**
     * 打开资源列表界面
     *
     * @return
     */
    @ApiIgnore
    @RequestMapping(value = "/index")
    public String list() {
        return forward("/pt/dyform/definition/dyform_common_field");
    }

    /**
     * 保存
     *
     * @param fieldDefinition
     * @param request
     * @param response
     */
    @RequestMapping(value = "/save")
    @ApiOperation(value = "保存字段", notes = "保存字段")
    @ResponseBody
    public void save(@RequestBody FormCommonFieldDefinition fieldDefinition, HttpServletRequest request,
                     HttpServletResponse response) {
        formCommonFieldService.saveDyformCommonField(fieldDefinition);
    }

    /**
     * 根据UUID获取表单
     *
     * @param uuid
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/get/{uuid}")
    @ApiOperation(value = "根据表单定义UUID获取字段列表", notes = "根据表单定义UUID获取字段列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "表单定义UUID", paramType = "query", dataType = "String", required = true)})
    @ResponseBody
    public ResultMessage get(@PathVariable String uuid, HttpServletRequest request, HttpServletResponse response) {
        ResultMessage resultMessage = new ResultMessage();
        FormCommonFieldDefinition bean = formCommonFieldService.getDyformCommonFieldByUUID(uuid);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("bean", bean);
        // 获取关联表数据
        List<FormCommonFieldRef> list = formFormCommonFieldService.getFormCommonFieldByFieldUuid(uuid);
        if (CollectionUtils.isEmpty(list)) {
            list = new ArrayList<FormCommonFieldRef>();
        }
        map.put("list", list);
        resultMessage.setData(map);
        return resultMessage;
    }

    /**
     * 根据UUID删除
     *
     * @param bean
     */
    @RequestMapping(value = "/delete")
    @ApiOperation(value = "字段删除", notes = "字段删除")
    @ResponseBody
    public void delete(@RequestBody FormCommonFieldDefinition bean) {
        formCommonFieldService.deleteDyformCommonFieldDefinitionByUUID(bean.getUuid());
    }

    /**
     * 多个删除
     *
     * @param ids
     */
    @ApiIgnore
    @RequestMapping(value = "/deleteAll")
    @ResponseBody
    public void deleteAll(String ids) {
        String[] uuids = ids.split(",");
        formCommonFieldService.deleteAllDyformCommonFieldDefinition(uuids);
    }

}
