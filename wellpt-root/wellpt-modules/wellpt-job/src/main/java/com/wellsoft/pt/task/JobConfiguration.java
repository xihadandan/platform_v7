package com.wellsoft.pt.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.scheduling.concurrent.ScheduledExecutorFactoryBean;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/7/4
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/7/4    chenq		2019/7/4		Create
 * </pre>
 */
@Configuration
public class JobConfiguration {

    private static ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${quartz.enable:true}")
    private String enableQuartz;

    @Value("${schedule.pool.size:20}")
    private int schedulePoolSize;

//    @Bean(name = "scheduler")
//    public SchedulerFactoryBean schedulerFactoryBean() {
//        SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
//
//        if (!BooleanUtils.toBoolean(enableQuartz)) {
//            logger.warn(">>> 应用容器的参数配置quartz.enable=false，不启用定时任务！<<<");
//            return factoryBean;
//        }
//        if (BooleanUtils.toBoolean(enableQuartz)) {
//            factoryBean.setApplicationContextSchedulerContextKey("applicationContextKey");
//            try {
//                //web工程编译目录下资源配置文件
//                String quartzPropertiesLocation = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
//                        + "quartz.pt.properties";
//                Resource[] resources = resourcePatternResolver.getResources(
//                        quartzPropertiesLocation);
//
//                if (resources.length == 0) {
//                    throw new FileNotFoundException("无找到任务配置属性文件quartz.pt.properties");
//                }
//                Resource targetResource = null;
//                for (Resource r : resources) {
//                    if (r.getURI().toString().indexOf("WEB-INF/classes") != -1
//                            || r.getURI().toString().indexOf("WEB-INF/lib/wellpt-job") != -1) {
//                        targetResource = r;
//                        break;
//                    }
//                }
//
//                logger.info("加载定时任务配置文件：{}", targetResource);
//                Properties quartzProperties = PropertiesLoaderUtils.loadProperties(targetResource);
//                if (System.getProperty("quartzJobStoreTablePrefix") != null) {
//                    quartzProperties.setProperty("org.quartz.jobStore.tablePrefix", System.getProperty("quartzJobStoreTablePrefix"));
//                }
//                factoryBean.setQuartzProperties(quartzProperties);
//                factoryBean.setStartupDelay(10);
//
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        } else {
//            logger.warn("定时任务未开启，请检查quartz.pt.properties的属性[quartz.enable]是否存在。true：开启 false：关闭");
//        }
//        return factoryBean;
//    }

    @Bean
    public ScheduledExecutorFactoryBean scheduledExecutorFactoryBean() {
        ScheduledExecutorFactoryBean factoryBean = new ScheduledExecutorFactoryBean();
        factoryBean.setPoolSize(schedulePoolSize);
        factoryBean.setThreadNamePrefix("定时任务执行器 - ");
//        if ("false".equals(System.getProperty("quartz.enable"))) {
//             return factoryBean;
//        }
//        if ("true".equals(enableQuartz)) {
//            ScheduledExecutorTask jobFireScheduleTask = new ScheduledExecutorTask();
//            jobFireScheduleTask.setRunnable(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        ApplicationContextHolder.getBean(JobDetailSchedulerJob.class).execute();
//                    } catch (Exception e) {
//                    }
//                }
//            });
//            jobFireScheduleTask.setPeriod(5);
//            jobFireScheduleTask.setDelay(60);
//            jobFireScheduleTask.setTimeUnit(TimeUnit.SECONDS);
//            factoryBean.setScheduledExecutorTasks(jobFireScheduleTask);
//        }
        return factoryBean;
    }


}
