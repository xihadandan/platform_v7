package com.wellsoft.pt.di.component.frontdb;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.di.component.AbstractProducer;
import com.wellsoft.pt.di.entity.DiTableColumnDataChangeEntity;
import com.wellsoft.pt.di.entity.DiTableDataChangeEntity;
import com.wellsoft.pt.di.service.DiTableDataChangeService;
import com.wellsoft.pt.di.util.CamelContextUtils;
import com.wellsoft.pt.integration.security.ExchangeConfig;
import com.wellsoft.pt.jpa.dao.SessionOperationHibernateDao;
import com.wellsoft.pt.jpa.hibernate4.DynamicHibernateSessionFactoryRegistry;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.component.file.remote.RemoteFile;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.xerial.snappy.SnappyInputStream;

import javax.activation.DataHandler;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/30
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/30    chenq		2019/8/30		Create
 * </pre>
 */
public class FrontDbDataResotreProducer extends AbstractProducer<FrontDbDataResotreEndpoint> {
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    SessionOperationHibernateDao dao;
    String sessionFId;

    public FrontDbDataResotreProducer(FrontDbDataResotreEndpoint endpoint) {
        super(endpoint);

        Map<String, Object> dsProperties = Maps.newHashMap();
        dsProperties.put("allowLocalTransactions", true);
        dsProperties.put("ignnoreEntity", true);
        dsProperties.put("ignnoreMapping", true);
        String dataSourceId = DynamicHibernateSessionFactoryRegistry.buildDataSource(
                ApplicationContextHolder.defaultListableBeanFactory(),
                endpoint.getJdbcUrl(), endpoint.getUser(), endpoint.getPassword(), dsProperties);
        NamedParameterJdbcDaoSupport daoSupport = new NamedParameterJdbcDaoSupport();
        daoSupport.setDataSource((DataSource) ApplicationContextHolder.getBean(dataSourceId));
        namedParameterJdbcTemplate = daoSupport.getNamedParameterJdbcTemplate();

        //前置库数据源
        Map<String, Object> jdbcProperties = Maps.newHashMap();
        jdbcProperties.put("url", endpoint.getJdbcUrl());
        jdbcProperties.put("username", endpoint.getUser());
        jdbcProperties.put("password", endpoint.getPassword());
        sessionFId = DynamicHibernateSessionFactoryRegistry.registry(
                ApplicationContextHolder.defaultListableBeanFactory(), jdbcProperties);
        dao = (SessionOperationHibernateDao) ApplicationContextHolder.getBean(
                "DynamicDAO_" + sessionFId);
    }

