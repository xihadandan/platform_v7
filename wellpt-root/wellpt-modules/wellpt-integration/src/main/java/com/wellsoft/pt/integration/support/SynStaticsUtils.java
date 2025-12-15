/*
 * @(#)2016年8月20日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.support;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.Separator;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
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
 * 2016年8月20日.1	zhongzh		2016年8月20日		Create
 * </pre>
 * @date 2016年8月20日
 */
public class SynStaticsUtils implements Serializable {

    public static final long DEFAULT_SLEEP_MILLIS = 1000;
    public static final String SYN_STATIC_AUTO = "syn.static.auto";
    public static final String SYN_STATIC_ARR_IN = "syn.static.arr.in";
    public static final String SYN_STATIC_ARR_OUT = "syn.static.arr.out";
    public static final String SYN_STATIC_ARR_BACK = "syn.static.arr.back";
    public static final SynStaticsUtils instance = new SynStaticsUtils();
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    // 32~64、64~128、128~256、256~512、512~1024、1024~ : 分6挡,可配置
    private long[] IN_INIT_ARR = new long[]{800, 600, 500, 400, 200, -1};
    private long[] OUT_INIT_ARR = new long[]{800, 600, 500, 400, 200, -1};
    private long[] BACK_INIT_ARR = new long[]{800, 600, 500, 400, 200, -1};
    private Boolean enable = true;
    // acl
    private long acl_in = DEFAULT_SLEEP_MILLIS;
    private long acl_out = DEFAULT_SLEEP_MILLIS;
    private long acl_back = DEFAULT_SLEEP_MILLIS;
    // msg
    private long msg_in = DEFAULT_SLEEP_MILLIS;
    private long msg_out = DEFAULT_SLEEP_MILLIS;
    private long msg_back = DEFAULT_SLEEP_MILLIS;
    // flow
    private long flow_in = DEFAULT_SLEEP_MILLIS;
    private long flow_out = DEFAULT_SLEEP_MILLIS;
    private long flow_back = DEFAULT_SLEEP_MILLIS;
    // file
    private long file_in = DEFAULT_SLEEP_MILLIS;
    private long file_out = DEFAULT_SLEEP_MILLIS;
    private long file_back = DEFAULT_SLEEP_MILLIS;
    // data
    private long data_in = DEFAULT_SLEEP_MILLIS;
    private long data_out = DEFAULT_SLEEP_MILLIS;
    private long data_back = DEFAULT_SLEEP_MILLIS;
    // feed back
    private long feed_in = DEFAULT_SLEEP_MILLIS;
    private long feed_out = DEFAULT_SLEEP_MILLIS;
    private long feed_back = DEFAULT_SLEEP_MILLIS;

    private SynStaticsUtils() {
        String auto = Config.getValue(SYN_STATIC_AUTO);
        if (StringUtils.isNotBlank(auto)) {
            enable = Boolean.valueOf(auto);
        }
        IN_INIT_ARR = getArrayValue(Config.getValue(SYN_STATIC_ARR_IN), IN_INIT_ARR);
        OUT_INIT_ARR = getArrayValue(Config.getValue(SYN_STATIC_ARR_OUT), OUT_INIT_ARR);
        BACK_INIT_ARR = getArrayValue(Config.getValue(SYN_STATIC_ARR_BACK), BACK_INIT_ARR);
    }

    public static long[] getArrayValue(String values, long[] defaultValue) {
        if (StringUtils.isNotBlank(values)) {
            String[] arr = values.split(Separator.COMMA.getValue());
            for (int i = 0; i < arr.length; i++) {
                defaultValue[i] = Long.valueOf(arr[i]);
            }
        }
        return defaultValue;
    }

    private static long getLongArr(Number number, long[] ARR) {
        // 32~64、64~128、128~256、256~512、512~1024、1024~
        int length;
        if (number == null || (length = number.intValue()) < 32) {
            return DEFAULT_SLEEP_MILLIS;
        } else if (32 <= length && length < 64) {
            return ARR[0];
        } else if (64 <= length && length < 128) {
            return ARR[1];
        } else if (128 <= length && length < 256) {
            return ARR[2];
        } else if (256 <= length && length < 512) {
            return ARR[3];
        } else if (512 <= length && length < 1024) {
            return ARR[4];
        } else if (1024 <= length) {
            return ARR[5];
        }
        return -1;// no sleep
    }

    private static long getLongArr(Number number, long[] ARR, int mutil) {
        // 32~64、64~128、128~256、256~512、512~1024、1024~
        int length;
        if (number == null || (length = number.intValue()) < 32) {
            return DEFAULT_SLEEP_MILLIS;
        } else if (32 <= length && length < 64) {
            return ARR[0];
        } else if (64 <= length && length < 128) {
            return ARR[1];
        } else if (128 <= length && length < 256) {
            return ARR[2];
        } else if (256 <= length && length < 512) {
            return ARR[3];
        } else if (512 <= length && length < 1024) {
            return ARR[4];
        } else if (1024 <= length) {
            return ARR[5];
        }
        return -1;// no sleep
    }

