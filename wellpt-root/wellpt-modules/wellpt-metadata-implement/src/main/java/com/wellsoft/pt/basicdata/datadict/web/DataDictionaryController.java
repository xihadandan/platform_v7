/*
 * @(#)2013-1-25 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.basicdata.datadict.entity.DataDictionary;
import com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService;
import com.wellsoft.pt.jpa.util.BeanUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-25.1	zhulh		2013-1-25		Create
 * </pre>
 * @date 2013-1-25
 */
@Api(tags = "数据字典")
@RestController
@RequestMapping("/basicdata/datadict")
public class DataDictionaryController extends BaseController {

    @Autowired
    private DataDictionaryService dataDictionaryService;

    /**
     * 跳转到数据字典维护界面
     *
     * @return
     */
    @GetMapping(value = "")
    public String datadict() {
        return forward("/basicdata/datadict/datadict");
    }

    /**
     * 跳转到平台数据字典维护界面
     *
     * @return
     */
    @GetMapping(value = "/pt")
    public String ptDatadict() {
        return forward("/basicdata/datadict/pt_datadict");
    }

    /**
     * 跳转到应用数据字典维护界面
     *
     * @return
     */
    @GetMapping(value = "/app")
    public String appDatadict() {
        return forward("/basicdata/datadict/app_datadict");
    }

    /**
     * 跳转到业务数据字典维护界面
     *
     * @return
     */
    @GetMapping(value = "/biz")
    public String bizDatadict() {
        return forward("/basicdata/datadict/biz_datadict");
    }

    /**
     * 根据数据字典的uuid取得全路径,
     * level表示往上追溯几层,-1表示全路径
     *
     * @return
     */
    @GetMapping(value = "/{uuid}/cn_absolute_path/{level}")
    @ResponseBody
    public String getCnAbsolutePath(@PathVariable(value = "uuid") String uuid, @PathVariable(value = "level") int level) {
        return dataDictionaryService.getCnAbsolutePath(uuid, level);
    }

    @GetMapping(value = "/type/{type}")
    @ApiOperation(value = "根据类型获取数据字典", notes = "根据类型获取数据字典", response = ResultMessage.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "type", value = "字典类型", paramType = "query", dataType = "String", required = true)})
    @ResponseBody
    public ResultMessage getByType(@PathVariable(value = "type") String type) {
        ResultMessage result = new ResultMessage();
        DataDictionary dataDictionary = dataDictionaryService.getByType(type);
        if (null != dataDictionary) {
            DataDictionary bean = new DataDictionary();
            BeanUtils.copyProperties(dataDictionary, bean);
            result.setData(bean);
        } else {
        }
        return result;
    }

}