    @Override
    protected void action(Object body, Map<String, Object> headers, Map<String, Object> properties,
                          Map<String, DataHandler> attachments) throws Exception {
        if (!(body instanceof List)) {
            //非数据集合
            return;
        }
        List<String> list = (List<String>) body;//前置库待还原数据的UUID集合
//        SessionOperationHibernateDao userDbDao = dao.getDao("sessionFactoryTest");
        SessionOperationHibernateDao userDbDao = dao;
        //数据反馈
        boolean isSyncFeedback = endpoint.getSyncType().equals(2);
        if (isSyncFeedback) {
            syncFeedbackUpdate(list);
            return;
        }

        //数据同步
        Map<String, Object> lockParams = Maps.newHashMap();
        TransactionStatus ts = null;
        org.springframework.orm.hibernate4.HibernateTransactionManager transactionManager = ApplicationContextHolder.getBean(
                DynamicHibernateSessionFactoryRegistry.DYNAMIC_TRANSACTIONMANAGER_PREFIX + sessionFId, org.springframework.orm.hibernate4.HibernateTransactionManager.class);
        for (String uid : list) {
            lockParams.put("uuid", uid);
            try {
                //处理中
                if (lockDataAsSyncing(uid, true) > 0) {
                    List<DiTableDataChangeEntity> dataList = null;
                    //还原数据
                    List<String> clobStrings = dao.queryClobAsStrings(
                            "select encrypted_data from front_db_data where uuid=:uuid",
                            lockParams);
                    String clob = clobStrings.get(0);
                    if (StringUtils.isBlank(clob)) {
                        dao.update(
                                "update front_db_data set data_status=2,sync_time=sysdate where uuid=:uuid and data_status=1",
                                lockParams);//同步还原成功
                        continue;
                    }
                    Object dencryptedObject = endpoint.dencryptedStringAsObject(clob);
                    if (dencryptedObject instanceof String
                            && dencryptedObject.toString().startsWith("file_data_size=")) {
                        String[] parts = clob.split(",");
                        String fileName = (parts[1].split("="))[1];
                        ConsumerTemplate consumer = CamelContextUtils.consumer();
                        Exchange ex = consumer.receive(
                                String.format(
                                        "ftp://%s?charset=UTF-8&delete=true&username=%s&password=%s" +
                                                "&fileName=%s" +
                                                "&localWorkDirectory=" + System.getProperty(
                                                "java.io.tmpdir") + "/camel_ftp_tmp",
                                        super.endpoint.getFtpAddress(), endpoint.getFtpUser(),
                                        endpoint.getFtpPassword(), fileName),
                                10000L);
                        if (ex == null) {//未获取到ftp上的文件数据
                            lockDataAsSyncing(uid, false);
                            continue;
                        }
                        RemoteFile remoteFile = (RemoteFile) ex.getIn().getBody();
                        File localFile = (File) remoteFile.getBody();//被下载到临时目录下
                        Object data = endpoint.ungzipFile2Object(localFile);
                        if (data != null) {
                            dataList = (List<DiTableDataChangeEntity>) data;
                        }
                        consumer.doneUoW(ex);
                        localFile.delete();//删除临时文件
                    } else if (dencryptedObject instanceof List) {
                        dataList = (List<DiTableDataChangeEntity>) dencryptedObject;
                    }


                    if (dataList != null) {
                        //还原数据
                        int row = 0;
                        TransactionDefinition td = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                        ts = transactionManager.getTransaction(td);
                        for (DiTableDataChangeEntity tableData : dataList) {

                            if (tableData.getTableName().equalsIgnoreCase(
                                    ExchangeConfig.FILETABLE)) {
                                updateTableDataMongoDBAttachment(tableData);
                            }
                            if (tableData.getAction().equalsIgnoreCase("insert")) {
                                row = insert(userDbDao, tableData);
                            } else if (tableData.getAction().equalsIgnoreCase("update")) {
                                row = update(userDbDao, tableData);
                            } else if (tableData.getAction().equalsIgnoreCase("delete")) {
                                row = delete(userDbDao, tableData);
                            }


                            if (row > 0) {  // 还原成功 -> 反馈
                                dao.update(
                                        "update front_db_data set data_status=2,sync_time=sysdate where uuid=:uuid and data_status=1",
                                        lockParams);//同步还原成功
                            }
                        }
                        transactionManager.commit(ts);


                    }

                }

            } catch (Exception e) {
                logger.error("前置库数据[uuid={}]还原执行异常：", uid, e);
                if (ts != null) {
                    try {
                        transactionManager.rollback(ts);
                    } catch (Exception te) {
                    }
                }
                lockDataAsSyncing(uid, false);//执行失败需要解锁
            } finally {

            }

        }


    }

    private int delete(SessionOperationHibernateDao userDbDao, DiTableDataChangeEntity tableData) {
        List<DiTableColumnDataChangeEntity> columnDataList = tableData.getColumnDataChangeEntities();
        StringBuilder update = new StringBuilder("delete from ").append(tableData.getTableName());
        Map<String, Object> columnNamedParams = Maps.newHashMap();
        if (StringUtils.isNotBlank(tableData.getPkColName())) {
            String[] pkColumns = tableData.getPkColName().split(";");
            String[] pkValues = tableData.getPkUuid().split(";");
            update.append(" where 1=1 ");
            for (int i = 0; i < pkColumns.length; i++) {
                update.append(" and ").append(pkColumns[i]).append(" =:").append(
                        "__" + pkColumns[i]);
                columnNamedParams.put("__" + pkColumns[i], pkValues[i]);
            }
        }
        if (!columnNamedParams.isEmpty()) {
            return dao.delete(update.toString(), columnNamedParams);
        }
        return 0;
    }