    public static void doUpdate(Map<String, Number> data) {
        if (data != null && instance.enable) {
            // 流入
            instance.acl_in = getLongArr(data.get("acl_in"), instance.IN_INIT_ARR);
            instance.msg_in = getLongArr(data.get("msg_in"), instance.IN_INIT_ARR);
            instance.flow_in = getLongArr(data.get("flow_in"), instance.IN_INIT_ARR);
            instance.file_in = getLongArr(data.get("file_in"), instance.IN_INIT_ARR);
            instance.data_in = getLongArr(data.get("data_in"), instance.IN_INIT_ARR);
            instance.feed_in = getLongArr(data.get("feed_in"), instance.IN_INIT_ARR);

            // 流出
            instance.acl_out = getLongArr(data.get("acl_out"), instance.OUT_INIT_ARR);
            instance.msg_out = getLongArr(data.get("msg_out"), instance.OUT_INIT_ARR);
            instance.flow_out = getLongArr(data.get("flow_out"), instance.OUT_INIT_ARR);
            instance.file_out = getLongArr(data.get("file_out"), instance.OUT_INIT_ARR);
            instance.data_out = getLongArr(data.get("data_out"), instance.OUT_INIT_ARR);
            instance.feed_out = getLongArr(data.get("feed_out"), instance.OUT_INIT_ARR);

            // 还原
            instance.acl_back = getLongArr(data.get("acl_back"), instance.BACK_INIT_ARR);
            instance.msg_back = getLongArr(data.get("msg_back"), instance.BACK_INIT_ARR);
            instance.flow_back = getLongArr(data.get("flow_back"), instance.BACK_INIT_ARR);
            instance.file_back = getLongArr(data.get("file_back"), instance.BACK_INIT_ARR);
            instance.data_back = getLongArr(data.get("data_back"), instance.BACK_INIT_ARR);
            instance.feed_back = data.get("feed_back") == null ? 0 : data.get("feed_back").intValue();

        }
    }

    public static void doReset() {
        // 流入
        instance.acl_in = DEFAULT_SLEEP_MILLIS;
        instance.msg_in = DEFAULT_SLEEP_MILLIS;
        instance.flow_in = DEFAULT_SLEEP_MILLIS;
        instance.file_in = DEFAULT_SLEEP_MILLIS;
        instance.data_in = DEFAULT_SLEEP_MILLIS;
        instance.feed_in = DEFAULT_SLEEP_MILLIS;

        // 流出
        instance.acl_out = DEFAULT_SLEEP_MILLIS;
        instance.msg_out = DEFAULT_SLEEP_MILLIS;
        instance.flow_out = DEFAULT_SLEEP_MILLIS;
        instance.file_out = DEFAULT_SLEEP_MILLIS;
        instance.data_out = DEFAULT_SLEEP_MILLIS;
        instance.feed_out = DEFAULT_SLEEP_MILLIS;

        // 还原
        instance.acl_back = DEFAULT_SLEEP_MILLIS;
        instance.msg_back = DEFAULT_SLEEP_MILLIS;
        instance.flow_back = DEFAULT_SLEEP_MILLIS;
        instance.file_back = DEFAULT_SLEEP_MILLIS;
        instance.data_back = DEFAULT_SLEEP_MILLIS;
        instance.feed_back = DEFAULT_SLEEP_MILLIS;
    }

    public static final void doSleep(long millis) throws InterruptedException {
        if (millis >= 0) {
            Thread.sleep(millis);
        }
    }

    /**
     * 检测同步是否有很多未反馈数据
     *
     * @param interval 阀值
     * @return
     */
    public static final boolean checkUnimasOk(int interval) {
        return instance.feed_back < interval;
    }

    public static final int getIntValue(Number number) {
        return number == null ? 0 : number.intValue();
    }

    public static final long getLongValue(Number number) {
        return number == null ? 0 : number.longValue();
    }

    /**
     * @return the enable
     */
    public final Boolean getEnable() {
        return enable;
    }

    /**
     * @return the acl_in
     */
    public final long getAcl_in() {
        return acl_in;
    }

    /**
     * @return the acl_out
     */
    public final long getAcl_out() {
        return acl_out;
    }

    /**
     * @return the acl_back
     */
    public final long getAcl_back() {
        return acl_back;
    }

    /**
     * @return the msg_in
     */
    public final long getMsg_in() {
        return msg_in;
    }

    /**
     * @return the msg_out
     */
    public final long getMsg_out() {
        return msg_out;
    }

    /**
     * @return the msg_back
     */
    public final long getMsg_back() {
        return msg_back;
    }

    /**
     * @return the flow_in
     */
    public final long getFlow_in() {
        return flow_in;
    }

    /**
     * @return the flow_out
     */
    public final long getFlow_out() {
        return flow_out;
    }

    /**
     * @return the flow_back
     */
    public final long getFlow_back() {
        return flow_back;
    }

    /**
     * @return the file_in
     */
    public final long getFile_in() {
        return file_in;
    }

    /**
     * @return the file_out
     */
    public final long getFile_out() {
        return file_out;
    }

    /**
     * @return the file_back
     */
    public final long getFile_back() {
        return file_back;
    }

    /**
     * @return the data_in
     */
    public final long getData_in() {
        return data_in;
    }

    /**
     * @return the data_out
     */
    public final long getData_out() {
        return data_out;
    }

    /**
     * @return the data_back
     */
    public final long getData_back() {
        return data_back;
    }

    /**
     * @return the feed_in
     */
    public final long getFeed_in() {
        return feed_in;
    }

    /**
     * @return the feed_out
     */
    public final long getFeed_out() {
        return feed_out;
    }

    /**
     * @return the feed_back
     */
    public final long getFeed_back() {
        return feed_back;
    }

}
