/*
 * @(#)2017年10月14日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.mapper.web;

import com.wellsoft.context.util.encode.EncodeUtils;
import com.wellsoft.context.web.controller.BaseFormDataController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.basicdata.mapper.DataMapper;
import com.wellsoft.pt.basicdata.mapper.service.MapperService;
import com.wellsoft.pt.basicdata.mapper.test.BeanTest;
import com.wellsoft.pt.basicdata.mapper.test.TestMapperBuilder1;
import com.wellsoft.pt.basicdata.mapper.test.TestMapperBuilder2;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import net.sf.json.JSONObject;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年10月14日.1	zhongzh		2017年10月14日		Create
 * </pre>
 * @date 2017年10月14日
 */
@Controller
@RequestMapping("/basicdata/datamapper")
public class DataMapperController extends BaseFormDataController {

    @Autowired
    private MapperService mapperService;

    @Autowired
    private DyFormFacade dyFormApiFacade;

    /**
     * 打开列表界面
     *
     * @return
     */
    @RequestMapping(value = "/list")
    public String cdDataMapperList() {
        return forward("/pt/basicdata/mapper/cd_data_mapper_list");
    }

    @RequestMapping("convert")
    @ResponseBody
    public ResultMessage convert(@RequestParam(value = "mapId") String mapId,
                                 @RequestParam(value = "source") String source, HttpServletRequest request) {
        mapId = EncodeUtils.urlDecode(mapId);
        mapId = EncodeUtils.htmlUnescape(mapId);
        source = EncodeUtils.urlDecode(source);
        source = EncodeUtils.htmlUnescape(source);
        ResultMessage resultMessage = new ResultMessage();
        try {
            resultMessage.setData(mapperService.convert(source, mapId, request));
        } catch (Exception ex) {
            resultMessage.setSuccess(false);
            resultMessage.setMsg(new StringBuilder(ex.getMessage()));
            resultMessage.setData(ExceptionUtils.getFullStackTrace(ex));
        }
        return resultMessage;
    }

    @RequestMapping("test")
    @ResponseBody
    public ResultMessage test(@RequestParam(value = "mapId") String mapId, @RequestParam(value = "source") String source) {
        mapId = EncodeUtils.urlDecode(mapId);
        mapId = EncodeUtils.htmlUnescape(mapId);
        source = EncodeUtils.urlDecode(source);
        source = EncodeUtils.htmlUnescape(source);
        ResultMessage resultMessage = new ResultMessage();
        DyFormData sourceDyformData = dyFormApiFacade.getDyFormData("ddaf2274-4b7a-43b9-a39c-0e1510f2bf4d",
                "c7b773664d700001812934007ba01b5f");
        Mapper mapper = DataMapper.getInstance();
        mapId = "classa://:?beanFactory=DyformDataFactory>>classb://:?beanFactory=DyformDataFactory";

        Map<String, Object> source1 = new HashMap<String, Object>();
        source1.put("id", "mid");
        source1.put("name", 1d);
        BeanTest source2 = new BeanTest();
        source2.setBb(true);
        source2.setId("bid");
        source2.setName("bname");
        source2.setBdate(new Date());
        //
        mapId = "java.util.Map://:>>java.util.Map://:";
        System.out.println(JSONObject.fromObject(source1));
        mapper = DataMapper.getInstance();
        System.out.println("Map---->>" + JSONObject.fromObject(mapper.map(source1, Map.class, mapId)));
        //
        source2.setName(JSONObject.fromObject(sourceDyformData).toString());
        mapId = "com.wellsoft.pt.utils.mapper.test.BeanTest://:>>com.wellsoft.pt.utils.mapper.test.BeanTest://:";
        System.out.println(JSONObject.fromObject(source2));
        DataMapper.getInstance().addDataMapper(mapId, new TestMapperBuilder1());
        System.out.println("Bean---->>" + JSONObject.fromObject(mapper.map(source2, BeanTest.class, mapId)));
        //
        mapId = "com.wellsoft.pt.dyform.facade.dto.DyFormData://:?beanFactory=com.wellsoft.pt.utils.mapper.DyformDataFactory&factoryBeanId=ddaf2274-4b7a-43b9-a39c-0e1510f2bf4d>>com.wellsoft.pt.dyform.facade.dto.DyFormData://:?beanFactory=com.wellsoft.pt.utils.mapper.DyformDataFactory&factoryBeanId=ddaf2274-4b7a-43b9-a39c-0e1510f2bf4d";
        System.out.println(JSONObject.fromObject(sourceDyformData.getFormDatas()));
        DataMapper.getInstance().addDataMapper(mapId, new TestMapperBuilder2());
        System.out.println("Dyform---->>"
                + JSONObject.fromObject(mapper.map(sourceDyformData, DyFormData.class, mapId).getFormDatas()));
        return resultMessage;
    }

}
