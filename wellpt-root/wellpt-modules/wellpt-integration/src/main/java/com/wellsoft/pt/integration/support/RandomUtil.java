/*
 * @(#)2014-3-25 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.support;

import java.util.Random;

/**
 * Description: 如何描述该类
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-3-25.1	Administrator		2014-3-25		Create
 * </pre>
 * @date 2014-3-25
 */
public class RandomUtil {
    public static String getRandomNumber(int num) {
        Random random = new Random();
        String result = "";
        for (int i = 0; i < num; i++) {
            result += random.nextInt(10);
        }
        return result;
    }
}
