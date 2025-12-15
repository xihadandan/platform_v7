package com.wellsoft.distributedlog;

/**
 * Description: 出来定义类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2021年06月30日   chenq	 Create
 * </pre>
 */
public class Constants {

    public static final String LOG_REDIS_KEY = "wellpt_distributedlog";

    public static final String ES_LOG_INDEX_NAME_PREFIX = "log-";

    public static final String ROCKETMQ_TOPIC = "wellpt_distributedlog";

    public static final String ROCKETMQ_PRODUCER_GROUP = "distributedlog_producer_group";

    public static final String ROCKETMQ_TAGS = "log";

    public static final String LOG_INDEX_DATE_FORMATE = "yyyyMMdd";


}
