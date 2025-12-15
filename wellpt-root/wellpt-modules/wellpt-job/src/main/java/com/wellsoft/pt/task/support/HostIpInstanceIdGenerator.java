package com.wellsoft.pt.task.support;

import com.wellsoft.context.util.NetUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.quartz.SchedulerException;
import org.quartz.simpl.SimpleInstanceIdGenerator;
import org.quartz.spi.InstanceIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Description: quartz实例id生成器
 *
 * @author chenq
 * @date 2018/7/12
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/7/12    chenq		2018/7/12		Create
 * </pre>
 */
public class HostIpInstanceIdGenerator implements InstanceIdGenerator {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String generateInstanceId() throws SchedulerException {
        try {
            return NetUtils.getLocalHost() + "/" + NetUtils.getLocalAddress() + ":" + NetUtils.getServerPort() + "/" + DateFormatUtils.format(
                    new Date(), "yyyyMMddHHmmssSSS");
        } catch (Exception e) {
            logger.warn("quartz实例id创建异常：", e);
        }
        return new SimpleInstanceIdGenerator().generateInstanceId();
    }


}
