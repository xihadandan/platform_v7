package com.wellsoft.pt.basicdata.datastore.service;

import com.wellsoft.pt.basicdata.datastore.dao.DbLinkConfigDao;
import com.wellsoft.pt.basicdata.datastore.entity.DbLinkConfigEntity;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.jpa.service.JpaService;
import org.hibernate.SessionFactory;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年12月10日   chenq	 Create
 * </pre>
 */
public interface DbLinkConfigService extends JpaService<DbLinkConfigEntity, DbLinkConfigDao, Long> {

    DbLinkConfigEntity saveDbLinkConfig(DbLinkConfigEntity entity);

    /**
     * 获取切换后的数据源，如果有切换则返回切换后的数据源，没有则返回原来的数据源
     *
     * @param currentNativeDao 原来数据源dao
     * @return
     */
    NativeDao getSwitchNativeDao(NativeDao currentNativeDao);

    SessionFactory createLocalSessionFactory(Long dbLinkConfUuid);

    /**
     * 构建数据源
     *
     * @param dbLinkConfUuid 连接uuid
     * @return
     */
    DataSource buildSQLDataSource(Long dbLinkConfUuid);

    List<DbLinkConfigEntity> getDbLinksBySystem(String system);

    DatabaseMetaData testConnection(DbLinkConfigEntity entity);
}
