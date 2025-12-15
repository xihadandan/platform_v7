/*
 * @(#)2016年5月17日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.handler;

import com.google.common.collect.Lists;
import com.wellsoft.pt.integration.enumcode.XzxkBusinessStatus;
import com.wellsoft.pt.integration.security.ExchangeConfig;

import java.util.List;

/**
 * 行政许可业务数据处理器
 *
 * @author {wangdj}
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年5月17日.1	{wangdj}		2016年5月17日		Create
 * </pre>
 * @date 2016年5月17日
 */
public class ExchangeXzxkBusinessDataProcessor {

    private List<String> xzxkList;

    public ExchangeXzxkBusinessDataProcessor() {
        createList();
    }

    /**
     * 创建行政许可阶段配置ID列表
     * 列表添加顺序 请按照XzxkBusinessStatus枚举的value升序
     *
     * @return
     */
    private void createList() {
        xzxkList = Lists.newArrayList();
        xzxkList.add(ExchangeConfig.TYPE_ID_SSXX_XZXK_XK);
        xzxkList.add(ExchangeConfig.TYPE_ID_SSXX_XZXK_YX);
        xzxkList.add(ExchangeConfig.TYPE_ID_SSXX_XZXK_BG);
        xzxkList.add(ExchangeConfig.TYPE_ID_SSXX_XZXK_FH);
        xzxkList.add(ExchangeConfig.TYPE_ID_SSXX_XZXK_YSBB);
        xzxkList.add(ExchangeConfig.TYPE_ID_SSXX_XZXK_ZX);
    }

    /**
     * 是否包含数据类型Id
     *
     * @param dataTypeId
     * @return
     */
    public boolean isContainDataTypeId(String dataTypeId) {
        return xzxkList.contains(dataTypeId);
    }

    /**
     * 根据数据类型id获取业务状态
     *
     * @param dataTypeId
     * @return
     */
    public XzxkBusinessStatus getBusinessStatus(String dataTypeId) {
        Integer index = null;
        for (int i = 0; i < xzxkList.size(); i++) {
            String type_id = xzxkList.get(i);
            if (type_id.equals(dataTypeId)) {
                index = i;
                break;
            }
        }
        if (index != null) {
            return XzxkBusinessStatus.getType(index);
        }
        return null;
    }
}
