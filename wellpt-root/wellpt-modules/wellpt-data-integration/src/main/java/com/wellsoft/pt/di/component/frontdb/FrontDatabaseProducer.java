package com.wellsoft.pt.di.component.frontdb;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.di.component.AbstractProducer;
import com.wellsoft.pt.di.entity.DiTableColumnDataChangeEntity;
import com.wellsoft.pt.di.entity.DiTableDataChangeEntity;
import com.wellsoft.pt.di.service.DiTableColumnDataChangeService;
import com.wellsoft.pt.di.service.DiTableDataChangeService;
import com.wellsoft.pt.integration.security.ExchangeConfig;
import com.wellsoft.pt.jpa.hibernate4.DynamicHibernateSessionFactoryRegistry;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.activation.DataHandler;
import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/26
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/26    chenq		2019/8/26		Create
 * </pre>
 */
public class FrontDatabaseProducer extends AbstractProducer<FrontDatabaseEndpoint> {


    NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    public FrontDatabaseProducer(FrontDatabaseEndpoint endpoint) {
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


    }

    @Override
    protected void action(Object body, Map<String, Object> headers, Map<String, Object> properties,
                          Map<String, DataHandler> attachments) throws Exception {
        if (body == null) {
            return;
        }
         /*SessionOperationHibernateDao hibernateDao = (SessionOperationHibernateDao) ApplicationContextHolder.getBean(
                "DynamicDAO_" + super.endpoint.getSessionFactoryId());*/
        DiTableDataChangeService diTableDataChangeService = ApplicationContextHolder.getBean(
                DiTableDataChangeService.class);
        DiTableColumnDataChangeService diTableColumnDataChangeService = ApplicationContextHolder.getBean(
                DiTableColumnDataChangeService.class);
        MongoFileService fileService = ApplicationContextHolder.getBean(
                MongoFileService.class);
        long lobLength = 0L;
        List<DiTableDataChangeEntity> dataList = (List<DiTableDataChangeEntity>) body;
        List<String> uuids = Lists.newArrayList();
        Iterator<DiTableDataChangeEntity> iterator = dataList.iterator();
        while (iterator.hasNext()) {
            DiTableDataChangeEntity entity = iterator.next();
            uuids.add(entity.getUuid());
            if (ExchangeConfig.FILETABLE.equalsIgnoreCase(entity.getTableName())
                    && entity.getAction().equalsIgnoreCase("insert")) {
                MongoFileEntity fileEntity = fileService.getFile(entity.getPkUuid());
                if (fileEntity != null) {//附件压缩上传到ftp服务器
                    endpoint.gzipUploadFile2Ftp(fileEntity.getInputstream(), entity.getPkUuid());
                }
            }
            //非附件表
            List<DiTableColumnDataChangeEntity> columnDatas = diTableColumnDataChangeService.getAllColumnDataByUuid(
                    entity.getUuid());
            entity.setColumnDataChangeEntities(columnDatas);

            for (DiTableColumnDataChangeEntity dc : columnDatas) {
                lobLength += dc.getLobLength();
            }

        }


        String encryptedData = "";
        String uuidStr = StringUtils.join(uuids, "@");
        String batchId = DigestUtils.md5Hex(uuidStr);
        if (lobLength >= 1024 * 1024 * 8) {
            ByteArrayOutputStream byteArrayOutputStream = endpoint.gzipData2ByteStream(dataList);
            encryptedData = "file_data_size=" + byteArrayOutputStream.size()
                    + ",file_name=" + batchId + ".zip"
                    + ",uuids=" + uuidStr;
            endpoint.byteUpload2Ftp(byteArrayOutputStream.toByteArray(), batchId + ".zip");
            IOUtils.closeQuietly(byteArrayOutputStream);
        } else {
            encryptedData = endpoint.encryptedDataAsString(dataList);
        }

        String sql = "insert into front_db_data (uuid,encrypted_data,create_time,sync_time,data_status,direction) values" +
                " (:uuid,:encryptedData,:createTime,:syncTime,:dataStatus,:direction)";
        Map<String, Object> data = Maps.newHashMap();
        data.put("uuid", batchId);
        data.put("encryptedData", encryptedData);
        data.put("createTime", new Date());
        data.put("syncTime", new Date());
        data.put("dataStatus", 0);//未同步的数据
        data.put("direction", super.endpoint.getNetworkType());
        int row = namedParameterJdbcTemplate.update(sql, data);
        if (row > 0) {
            diTableDataChangeService.batchUpdateStatus(1, uuids);//更新数据表的变更记录为同步中
        }
    }


}
