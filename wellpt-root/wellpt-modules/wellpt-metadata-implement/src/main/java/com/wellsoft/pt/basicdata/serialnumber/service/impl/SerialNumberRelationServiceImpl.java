package com.wellsoft.pt.basicdata.serialnumber.service.impl;

import com.wellsoft.pt.basicdata.serialnumber.dao.ISerialNumberRelationDao;
import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumberRelation;
import com.wellsoft.pt.basicdata.serialnumber.enums.ObjectTypeEnum;
import com.wellsoft.pt.basicdata.serialnumber.service.ISerialNumberRecordService;
import com.wellsoft.pt.basicdata.serialnumber.service.ISerialNumberRelationService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.HqlUtils;
import com.wellsoft.pt.xxljob.model.ExecutionParam;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @Auther: yt
 * @Date: 2022/4/26 14:55
 * @Description:
 */
@Service
public class SerialNumberRelationServiceImpl extends AbstractJpaServiceImpl<SerialNumberRelation, ISerialNumberRelationDao, String> implements ISerialNumberRelationService {

    @Autowired
    private ISerialNumberRecordService serialNumberRecordService;

    @Override
    @Transactional
    public void dataCleaningTask(ExecutionParam executionParam) {
        List<SerialNumberRelation> relationList = new ArrayList<>();
        if (executionParam.containsKey("snId")) {
            String snIdStr = executionParam.get("snId");
            String[] snIds = snIdStr.split(",");
            Map<String, Object> paramsMap = new HashMap<>();
            paramsMap.put("snIds", snIds);
            relationList = this.listByHQL("from SerialNumberRelation where snId in (:snIds)", paramsMap);
        } else {
            relationList = this.listAll();
        }
        Map<String, Boolean> existsTableMap = new HashMap<>();
        for (SerialNumberRelation relation : relationList) {
            if (relation.getObjectType().equals(ObjectTypeEnum.TABLE.getType())) {
                String tableName = relation.getObjectName();
                String fieldName = relation.getFieldName();
                String key = tableName + "_" + fieldName;
                Boolean flg = existsTableMap.get(key);
                if (flg == null) {
                    flg = this.existsTableFieldName(tableName, fieldName);
                    existsTableMap.put(key, flg);
                }
                List<String> uuidList = new ArrayList<>();
                if (flg) {
                    String sql = "select uuid from " + tableName.toLowerCase() + " where " + fieldName.toLowerCase() + " is not null";
                    uuidList = this.getDao().listCharSequenceBySQL(sql, null);
                }
                Map<String, Object> paramsMap = new HashMap<>();
                paramsMap.put("relationUuid", relation.getUuid());
                StringBuilder sbHql = new StringBuilder("delete from SerialNumberRecord t where t.relationUuid = :relationUuid ");
                if (!CollectionUtils.isEmpty(uuidList)) {
                    sbHql.append(" and ");
                    HqlUtils.notInSql("dataUuid", paramsMap, sbHql, new HashSet<>(uuidList));
                }
                serialNumberRecordService.getDao().deleteByHQL(sbHql.toString(), paramsMap);
            }
        }
    }

    @Override
    public boolean existsTable(String tableName) {
        boolean flg = this.exists(tableName);
        if (!flg) {
            flg = this.exists(tableName.toUpperCase());
        }
        if (!flg) {
            flg = this.exists(tableName.toLowerCase());
        }
        return flg;
    }

    private boolean exists(String tableName) {
        SessionFactoryImpl sessionFactory = (SessionFactoryImpl) this.getDao().getSession().getSessionFactory();
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = sessionFactory.getConnectionProvider().getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            resultSet = metaData.getTables(null, null, tableName, new String[]{"TABLE"});
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            logger.error(String.format("查询表[%s]，是否存在出错：", tableName), e);
        } finally {
            try {
                resultSet.close();
                connection.close();
            } catch (SQLException e) {
                logger.error("链接释放错误：", e);
            }
        }
        return false;
    }

    @Override
    public boolean existsTableFieldName(String tableName, String fieldName) {
        boolean flg = this.exists(tableName, fieldName);
        if (!flg) {
            flg = this.exists(tableName.toUpperCase(), fieldName.toUpperCase());
        }
        if (!flg) {
            flg = this.exists(tableName.toLowerCase(), fieldName.toLowerCase());
        }
        return flg;
    }


    private boolean exists(String tableName, String fieldName) {
        SessionFactoryImpl sessionFactory = (SessionFactoryImpl) this.getDao().getSession().getSessionFactory();
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = sessionFactory.getConnectionProvider().getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            resultSet = metaData.getColumns(null, null, tableName, fieldName);
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            logger.error(String.format("查询表[%s]，字段[%s]是否存在出错：", tableName, fieldName), e);
        } finally {
            try {
                resultSet.close();
                connection.close();
            } catch (SQLException e) {
                logger.error("链接释放错误：", e);
            }
        }
        return false;
    }
}
