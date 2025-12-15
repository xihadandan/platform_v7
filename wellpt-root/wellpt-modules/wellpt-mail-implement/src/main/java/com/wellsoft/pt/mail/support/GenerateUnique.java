package com.wellsoft.pt.mail.support;

/**
 * Description: 获得唯一KEY值类
 *
 * @author wuzq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-1	wuzq		2013-2-7		Create
 * </pre>
 * @date 2013-3-1
 */
public class GenerateUnique {
    private static long key = System.currentTimeMillis();

    private GenerateUnique() {
    }

    public static synchronized long getKey() {
        return key++;
    }


}
