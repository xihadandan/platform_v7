/*
 * @(#)2021年1月19日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.rocketmq.support;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.util.NetUtils;
import org.apache.commons.codec.binary.Hex;
import org.apache.rocketmq.client.ClientConfig;
import org.apache.rocketmq.remoting.common.RemotingUtil;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年1月19日.1	zhongzh		2021年1月19日		Create
 * </pre>
 * @date 2021年1月19日
 */
public abstract class MqUtils {

    private static String clientIp = RemotingUtil.getLocalAddress();

    private static int serverPort = NetUtils.getServerPort();

    private static boolean ownClustering = new Boolean(Config.getValue("rocketmq.ownClustering"));

    private static String idPart = (clientIp + ":" + serverPort);

    private static String idPart2 = new String(Hex.encodeHex(idPart.getBytes(Hex.DEFAULT_CHARSET), false));

    public static void setNamespace(ClientConfig clientConfig) {
        if (ownClustering) {
            clientConfig.setNamespace(idPart2);
        }
    }

    public static String getTopic(String topic) {
        if (ownClustering) {
            StringBuilder sb = new StringBuilder();
            sb.append(topic);
            sb.append("|");
            sb.append(idPart2);
            return sb.toString();
        } else {
            return topic;
        }
    }

    public static String getTags(String tags) {
        //		if (ownClustering) {
        //			StringBuilder sb = new StringBuilder();
        //			sb.append(tags);
        //			sb.append("://");
        //			sb.append(idPart);
        //			return sb.toString();
        //		} else {
        return tags;
        //		}
    }

    public static String getConsumerGroup(String consumerGroup) {
        if (ownClustering) {
            StringBuilder sb = new StringBuilder();
            sb.append(consumerGroup);
            sb.append("|");
            sb.append(idPart2);
            return sb.toString();
        } else {
            return consumerGroup;
        }
    }
}
