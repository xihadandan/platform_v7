/*
 * @(#)2016-03-11 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.fdext.web;

import com.wellsoft.context.util.encode.JsonBinder;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.common.fdext.bean.CdFieldExtensionValue;
import com.wellsoft.pt.common.fdext.facade.service.CdFieldFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-14.1	zhongzh		2016-03-11		Create
 * </pre>
 * @date 2016-03-11
 */
@Controller
@RequestMapping(value = "/cd/field/ext/value")
public class CdFieldExtValueMgrController extends com.wellsoft.context.web.controller.BaseFormDataController {

    @Autowired
    private CdFieldFacade cdFieldFacade;

    /**
     * 保存扩展字段的值
     *
     * @param jsonData
     * @param groupCode
     * @param dataUuid
     * @return dataUuid
     */
    @RequestMapping(value = "/saveData", method = RequestMethod.POST)
    public ResponseEntity<ResultMessage> saveData(@RequestParam("jsonData") String jsonData,
                                                  @RequestParam("groupCode") String groupCode, @RequestParam("dataUuid") String dataUuid) {
        CdFieldExtensionValue bean = JsonBinder.buildNormalBinder().fromJson(jsonData, CdFieldExtensionValue.class);
        cdFieldFacade.saveData(dataUuid, groupCode, bean);
        return getSucessfulResultMsg(dataUuid);
    }

    /**
     * 如何描述该方法
     *
     * @param groupCode
     * @param dataUuid
     * @return CdFieldExtensionValue
     */
    @RequestMapping(value = "/loadData")
    public ResponseEntity<ResultMessage> loadData(@RequestParam("groupCode") String groupCode,
                                                  @RequestParam("dataUuid") String dataUuid) {
        return getSucessfulResultMsg(cdFieldFacade.getData(dataUuid, groupCode));
    }

    /**
     * 打开列表界面
     *
     * @return
     */
    @RequestMapping(value = "/list")
    public String cdFieldExtValueList() {
        return forward("/fdext/cd_field_ext_value_list");
    }

}
