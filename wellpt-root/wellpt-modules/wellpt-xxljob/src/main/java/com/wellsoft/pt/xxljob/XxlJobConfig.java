package com.wellsoft.pt.xxljob;

import com.wellsoft.context.config.Config;
import com.wellsoft.pt.xxljob.autoconf.XxlJobExecutorCondition;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import com.xxl.job.core.util.IpUtil;
import com.xxl.job.core.well.WellXxlJobBiz;
import com.xxl.job.core.well.client.WellXxlJobBizClient;
import com.xxl.job.core.well.model.req.JobGroupReqParam;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class XxlJobConfig {

    private Logger logger = LoggerFactory.getLogger(XxlJobConfig.class);


    @Value("${xxl.job.admin.addresses}")
    private String adminAddresses;

    @Value("${xxl.job.accessToken}")
    private String accessToken;

    @Value("${xxl.job.executor.appname}")
    private String appname;

    @Value("${xxl.job.executor.title}")
    private String title;

    @Value("${xxl.job.executor.address}")
    private String address;

    @Value("${xxl.job.executor.ip}")
    private String ip;

    @Value("${xxl.job.executor.port}")
    private int port;

    @Value("${xxl.job.executor.logpath}")
    private String logPath;

    @Value("${xxl.job.executor.logretentiondays}")
    private int logRetentionDays;

    public static boolean isStart() {
        String sysStart = System.getProperty("xxl.job.start");
        if (sysStart != null) {
            return sysStart.equalsIgnoreCase("true");
        }
        return Config.getValue("xxl.job.start", "").equalsIgnoreCase("true");
    }


    @Bean
    @Conditional(XxlJobExecutorCondition.class)
    public XxlJobSpringExecutor xxlJobExecutor() {
        logger.info(">>>>>>>>>>> xxl-job config init.");
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(adminAddresses);
        xxlJobSpringExecutor.setAppname(appname);
        xxlJobSpringExecutor.setAddress(address);
        xxlJobSpringExecutor.setIp(ip);
        xxlJobSpringExecutor.setPort(port);
        xxlJobSpringExecutor.setAccessToken(accessToken);
        xxlJobSpringExecutor.setLogPath(logPath);
        xxlJobSpringExecutor.setLogRetentionDays(logRetentionDays);
        return xxlJobSpringExecutor;
    }

    @Bean
    public WellXxlJobBiz wellXxlJobBiz() {
        logger.info(">>>>>>>>>>> xxl-job wellXxlJobBiz init.");
        WellXxlJobBizClient xxlJobInfoBiz = new WellXxlJobBizClient();
        xxlJobInfoBiz.setAccessToken(accessToken);
        xxlJobInfoBiz.setAdminAddresses(adminAddresses);

        String addr = null;
        if (StringUtils.isNotBlank(address)) {
            addr = address.trim();
        } else if (StringUtils.isNotBlank(ip)) {
            String ip_port_address = IpUtil.getIpPort(ip, port);   // registry-address：default use address to registry , otherwise use ip:port if address is null
            ip_port_address = "http://{ip_port}/".replace("{ip_port}", ip_port_address);
            addr = ip_port_address;
        }
        JobGroupReqParam jobGroupReqParam = new JobGroupReqParam();
        jobGroupReqParam.setStart(isStart()).setAppname(appname).setTitle(title);
        if (addr != null) {
            jobGroupReqParam.setAddressList(addr).setAddressType(1);
        }
        xxlJobInfoBiz.setJobGroupReqParam(jobGroupReqParam);
        return xxlJobInfoBiz;
    }

}
