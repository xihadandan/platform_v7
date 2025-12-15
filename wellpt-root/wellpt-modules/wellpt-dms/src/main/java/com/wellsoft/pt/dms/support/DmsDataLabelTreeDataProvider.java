/*
 * @(#)2018年2月27日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.support;

import org.springframework.stereotype.Component;

/**
 * Description: 数据标签树
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年2月27日.1	chenqiong		2018年2月27日		Create
 * </pre>
 * @date 2018年2月27日
 */
@Component
public class DmsDataLabelTreeDataProvider extends AbstractDataLabelTreeDataProvider {


    @Override
    public String getName() {
        return "平台应用_我的标签数据树";
    }

    @Override
    String getModuleId() {
        return "PT";
    }

    @Override
    String getRootTreeNodeName() {
        return "我的标签";
    }


}
