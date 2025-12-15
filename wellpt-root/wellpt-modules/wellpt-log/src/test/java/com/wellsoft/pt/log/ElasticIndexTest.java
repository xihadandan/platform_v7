/*
 * @(#)2020年3月3日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.log;

import org.apache.http.client.utils.DateUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author wangrf
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年3月3日.1	wangrf		2020年3月3日		Create
 * </pre>
 * @date 2020年3月3日
 */
public class ElasticIndexTest {

    //	https://www.cnblogs.com/javasl/p/12081829.html
    @Test
    public void testDays() {
        String[] args = {"wellpt-log-2020.02.04", "wellpt-log-2020.02.05", "wellpt-log-2020.02.06",
                "wellpt-log-2020.02.07", "wellpt-log-2020.02.08", "wellpt-log-2020.02.09", "wellpt-log-2020.02.10",
                "wellpt-log-2020.02.11", "wellpt-log-2020.02.12", "wellpt-log-2020.02.13", "wellpt-log-2020.02.14",
                "wellpt-log-2020.02.15", "wellpt-log-2020.02.16", "wellpt-log-2020.02.17", "wellpt-log-2020.02.18",
                "wellpt-log-2020.02.19", "wellpt-log-2020.02.20", "wellpt-log-2020.02.21", "wellpt-log-2020.02.22",
                "wellpt-log-2020.02.23", "wellpt-log-2020.02.24", "wellpt-log-2020.02.25", "wellpt-log-2020.02.26",
                "wellpt-log-2020.02.27", "wellpt-log-2020.02.28", "wellpt-log-2020.03.03", "wellpt-log-2020.03.04",};
        List<String> list = Arrays.asList(args);
        // 先排序
        // 保存的天数
        // 当前时间向前数保留的数据
        Collections.sort(list);
        int day = 30;
        Date saveDay = new Date(new Date().getTime() - day * 3600 * 24 * 1000l);
        String saveDayStr = "wellpt-log-" + DateUtils.formatDate(saveDay, "yyyy.MM.dd");
        int saveIndex = -1;
        for (int i = 0; i < list.size(); i++) {
            // 计算出需要删除数据的下标
            String arg = list.get(i);
            if (arg.compareTo(saveDayStr) > 0) {
                saveIndex = i;
                break;
            }
        }
        if (saveIndex > 0) {
            // 删除数据
            List<String> list2 = list.subList(0, saveIndex);
            for (String s : list2) {
                System.out.println("delete=" + s);
            }
        }
    }

}
