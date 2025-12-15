package com.wellsoft.pt.basicdata.datastore.listener;

import com.wellsoft.pt.basicdata.datastore.entity.DbLinkConfigEntity;
import com.wellsoft.pt.basicdata.datastore.service.DbLinkConfigService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年12月17日   chenq	 Create
 * </pre>
 */
@Component
public class DbLinkSessionFactoryInitListener implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    DbLinkConfigService dbLinkConfigService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        List<DbLinkConfigEntity> list = dbLinkConfigService.listAll();
        StopWatch watch = new StopWatch("DbLinkSessionFactoryInitListener");
        watch.start("初始化创建sessionFactory");
        if (CollectionUtils.isNotEmpty(list)) {
            for (DbLinkConfigEntity entity : list) {
//                FIXME: 是否启动时候初始化
//                dbLinkConfigService.createLocalSessionFactory(entity.getUuid());
             }
        }
        watch.stop();
        logger.info("{}", watch.prettyPrint());
    }
}