    public void syncFeedbackUpdate(List<String> frontDbUuids) {
//        SessionOperationHibernateDao userDbDao = dao.getDao("sessionFactoryTest");
        DiTableDataChangeService tableDataChangeService = ApplicationContextHolder.getBean(
                DiTableDataChangeService.class);
        Map<String, Object> lockParams = Maps.newHashMap();

        org.springframework.orm.hibernate4.HibernateTransactionManager transactionManager = ApplicationContextHolder.getBean(
                DynamicHibernateSessionFactoryRegistry.DYNAMIC_TRANSACTIONMANAGER_PREFIX + sessionFId, org.springframework.orm.hibernate4.HibernateTransactionManager.class);
        for (String uuid : frontDbUuids) {
            lockParams.put("uuid", uuid);
            TransactionStatus ts = null;
            try {
                DefaultTransactionDefinition td = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED);
                ts = transactionManager.getTransaction(td);
                List<String> clobStrings = dao.queryClobAsStrings(
                        "select encrypted_data from front_db_data where uuid=:uuid",
                        lockParams);
                String clob = clobStrings.get(0);
                List<String> tableDataUuids = Lists.newArrayList();
                if (clob.startsWith("file_data_size=")) {
                    String[] parts = clob.split(",");
                    tableDataUuids = Lists.newArrayList(((parts[2].split("="))[1]).split("@"));
                    //TODO:删除ftp上的附件
                } else {
                    List<DiTableDataChangeEntity> dataList = (List<DiTableDataChangeEntity>) endpoint.dencryptedStringAsObject(
                            clobStrings.get(0));
                    for (DiTableDataChangeEntity entity : dataList) {
                        tableDataUuids.add(entity.getUuid());
                    }
                }

                //更新[同步中 -> 同步完成]
                tableDataChangeService.updateStatus2Status(1, 2, tableDataUuids);
                // 已反馈

                dao.update("update front_db_data set data_status=3 where uuid=:uuid", lockParams);
                transactionManager.commit(ts);
            } catch (Exception e) {
                logger.error("反馈同步数据更新异常，uuid={} ", uuid, e);
                if (ts != null) {
                    try {
                        transactionManager.rollback(ts);
                    } catch (Exception te) {
                    }
                }
            }

        }
    }

    /**
     * 文件需要重新上传到MongoDB，并更新最新的文件ID
     *
     * @param tableData
     */
    private void updateTableDataMongoDBAttachment(DiTableDataChangeEntity tableData) {
        ConsumerTemplate consumer = CamelContextUtils.consumer();
        Exchange ex = consumer.receive(
                String.format(
                        "ftp://%s?binary=true&charset=UTF-8&delete=true" +
                                "&username=%s&password=%s" +
                                "&fileName=%s" +
                                "&localWorkDirectory=" + System.getProperty(
                                "java.io.tmpdir") + "/camel_ftp_tmp",
                        super.endpoint.getFtpAddress(), endpoint.getFtpUser(),
                        endpoint.getFtpPassword(), tableData.getPkUuid() + ".zip"),
                10000L);
        if (ex == null) {//未获取到ftp上的文件数据
            return;
        }

        RemoteFile body = (RemoteFile) ex.getIn().getBody();
        File localFile = (File) body.getBody();//被下载到临时目录下
        FileInputStream fileInputStream = null;
        SnappyInputStream snappyInputStream = null;
        try {
            String fileID = "";
            String contentType = "";
            String fileName = "";
            List<DiTableColumnDataChangeEntity> columnDataList = tableData.getColumnDataChangeEntities();
            int index = -1;
            for (int i = 0; i < columnDataList.size(); i++) {
                DiTableColumnDataChangeEntity column = columnDataList.get(i);
                if (column.getColumnName().equalsIgnoreCase("PHYSICAL_FILE_ID")) {
                    fileID = StringUtils.defaultString(column.getDataTextValue(), "");
                    index = i;
                } else if (column.getColumnName().equalsIgnoreCase("FILE_NAME")) {
                    fileName = StringUtils.defaultString(column.getDataTextValue(), "");
                } else if (column.getColumnName().equalsIgnoreCase("CONTENT_TYPE")) {
                    contentType = StringUtils.defaultString(column.getDataTextValue(), "");
                }
            }
            fileInputStream = new FileInputStream(localFile);
            snappyInputStream = new SnappyInputStream(fileInputStream);
            MongoFileService mongoFileService = ApplicationContextHolder.getBean(
                    MongoFileService.class);
            MongoFileEntity mongoFileEntity = mongoFileService.savePhysicalFileWithoutVerifyMD5(
                    Config.DEFAULT_TENANT, fileID, fileName, contentType,
                    snappyInputStream);
            if (mongoFileEntity != null && StringUtils.isNotBlank(
                    mongoFileEntity.getPhysicalID()) && index != -1) {
                columnDataList.get(index).setDataTextValue(
                        mongoFileEntity.getPhysicalID());//替换为最新的MongoDB的文件id
            }
        } catch (Exception e) {
            throw new RuntimeException("解压缩文件[" + localFile.getName() + "]异常：", e);
        } finally {
        }
        consumer.doneUoW(ex);//ftp消费结束也会自动删除本地临时文件

    }

    private int insert(SessionOperationHibernateDao dao, DiTableDataChangeEntity tableData) {
        List<DiTableColumnDataChangeEntity> columnDataList = tableData.getColumnDataChangeEntities();
        StringBuilder insert = new StringBuilder("insert into ").append(tableData.getTableName());
        String[] columns = new String[columnDataList.size() + 1];
        int i = 0;
        Map<String, Object> columnNamedParams = Maps.newHashMap();
        for (DiTableColumnDataChangeEntity column : columnDataList) {
            tableColumNamedParamValues(column, columnNamedParams);
            columns[i++] = column.getColumnName();
        }
//        ExchangeConfig.ISSYNBACKFIELD
        //更新同步字段
        columns[i] = ExchangeConfig.ISSYNBACKFIELD;
        columnNamedParams.put(ExchangeConfig.ISSYNBACKFIELD, UUID.randomUUID().toString());
        insert.append(" ( " + StringUtils.join(columns, ",") + " ) ");//字段
        insert.append(" values ( :" + StringUtils.join(columns, ",:") + " ) ");//命名参数
        return dao.insert(insert.toString(), columnNamedParams);
    }


    private void tableColumNamedParamValues(DiTableColumnDataChangeEntity column,
                                            Map<String, Object> params) {
        if (column.getDataType().toLowerCase().indexOf("varchar") != -1) {
            params.put(column.getColumnName(), column.getDataTextValue());
        } else if (column.getDataType().toLowerCase().indexOf("clob") != -1) {
            try {
                params.put(column.getColumnName(),
                        IOUtils.toString(column.getDataClobValue().getCharacterStream()));
            } catch (Exception e) {
                logger.error("还原[字段={}]Clob数据异常：", column.getColumnName(), e);
                throw new RuntimeException(e);
            }
        } else if (column.getDataType().toLowerCase().indexOf("blob") != -1) {
            params.put(column.getColumnName(), column.getDataBlobValue());
        } else if (column.getDataType().toLowerCase().indexOf("timestamp") != -1
                || column.getDataType().toLowerCase().indexOf("date") != -1) {
            try {
                if (StringUtils.isNotBlank(column.getDataBasicValue())) {
                    params.put(column.getColumnName(),
                            DateUtils.parseDate(column.getDataBasicValue(),
                                    "yyyy-MM-dd HH:mm:ss.SSS"));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            params.put(column.getColumnName(), column.getDataBasicValue());
        }
    }

    private int update(SessionOperationHibernateDao dao, DiTableDataChangeEntity tableData) {
        List<DiTableColumnDataChangeEntity> columnDataList = tableData.getColumnDataChangeEntities();
        if (columnDataList.isEmpty()) {
            return 0;
        }
        StringBuilder update = new StringBuilder("update ").append(tableData.getTableName());
        String[] values = new String[columnDataList.size() + 1];
        int i = 0;
        Map<String, Object> columnNamedParams = Maps.newHashMap();
        for (DiTableColumnDataChangeEntity column : columnDataList) {
            tableColumNamedParamValues(column, columnNamedParams);
            String v = column.getColumnName() + " = :" + column.getColumnName();
            if (columnNamedParams.get(column.getColumnName()) == null) {
                //空值更新
                v = column.getColumnName() + " is null ";
                columnNamedParams.remove(column.getColumnName());
            }
            values[i++] = v;
        }
        values[i] = ExchangeConfig.ISSYNBACKFIELD;
        columnNamedParams.put(ExchangeConfig.ISSYNBACKFIELD, UUID.randomUUID().toString());
        update.append(" set " + StringUtils.join(values, ","));//字段更新值
        update.append(" where 1=1 ");
        if (StringUtils.isNotBlank(tableData.getPkColName())) {
            String[] pkCols = tableData.getPkColName().split(";");
            String[] pkVals = tableData.getPkUuid().split(";");
            for (int j = 0; j < pkCols.length; j++) {
                update.append(" and ").append(pkCols[j]).append("=:").append("__" + pkCols[j]);
                columnNamedParams.put("__" + pkCols[j], pkVals[j]);
            }

        } else {
            return 0;
        }

        return dao.update(update.toString(), columnNamedParams);
    }

    private int lockDataAsSyncing(String uid, boolean lock) {
        Map<String, Object> lockParams = Maps.newHashMap();
        lockParams.put("uuid", uid);
        lockParams.put("dataStatus", lock ? 1 : 0);
        return dao.update("update front_db_data set data_status=:dataStatus where uuid=:uuid",
                lockParams);
    }
}
