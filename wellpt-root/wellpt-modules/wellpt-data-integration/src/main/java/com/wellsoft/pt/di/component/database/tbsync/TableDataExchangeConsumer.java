package com.wellsoft.pt.di.component.database.tbsync;

import com.google.common.collect.Lists;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.di.component.AbstractScheduledPollConsumer;
import com.wellsoft.pt.di.entity.DiTableDataChangeEntity;
import com.wellsoft.pt.di.service.DiTableDataChangeService;
import com.wellsoft.pt.integration.trigger.service.TriggerService;
import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ClassUtils;

import javax.activation.DataHandler;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/23
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/23    chenq		2019/8/23		Create
 * </pre>
 */
public class TableDataExchangeConsumer extends
        AbstractScheduledPollConsumer<TableDataExchangeEndpoint> {
    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    public TableDataExchangeConsumer(
            TableDataExchangeEndpoint endpoint, Processor processor) {
        super(endpoint, processor);

        try {
            //TODO: 触发器表的数据区别是还原数据操作还是正常的业务操作
            //创建相关表的触发器，对表的数据变更进行记录
            if (StringUtils.isNotBlank(super.endpoint.getTableName())) {
                String ftlString = getTriggerFtl("table_data_change_trigger_create.ftl");
                String[] tables = super.endpoint.getTableName().split(",|;");
                for (String t : tables) {
                    ApplicationContextHolder.getBean(TriggerService.class).generateCreateTrigger(
                            t, ftlString);
                }
            }

        } catch (Exception e) {
            logger.error("数据变更记录创建触发器异常：", e);
            throw new RuntimeException(e);
        }
    }


    @Override
    protected Object body() {
        //获取更新数据推到前置机消费端
        PagingInfo pagingInfo = new PagingInfo(0, super.endpoint.getLimit(), false);

        List<DiTableDataChangeEntity> list = ApplicationContextHolder.getBean(
                DiTableDataChangeService.class).queryNoSyncTableDataChanges(
                Lists.newArrayList(super.endpoint.getTableName().split(",|;")),
                pagingInfo);
        return list.isEmpty() ? null : list;
    }

    @Override
    protected Map<String, Object> headers() {
        return null;
    }

    @Override
    public Map<String, Object> properties() {
        return null;
    }

    @Override
    protected Map<String, DataHandler> attachments() {
        return null;
    }

    private String getTriggerFtl(String triggerFtl) {
        try {
            String basePackage = Config.BASE_PACKAGES;
            String ftlPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                    + ClassUtils.convertClassNameToResourcePath(basePackage) + "/**/*" + triggerFtl;
            Resource[] resources = resourcePatternResolver.getResources(ftlPath);
            return IOUtils.toString(resources[0].getInputStream());
        } catch (Exception e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
