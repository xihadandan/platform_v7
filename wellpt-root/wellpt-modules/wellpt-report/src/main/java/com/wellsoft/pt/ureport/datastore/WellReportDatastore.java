package com.wellsoft.pt.ureport.datastore;

import com.bstek.ureport.definition.datasource.BuildinDatasource;
import com.wellsoft.context.util.ApplicationContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Description: 平台报表数据库数据源
 *
 * @author chenq
 * @date 2018/9/25
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/9/25    chenq		2018/9/25		Create
 * </pre>
 */
@Component
public class WellReportDatastore implements BuildinDatasource {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String name() {
        return "平台报表数据库";
    }

    @Override
    public Connection getConnection() {
        try {
            return ((DataSource) ApplicationContextHolder.getBean(
                    "reportDataSource")).getConnection();
        } catch (Exception e) {
            logger.error("报表工具数据源连接异常：", e);
        }

        return null;
    }
}
