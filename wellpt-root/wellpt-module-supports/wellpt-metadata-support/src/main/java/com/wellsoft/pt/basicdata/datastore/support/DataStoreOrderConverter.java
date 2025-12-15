/*
 * @(#)2019年12月5日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.support;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreOrder;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年12月5日.1	zhulh		2019年12月5日		Create
 * </pre>
 * @date 2019年12月5日
 */
public class DataStoreOrderConverter {

    /**
     *
     */
    private DataStoreOrderConverter() {
    }

    /**
     * @param orderString
     * @return
     */
    public static List<DataStoreOrder> covertOrders(String orderString) {
        List<DataStoreOrder> orders = Lists.newArrayList();
        String[] orderParts = StringUtils.split(orderString, Separator.COMMA.getValue());
        for (String orderPart : orderParts) {
            String[] orderInfos = StringUtils.split(orderPart, Separator.SPACE.getValue());
            if (orderInfos.length == 1) {
                orders.add(new DataStoreOrder(orderInfos[0], true));
            } else if (orderInfos.length == 2) {
                orders.add(new DataStoreOrder(orderInfos[0], StringUtils.equalsIgnoreCase(DataStoreOrder.ASC,
                        orderInfos[1])));
            } else {
                throw new RuntimeException("无效的排序：" + orderString);
            }
        }
        return orders;
    }

}
