package com.wellsoft.pt.integration.service.impl;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.basicdata.systemtable.entity.SystemTable;
import com.wellsoft.pt.basicdata.systemtable.entity.SystemTableAttribute;
import com.wellsoft.pt.basicdata.systemtable.facade.service.SystemTableFacadeService;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.integration.bean.FtpDataBean;
import com.wellsoft.pt.integration.entity.DataOperationLog;
import com.wellsoft.pt.integration.entity.SynchronousSourceField;
import com.wellsoft.pt.integration.entity.SynchronousSourceTable;
import com.wellsoft.pt.integration.entity.SysProperties;
import com.wellsoft.pt.integration.facade.ExchangedataApiFacade;
import com.wellsoft.pt.integration.ftp.CommonFtp;
import com.wellsoft.pt.integration.security.ExchangeConfig;
import com.wellsoft.pt.integration.service.DataOperationLogService;
import com.wellsoft.pt.integration.service.ExchangeDataSynchronousService;
import com.wellsoft.pt.integration.service.SynchronousSourceFieldService;
import com.wellsoft.pt.integration.service.SynchronousSourceTableService;
import com.wellsoft.pt.integration.support.ConManager;
import com.wellsoft.pt.integration.support.ExchangeDataResultTransformer;
import com.wellsoft.pt.jpa.dao.UniversalDao;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.task.bean.JobDetailsBean;
import com.wellsoft.pt.task.entity.JobDetails;
import com.wellsoft.pt.task.service.JobDetailsService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Table;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author ruanhg
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-7-9.1	ruanhg		2014-7-9		Create
 * </pre>
 * @date 2014-7-9
 */
@Service
public class ExchangeDataSynchronousServiceImpl implements ExchangeDataSynchronousService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SynchronousSourceTableService synchronousSourceTableService;
    @Autowired
    private BasicDataApiFacade basicDataApiFacade;
    @Autowired
    private SynchronousSourceFieldService synchronousSourceFieldService;
    @Autowired
    private DyFormFacade dyFormApiFacade;
    @Autowired
    private MongoFileService mongoFileService;
    @Autowired
    private ExchangedataApiFacade exchangedataApiFacade;
    @Autowired
    private JobDetailsService jobDetailsService;
    @Autowired
    private DataOperationLogService dataOperationLogService;
    @Autowired
    private UniversalDao dao;
    @Autowired
    private SystemTableFacadeService systemTableService;

    @Override
    @Transactional(readOnly = true)
    public SynchronousSourceTable getBeanByUuid(String uuid) {
        // TODO Auto-generated method stub
        SynchronousSourceTable temp = new SynchronousSourceTable();
        SynchronousSourceTable table = synchronousSourceTableService.getOne(uuid);
        BeanUtils.copyProperties(table, temp);
        Set<SynchronousSourceField> synchronousSourceFields = table.getSynchronousSourceFields();
        temp.setSynchronousSourceFields(BeanUtils.convertCollection(synchronousSourceFields,
                SynchronousSourceField.class));
        return temp;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getColumnsData(String type, String definitionUuid) {
        // TODO Auto-generated method stub
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (type.equals(ExchangeConfig.SOURCETYPE_ENTITY)) {
            List<SystemTableAttribute> sysTableList = basicDataApiFacade.getSystemTableColumns(definitionUuid);
            for (SystemTableAttribute systemTableAttribute : sysTableList) {
                // if (systemTableAttribute.getIsSynchronization()) {
                Map<String, Object> fieldMap = new HashMap<String, Object>();
                fieldMap.put("fieldCnName", systemTableAttribute.getChineseName());
                fieldMap.put("fieldEnName", systemTableAttribute.getFieldName());
                fieldMap.put("dataType", systemTableAttribute.getColumnType());
                list.add(fieldMap);
                // }
            }
        } else if (type.equals(ExchangeConfig.SOURCETYPE_TABLE)) {
        }
        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public List getSynchronousSourceTablesByType(String treeNodeId, String type) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        if (type.equals(ExchangeConfig.SOURCETYPE_ENTITY)) {
            // 获取系统表的表定义
            List<SystemTable> forms = basicDataApiFacade.getAllSystemTables();
            TreeNode treeNode;
            for (Iterator<SystemTable> iterator = forms.iterator(); iterator.hasNext(); ) {
                SystemTable form = iterator.next();
                SystemTable formNew = new SystemTable();
                BeanUtils.copyProperties(form, formNew);
                treeNode = new TreeNode();
                treeNode.setId(formNew.getUuid());
                treeNode.setName(formNew.getChineseName());
                treeNode.setData(formNew.getTableName());
                treeNodes.add(treeNode);
            }
        } else if (type.equals(ExchangeConfig.SOURCETYPE_TABLE)) {
        }
        return treeNodes;
    }

    @Override
    @Transactional
    public Boolean delSynchronousSourceTables(String[] uuids) {
        // TODO Auto-generated method stub
        try {
            for (int i = 0; i < uuids.length; i++) {
                synchronousSourceTableService.delete(uuids[i]);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public Boolean saveBean(SynchronousSourceTable bean) {
        // TODO Auto-generated method stub
        SynchronousSourceTable table = new SynchronousSourceTable();
        try {
            if (!StringUtils.isBlank(bean.getUuid())) {
                table = synchronousSourceTableService.getOne(bean.getUuid());
                BeanUtils.copyProperties(bean, table);
                synchronousSourceTableService.save(table);

                Set<SynchronousSourceField> fields = table.getSynchronousSourceFields();
                for (SynchronousSourceField field : fields) {
                    SynchronousSourceField synchronousSourceField = synchronousSourceFieldService.getOne(field
                            .getUuid());
                    synchronousSourceFieldService.delete(synchronousSourceField);
                }

                for (SynchronousSourceField field : bean.getSynchronousSourceFields()) {
                    field.setUuid(UUID.randomUUID().toString());
                    field.setSynchronousSourceTable(table);
                    synchronousSourceFieldService.save(field);
                }
            } else {
                BeanUtils.copyProperties(bean, table);
                synchronousSourceTableService.save(table);
                for (SynchronousSourceField field : bean.getSynchronousSourceFields()) {
                    field.setUuid(UUID.randomUUID().toString());
                    field.setSynchronousSourceTable(table);
                    synchronousSourceFieldService.save(field);
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 平台同步数据到前置机（流出）
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataSynchronousService#synchronousOutData(java.util.Date)
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public Boolean synchronousOutData(Date preFireTime, String taskUuid) {
        JobDetailsBean jobDetailsBean = jobDetailsService.getBean(taskUuid);
        String taskId = jobDetailsBean.getId();
        if (preFireTime == null) {
            preFireTime = new Date();
        }
        // TODO Auto-generated method stub
        try {
            /***********************获取同步的信息****************************/
            Map<String, SysProperties> sysPropertiess = exchangedataApiFacade.getAllSysProperties("SYSPROPERTIES");
            String tenant = Config.DEFAULT_TENANT;// 默认租户
            String xt_wl = sysPropertiess.get("xt_wl").getProValue();// 内网/外网
            // in/out
            String fromTenant = tenant;// 租户
            String toTenant = "";
            String ftp_host = "";
            String ftp_user_name = "";
            String ftp_pass_word = "";
            int ftp_post = 21;
            if (xt_wl.equals("in")) {// 当前是内网，内网到内网前置机
                toTenant = sysPropertiess.get("qzj_zh_nw_out").getProValue();
                if (StringUtils.isBlank(toTenant)) {
                    return false;
                }
                ftp_host = sysPropertiess.get("qzj_ftp_nw_out_host").getProValue();
                ftp_user_name = sysPropertiess.get("qzj_ftp_nw_out_username").getProValue();
                ftp_pass_word = sysPropertiess.get("qzj_ftp_nw_out_password").getProValue();
                ftp_post = Integer.parseInt(sysPropertiess.get("qzj_ftp_nw_out_post").getProValue());
            } else if (xt_wl.equals("out")) {// 当前是外网，外网到外网前置机
                toTenant = sysPropertiess.get("qzj_zh_ww_out").getProValue();
                if (StringUtils.isBlank(toTenant)) {
                    return false;
                }
                ftp_host = sysPropertiess.get("qzj_ftp_ww_out_host").getProValue();
                ftp_user_name = sysPropertiess.get("qzj_ftp_ww_out_username").getProValue();
                ftp_pass_word = sysPropertiess.get("qzj_ftp_ww_out_password").getProValue();
                ftp_post = Integer.parseInt(sysPropertiess.get("qzj_ftp_ww_out_post").getProValue());
            }
            List<SynchronousSourceTable> synchronousSourceTables = synchronousSourceTableService
                    .getSynchronousSourceTables("out", taskId);// 查找需要同步的表
            IgnoreLoginUtils.logout();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String preFireTimeStr = sdf.format(preFireTime);
            Map<String, List<String>> uuidMap = new HashMap<String, List<String>>();
            for (SynchronousSourceTable st : synchronousSourceTables) {
                /***********************提取数据****************************/
                Map<String, File> fileMap = new HashMap<String, File>();
                List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
                IgnoreLoginUtils.login(fromTenant, fromTenant);// 虚拟登陆源租户
                Session session = this.dao.getSession();
                if (st.getIsRelationTable() != null && st.getIsRelationTable()) {
                    try {
                        // 根据uuid提取主控，被控的数据
                        String querySql = "select * from " + st.getTableEnName() + " where ";
                        String whereSql = "";
                        for (SynchronousSourceField fieldItem : st.getSynchronousSourceFields()) {
                            if (!StringUtils.isBlank(fieldItem.getForeignKeyTable())) {
                                List<String> uuidList = uuidMap.get(fieldItem.getForeignKeyTable());
                                if (uuidList != null) {
                                    whereSql += " or " + fieldItem.getFieldEnName() + " in (";
                                    String uuids = "";
                                    for (String uuidItem : uuidList) {
                                        uuids += ",'" + uuidItem + "'";
                                    }
                                    whereSql += uuids.replaceFirst(",", "") + ") ";
                                }
                            }
                        }
                        if (!StringUtils.isBlank(whereSql)) {
                            querySql += whereSql.replaceFirst("or", "");
                            dataList = session.createSQLQuery(querySql)
                                    .setResultTransformer(ExchangeDataResultTransformer.INSTANCE).list();
                        }
                        if (dataList.size() == 0) {
                            session.close();
                            IgnoreLoginUtils.logout();// 退出源租户
                            continue;
                        }
                        SimpleDateFormat time_ = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        logger.error("*******************OUT gxb date:" + time_.format(new Date()) + " name:"
                                + st.getTableEnName() + " stUuid:" + st.getUuid() + " size: " + dataList.size()
                                + " sql:" + querySql);
                        session.close();
                        IgnoreLoginUtils.logout();// 退出源租户
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        session.close();
                        IgnoreLoginUtils.logout();// 退出源租户
                        logger.error(e.getMessage(), e);
                        // logger.info(e.getMessage());
                    }
                } else {
                    try {
                        String querySql = "select * from " + st.getTableEnName() + " o where o.modify_time >"
                                + "to_timestamp('" + preFireTimeStr + "', 'YYYY-MM-DD HH24:MI:ss.ff3')";
                        dataList = session.createSQLQuery(querySql)
                                .setResultTransformer(ExchangeDataResultTransformer.INSTANCE).list();
                        if (dataList.size() == 0) {
                            session.close();
                            IgnoreLoginUtils.logout();// 退出源租户
                            continue;
                        }
                        List<String> uuids = new ArrayList<String>();
                        for (Map<String, Object> dataItem : dataList) {
                            String uuid = dataItem.get("uuid").toString();
                            uuids.add(uuid);
                            if (mongoFileService.isFolderExist(uuid)) {// 判断是否存在文件夹
                                Date modifyTime = (Date) (dataItem.get("modify_time") == null ? dataItem
                                        .get("create_time") : dataItem.get("modify_time"));
                                Calendar calendar = new GregorianCalendar();
                                calendar.setTime(modifyTime);
                                calendar.add(calendar.SECOND, -59);
                                modifyTime = calendar.getTime();
                                File zipFile = mongoFileService.exportChangeInfo(uuid, modifyTime);
                                if (zipFile != null) {
                                    fileMap.put(uuid, zipFile);
                                }
                            }
                        }
                        SimpleDateFormat time_ = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        logger.error("*******************OUT ptb date:" + time_.format(new Date()) + " name:"
                                + st.getTableEnName() + " stUuid:" + st.getUuid() + " size: " + dataList.size()
                                + " sql:" + querySql + " uuid:" + uuids.toString());
                        // 有关系表
                        if (!StringUtils.isBlank(st.getRelationTable())) {// 关系控制表
                            uuidMap.put(st.getTableEnName(), uuids);
                        }
                        session.close();
                        IgnoreLoginUtils.logout();// 退出源租户
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        session.close();
                        IgnoreLoginUtils.logout();// 退出源租户
                        logger.error(e.getMessage(), e);
                        // logger.info(e.getMessage());
                    }
                }

                /****************************插入数据*****************************/
                if (!StringUtils.isBlank(st.getTenant())) {// 指定租户时
                    IgnoreLoginUtils.login(st.getTenant(), st.getTenant());// 虚拟登陆目标租户
                } else {
                    IgnoreLoginUtils.login(toTenant, toTenant);// 虚拟登陆目标租户
                }

                Session session2 = this.dao.getSession();
                if (st.getIsRelationTable() != null && st.getIsRelationTable()) {
                    try {
                        // 删除前置机本次同步有关系表的所有关系表数据
                        String whereSql = "";
                        for (SynchronousSourceField sfield : st.getSynchronousSourceFields()) {
                            String foreignKeyTable = sfield.getForeignKeyTable();
                            if (!StringUtils.isBlank(foreignKeyTable)) {
                                List<String> uuidList = uuidMap.get(foreignKeyTable);
                                if (uuidList != null) {
                                    whereSql += " or " + sfield.getFieldEnName() + " in (";
                                    String uuids = "";
                                    for (String uuidItem : uuidList) {
                                        uuids += ",'" + uuidItem + "'";
                                    }
                                    whereSql += uuids.replaceFirst(",", "") + ") ";
                                }
                            }
                        }
                        String delRelationSql = "delete " + st.getTableEnName() + " where "
                                + whereSql.replaceFirst(" or ", "");
                        session2.createSQLQuery(delRelationSql).executeUpdate();
                        // 插入关系表
                        StringBuffer columns = new StringBuffer("");
                        StringBuffer valueKeys = new StringBuffer("");
                        for (SynchronousSourceField fieldItem : st.getSynchronousSourceFields()) {
                            columns.append("," + fieldItem.getFieldEnName());
                            valueKeys.append(",:" + fieldItem.getFieldEnName());
                        }
                        for (Map<String, Object> dataItem : dataList) {
                            String insertRelationSql = "insert into " + st.getTableEnName() + " ("
                                    + columns.toString().replaceFirst(",", "") + ") values ("
                                    + valueKeys.toString().replaceFirst(",", "") + ")";
                            SQLQuery sqlquery = session2.createSQLQuery(insertRelationSql);
                            for (SynchronousSourceField fieldItem : st.getSynchronousSourceFields()) {
                                Object obj = dataItem.get(fieldItem.getFieldEnName().toLowerCase());
                                if (fieldItem.getDataType().equals("DATE")) {
                                    sqlquery.setTimestamp(fieldItem.getFieldEnName(), obj == null ? null : (Date) obj);
                                } else if (fieldItem.getDataType().equals("INTEGER")) {
                                    sqlquery.setInteger(fieldItem.getFieldEnName(),
                                            obj == null ? 0 : Integer.parseInt(obj.toString()));
                                } else if (fieldItem.getDataType().equals("FLOAT")) {
                                    sqlquery.setFloat(fieldItem.getFieldEnName(),
                                            obj == null ? 0 : Float.parseFloat(obj.toString()));
                                } else if (fieldItem.getDataType().equals("LONG")) {
                                    sqlquery.setLong(fieldItem.getFieldEnName(),
                                            obj == null ? 0 : Long.parseLong(obj.toString()));
                                } else if (fieldItem.getDataType().equals("DOUBLE")) {
                                    sqlquery.setDouble(fieldItem.getFieldEnName(),
                                            obj == null ? 0 : Double.parseDouble(obj.toString()));
                                } else if (fieldItem.getDataType().equals("CLOB")) {
                                    if (obj != null) {
                                        Clob clob = (Clob) obj;
                                        sqlquery.setText(fieldItem.getFieldEnName(),
                                                IOUtils.toString(clob.getCharacterStream()).toString());
                                    } else {
                                        sqlquery.setText(fieldItem.getFieldEnName(), "");
                                    }

                                } else {
                                    sqlquery.setString(fieldItem.getFieldEnName(), obj == null ? "" : obj.toString());
                                }
                            }
                            sqlquery.executeUpdate();
                        }
                        session2.close();
                        IgnoreLoginUtils.logout();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        session2.close();
                        IgnoreLoginUtils.logout();// 退出源租户
                        logger.error(e.getMessage(), e);
                        // logger.info(e.getMessage());
                    }
                } else {
                    try {
                        // 插入字段的处理
                        StringBuffer columns = new StringBuffer("");
                        String idEntityField = ",uuid,creator,create_time,modifier,modify_time,rec_ver";
                        if (st.getType().equals(ExchangeConfig.SOURCETYPE_TABLE)) {
                            idEntityField += ",form_uuid,status,signature_";
                        }
                        String insertFieldStr = "";// 要插入的字段
                        columns.append(idEntityField);
                        for (SynchronousSourceField fieldItem : st.getSynchronousSourceFields()) {
                            if (idEntityField.indexOf("," + fieldItem.getFieldEnName()) == -1) {// 公共字段不重复加入
                                columns.append("," + fieldItem.getFieldEnName());
                            }
                        }
                        insertFieldStr = columns.toString().replaceFirst(",", "");
                        // 插入数据处理
                        for (Map<String, Object> dataItem : dataList) {
                            // 删除已经存在数据
                            String uuid = dataItem.get("uuid").toString();
                            String querySql2 = "select o.uuid from " + st.getTableEnName() + " o where o.uuid='" + uuid
                                    + "'";// 查询是否存在数据
                            List<Map<String, Object>> dataList2 = session2.createSQLQuery(querySql2)
                                    .setResultTransformer(ExchangeDataResultTransformer.INSTANCE).list();
                            if (dataList2.size() > 0) {// 删除已存在的uuid
                                String delSql = "delete " + st.getTableEnName() + " o where o.uuid='" + uuid + "'";
                                session2.createSQLQuery(delSql).executeUpdate();
                            }
                            // 附件的处理
                            if (fileMap.containsKey(uuid)) {
                                File zipFile = fileMap.get(uuid);
                                CommonFtp myFtp = new CommonFtp(ftp_host, ftp_post, ftp_user_name, ftp_pass_word);
                                try {
                                    if (!myFtp.isConnected()) {
                                        myFtp.connect();
                                    }
                                    String zuid = uuid + ".zip";
                                    myFtp.deleteFile("\\", zuid);
                                    myFtp.uploadFile(zipFile, "\\");// 上传
                                    myFtp.rename("\\", zuid + ".oa");// 上传完成后修改文件名
                                } catch (Exception e) {
                                    logger.error(e.getMessage(), e);
                                    // logger.info(e.getMessage());
                                } finally {
                                    myFtp.disconnect();
                                }
                            }
                            // 表数据的处理
                            Map<String, Object> valueMaps = new HashMap<String, Object>();
                            Map<String, String> keyMaps = new HashMap<String, String>();
                            StringBuffer valueKeys = new StringBuffer("");

                            valueKeys.append(",:uuid");
                            valueMaps.put("uuid", dataItem.get("uuid"));
                            keyMaps.put("uuid", "STRING");

                            valueKeys.append(",:creator");
                            valueMaps.put("creator", dataItem.get("creator"));
                            keyMaps.put("creator", "STRING");

                            valueKeys.append(",:create_time");
                            valueMaps.put("create_time", dataItem.get("create_time"));
                            keyMaps.put("create_time", "DATE");

                            valueKeys.append(",:modifier");
                            valueMaps.put("modifier", dataItem.get("modifier"));
                            keyMaps.put("modifier", "STRING");

                            valueKeys.append(",:modify_time");
                            valueMaps.put("modify_time", dataItem.get("modify_time"));
                            keyMaps.put("modify_time", "DATE");

                            valueKeys.append(",:rec_ver");
                            valueMaps.put("rec_ver", dataItem.get("rec_ver"));
                            keyMaps.put("rec_ver", "INTEGER");
                            if (st.getType().equals(ExchangeConfig.SOURCETYPE_TABLE)) {
                                valueKeys.append(",:form_uuid");
                                valueMaps.put("form_uuid", dataItem.get("form_uuid"));
                                keyMaps.put("form_uuid", "STRING");

                                valueKeys.append(",:status");
                                valueMaps.put("status", dataItem.get("status"));
                                keyMaps.put("status", "STRING");

                                valueKeys.append(",:signature_");
                                valueMaps.put("signature_", dataItem.get("signature_"));
                                keyMaps.put("signature_", "STRING");
                            }
                            for (SynchronousSourceField fieldItem : st.getSynchronousSourceFields()) {
                                if (idEntityField.indexOf("," + fieldItem.getFieldEnName()) == -1) {// 公共字段不重复加入
                                    valueKeys.append(",:" + fieldItem.getFieldEnName());
                                    valueMaps.put(fieldItem.getFieldEnName(),
                                            dataItem.get(fieldItem.getFieldEnName().toLowerCase()));
                                    keyMaps.put(fieldItem.getFieldEnName(), fieldItem.getDataType());
                                }
                            }
                            String insertSql = "insert into " + st.getTableEnName() + " (" + insertFieldStr
                                    + ") values (" + valueKeys.toString().replaceFirst(",", "") + ")";
                            SQLQuery sqlquery = session2.createSQLQuery(insertSql);
                            for (String key : keyMaps.keySet()) {
                                if (keyMaps.get(key).equals("DATE")) {
                                    sqlquery.setTimestamp(key,
                                            valueMaps.get(key) == null ? null : (Date) valueMaps.get(key));
                                } else if (keyMaps.get(key).equals("INTEGER")) {
                                    sqlquery.setInteger(
                                            key,
                                            valueMaps.get(key) == null ? 0 : Integer.parseInt(valueMaps.get(key)
                                                    .toString()));
                                } else if (keyMaps.get(key).equals("FLOAT")) {
                                    sqlquery.setFloat(
                                            key,
                                            valueMaps.get(key) == null ? 0 : Float.parseFloat(valueMaps.get(key)
                                                    .toString()));
                                } else if (keyMaps.get(key).equals("LONG")) {
                                    sqlquery.setLong(
                                            key,
                                            valueMaps.get(key) == null ? 0 : Long.parseLong(valueMaps.get(key)
                                                    .toString()));
                                } else if (keyMaps.get(key).equals("DOUBLE")) {
                                    sqlquery.setDouble(
                                            key,
                                            valueMaps.get(key) == null ? 0 : Double.parseDouble(valueMaps.get(key)
                                                    .toString()));
                                } else if (keyMaps.get(key).equals("CLOB")) {
                                    if (valueMaps.get(key) != null) {
                                        Clob clob = (Clob) valueMaps.get(key);
                                        sqlquery.setText(key, IOUtils.toString(clob.getCharacterStream()).toString());
                                    } else {
                                        sqlquery.setText(key, "");
                                    }
                                } else {
                                    sqlquery.setString(key, valueMaps.get(key) == null ? "" : valueMaps.get(key)
                                            .toString());
                                }
                            }
                            sqlquery.executeUpdate();
                        }
                        session2.close();
                        IgnoreLoginUtils.logout();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        session2.close();
                        IgnoreLoginUtils.logout();// 退出源租户
                        logger.error(e.getMessage(), e);
                        // logger.info(e.getMessage());
                    }
                }
            }
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // logger.info(e.getMessage());
            return false;
        }
    }

    /**
     * 平台从前置机提取数据(流入)
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataSynchronousService#synchronousInData(java.util.Date)
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public Boolean synchronousInData(String taskUuid) {
        JobDetailsBean jobDetailsBean = jobDetailsService.getBean(taskUuid);
        String taskId = jobDetailsBean.getId();
        Date nowDate = new Date();
        // TODO Auto-generated method stub
        try {
            /***********************获取同步的信息****************************/
            Map<String, SysProperties> sysPropertiess = exchangedataApiFacade.getAllSysProperties("SYSPROPERTIES");
            String tenant = Config.DEFAULT_TENANT;// 默认租户
            String xt_wl = sysPropertiess.get("xt_wl").getProValue();// 内网/外网
            // in/out
            String fromTenant = "";
            String toTenant = tenant;
            String ftp_host = "";
            String ftp_user_name = "";
            String ftp_pass_word = "";
            int ftp_post = 21;
            if (xt_wl.equals("in")) {// 当前是内网，从内网前置机提取数据
                fromTenant = sysPropertiess.get("qzj_zh_nw_in").getProValue();
                if (StringUtils.isBlank(fromTenant)) {
                    return false;
                }
                ftp_host = sysPropertiess.get("qzj_ftp_nw_in_host").getProValue();
                ftp_user_name = sysPropertiess.get("qzj_ftp_nw_in_username").getProValue();
                ftp_pass_word = sysPropertiess.get("qzj_ftp_nw_in_password").getProValue();
                ftp_post = Integer.parseInt(sysPropertiess.get("qzj_ftp_nw_in_post").getProValue());
            } else if (xt_wl.equals("out")) {// 当前是外网，从外网前置机提取数据
                fromTenant = sysPropertiess.get("qzj_zh_ww_in").getProValue();
                if (StringUtils.isBlank(fromTenant)) {
                    return false;
                }
                ftp_host = sysPropertiess.get("qzj_ftp_ww_in_host").getProValue();
                ftp_user_name = sysPropertiess.get("qzj_ftp_ww_in_username").getProValue();
                ftp_pass_word = sysPropertiess.get("qzj_ftp_ww_in_password").getProValue();
                ftp_post = Integer.parseInt(sysPropertiess.get("qzj_ftp_ww_in_post").getProValue());
            }
            // 当前登录平台租户，获取需要同步的信息
            List<SynchronousSourceTable> synchronousSourceTables = synchronousSourceTableService
                    .getSynchronousSourceTables("in", taskId);// 查找需要同步的表
            IgnoreLoginUtils.logout();// 退出平台租户

            Map<String, List<String>> uuidMap = new HashMap<String, List<String>>();
            for (SynchronousSourceTable st : synchronousSourceTables) {
                List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
                /***********************提取数据****************************/
                if (!StringUtils.isBlank(st.getTenant())) {// 指定租户时
                    IgnoreLoginUtils.login(st.getTenant(), st.getTenant());// 虚拟登陆目标租户
                } else {
                    IgnoreLoginUtils.login(fromTenant, fromTenant);// 虚拟登陆前置机租户
                }

                Session session = this.dao.getSession();
                try {
                    String querySql = "select * from " + st.getTableEnName();
                    if (st.getIsRelationTable() == null
                            || (st.getIsRelationTable() != null && !st.getIsRelationTable())) {// 非关系表
                        if (st.getPreModifyTime() == null) {
                            querySql += " o order by o.modify_time asc";
                        } else {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                            Calendar calendar = new GregorianCalendar();
                            calendar.setTime(st.getPreModifyTime());

                            Calendar calendar2 = new GregorianCalendar();
                            calendar2.setTime(nowDate);
                            String nowDateStr = sdf.format(calendar2.getTime());

                            String preModifyTime = sdf.format(calendar.getTime());
                            querySql += " o where o.modify_time>to_timestamp('" + preModifyTime
                                    + "', 'YYYY-MM-DD HH24:MI:ss.ff3') and o.modify_time<to_timestamp('" + nowDateStr
                                    + "', 'YYYY-MM-DD HH24:MI:ss.ff3') order by o.modify_time asc";
                        }
                    }
                    SQLQuery sqlqueryData = session.createSQLQuery(querySql);
                    dataList = sqlqueryData.setResultTransformer(ExchangeDataResultTransformer.INSTANCE).list();
                    if (dataList.size() == 0) {
                        session.close();
                        IgnoreLoginUtils.logout();// 退出源租户
                        continue;
                    }

                    List<String> uuids = new ArrayList<String>();
                    for (Map<String, Object> dataItem : dataList) {
                        String uuid = dataItem.get("uuid").toString();
                        uuids.add(uuid);
                    }
                    SimpleDateFormat time_ = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    logger.error("------------------------IN date:" + time_.format(new Date()) + " name:"
                            + st.getTableEnName() + " stUuid:" + st.getUuid() + " size: " + dataList.size() + " "
                            + querySql + " uuid:" + uuids.toString());
                    // 有关系表
                    if (!StringUtils.isBlank(st.getRelationTable())) {// 关系控制表
                        uuidMap.put(st.getTableEnName(), uuids);
                    }
                    session.close();
                    IgnoreLoginUtils.logout();// 退出前置机租户
                } catch (Exception e) {
                    session.close();
                    IgnoreLoginUtils.logout();// 退出前置机租户
                    logger.error(e.getMessage(), e);
                    // logger.info(e.getMessage());
                }

                /****************************更新数据*****************************/
                IgnoreLoginUtils.login(toTenant, toTenant);// 虚拟平台租户
                Session session2 = this.dao.getSession();
                try {
                    if (st.getTableEnName().equals("is_del_data_log")) {// 删除记录表
                        // 删除数据处理
                        int i = 1;
                        for (Map<String, Object> dataItem : dataList) {
                            String delDataUuid = dataItem.get("data_uuid").toString();

                            SynchronousSourceTable sTable = synchronousSourceTableService.getByTableEnName(dataItem
                                    .get("table_name").toString().toLowerCase());
                            if (sTable == null || (sTable.getIsRelationTable() != null && sTable.getIsRelationTable())) {
                                continue;
                            }
                            if (!StringUtils.isBlank(sTable.getRelationTable())) {// 关系控制表，先删除关系表数据
                                String relationTableStr = sTable.getRelationTable();
                                String[] relationTableArr = relationTableStr.split(";");
                                for (int j = 0; j < relationTableArr.length; j++) {
                                    SynchronousSourceTable relationTable = synchronousSourceTableService
                                            .getByTableEnName(relationTableArr[j]);
                                    String field = "";
                                    for (SynchronousSourceField synchronousSourceField : relationTable
                                            .getSynchronousSourceFields()) {
                                        if (!StringUtils.isBlank(synchronousSourceField.getForeignKeyTable())
                                                && synchronousSourceField.getForeignKeyTable().equals(
                                                sTable.getTableEnName())) {
                                            field = synchronousSourceField.getFieldEnName();
                                        }
                                    }
                                    String delRelationSql = "delete " + relationTableArr[j] + " o where o." + field
                                            + "='" + delDataUuid + "'";
                                    session2.createSQLQuery(delRelationSql).executeUpdate();
                                }
                            }
                            if (i == 1) {
                                st.setPreModifyTime((Date) dataItem.get("modify_time"));
                            }
                            i++;
                            String delTableName = dataItem.get("table_name").toString();
                            String delSql = "delete " + delTableName + " o where o.uuid='" + delDataUuid + "'";
                            session2.createSQLQuery(delSql).executeUpdate();
                        }
                        session2.close();
                        IgnoreLoginUtils.logout();
                    } else if (st.getIsRelationTable() != null && st.getIsRelationTable()) {// 关系表
                        // 插入数据处理
                        for (Map<String, Object> dataItem : dataList) {
                            String insertSelectSql = "select * from " + st.getTableEnName();
                            String whereStr = "";
                            for (SynchronousSourceField fieldItem : st.getSynchronousSourceFields()) {
                                if (!StringUtils.isBlank(fieldItem.getForeignKeyTable())) {
                                    whereStr += " and " + fieldItem.getFieldEnName() + "='"
                                            + dataItem.get(fieldItem.getFieldEnName().toLowerCase()).toString() + "'";
                                }
                            }
                            whereStr = whereStr.replaceFirst(" and ", "");
                            List<Map<String, Object>> insetDataList = session2
                                    .createSQLQuery(insertSelectSql + " where " + whereStr)
                                    .setResultTransformer(ExchangeDataResultTransformer.INSTANCE).list();
                            if (insetDataList.size() == 0) {
                                // 插入字段的处理
                                StringBuffer columns = new StringBuffer("");
                                StringBuffer valueKeys = new StringBuffer("");
                                for (SynchronousSourceField fieldItem : st.getSynchronousSourceFields()) {
                                    columns.append("," + fieldItem.getFieldEnName());
                                    valueKeys.append(",:" + fieldItem.getFieldEnName());
                                }
                                String insertSql = "insert into " + st.getTableEnName() + " ("
                                        + columns.toString().replaceFirst(",", "") + ") values ("
                                        + valueKeys.toString().replaceFirst(",", "") + ")";
                                SQLQuery sqlquery = session2.createSQLQuery(insertSql);
                                for (SynchronousSourceField fieldItem : st.getSynchronousSourceFields()) {
                                    Object obj = dataItem.get(fieldItem.getFieldEnName().toLowerCase());
                                    if (fieldItem.getDataType().equals("DATE")) {
                                        sqlquery.setTimestamp(fieldItem.getFieldEnName(), obj == null ? null
                                                : (Date) obj);
                                    } else if (fieldItem.getDataType().equals("INTEGER")) {
                                        sqlquery.setInteger(fieldItem.getFieldEnName(),
                                                obj == null ? 0 : Integer.parseInt(obj.toString()));
                                    } else if (fieldItem.getDataType().equals("FLOAT")) {
                                        sqlquery.setFloat(fieldItem.getFieldEnName(),
                                                obj == null ? 0 : Float.parseFloat(obj.toString()));
                                    } else if (fieldItem.getDataType().equals("LONG")) {
                                        sqlquery.setLong(fieldItem.getFieldEnName(),
                                                obj == null ? 0 : Long.parseLong(obj.toString()));
                                    } else if (fieldItem.getDataType().equals("DOUBLE")) {
                                        sqlquery.setDouble(fieldItem.getFieldEnName(),
                                                obj == null ? 0 : Double.parseDouble(obj.toString()));
                                    } else if (fieldItem.getDataType().equals("CLOB")) {
                                        if (obj != null) {
                                            Clob clob = (Clob) obj;
                                            sqlquery.setText(fieldItem.getFieldEnName(),
                                                    IOUtils.toString(clob.getCharacterStream()).toString());
                                        } else {
                                            sqlquery.setText(fieldItem.getFieldEnName(), "");
                                        }

                                    } else {
                                        sqlquery.setString(fieldItem.getFieldEnName(),
                                                obj == null ? "" : obj.toString());
                                    }
                                }
                                sqlquery.executeUpdate();
                            }
                        }
                        session2.close();
                        IgnoreLoginUtils.logout();

                    } else {
                        // 插入数据处理
                        int i = 1;
                        for (Map<String, Object> dataItem : dataList) {
                            String uuid = dataItem.get("uuid").toString();
                            if (i == 1) {
                                st.setPreModifyTime((Date) dataItem.get("modify_time"));
                            }
                            i++;
                            String querySql2 = "select o.uuid from " + st.getTableEnName() + " o where o.uuid='" + uuid
                                    + "'";// 查询是否存在数据
                            List<Map<String, Object>> dataList2 = session2.createSQLQuery(querySql2)
                                    .setResultTransformer(ExchangeDataResultTransformer.INSTANCE).list();
                            /******由于提取数据受表约束条件的限制更新、插入需要分开处理*********/
                            if (dataList2.size() > 0) {// 更新
                                // 更新字段的处理
                                String idEntityField = ",creator=:creator,create_time=:create_time,modifier=:modifier,modify_time=:modify_time,rec_ver=:rec_ver";
                                if (st.getType().equals(ExchangeConfig.SOURCETYPE_TABLE)) {
                                    idEntityField += ",form_uuid=:form_uuid,status=:status,signature_=:signature_";
                                }
                                String setStr = idEntityField;
                                for (SynchronousSourceField fieldItem : st.getSynchronousSourceFields()) {
                                    if (idEntityField.indexOf("," + fieldItem.getFieldEnName()) == -1) {// 公共字段不重复加入
                                        setStr += "," + fieldItem.getFieldEnName() + "=:" + fieldItem.getFieldEnName();
                                    }
                                }
                                setStr = setStr.replaceFirst(",", "");
                                // 表数据的处理
                                Map<String, Object> valueMaps = new HashMap<String, Object>();
                                Map<String, String> keyMaps = new HashMap<String, String>();

                                valueMaps.put("creator", dataItem.get("creator"));
                                keyMaps.put("creator", "STRING");

                                valueMaps.put("create_time", dataItem.get("create_time"));
                                keyMaps.put("create_time", "DATE");

                                valueMaps.put("modifier", dataItem.get("modifier"));
                                keyMaps.put("modifier", "STRING");

                                valueMaps.put("modify_time", dataItem.get("modify_time"));
                                keyMaps.put("modify_time", "DATE");

                                valueMaps.put("rec_ver", dataItem.get("rec_ver"));
                                keyMaps.put("rec_ver", "INTEGER");

                                if (st.getType().equals(ExchangeConfig.SOURCETYPE_TABLE)) {
                                    valueMaps.put("form_uuid", dataItem.get("form_uuid"));
                                    keyMaps.put("form_uuid", "STRING");

                                    valueMaps.put("status", dataItem.get("status"));
                                    keyMaps.put("status", "STRING");

                                    valueMaps.put("signature_", dataItem.get("signature_"));
                                    keyMaps.put("signature_", "STRING");
                                }
                                for (SynchronousSourceField fieldItem : st.getSynchronousSourceFields()) {
                                    if (idEntityField.indexOf("," + fieldItem.getFieldEnName()) == -1) {// 公共字段不重复加入
                                        valueMaps.put(fieldItem.getFieldEnName(),
                                                dataItem.get(fieldItem.getFieldEnName().toLowerCase()));
                                        keyMaps.put(fieldItem.getFieldEnName(), fieldItem.getDataType());
                                    }
                                }
                                String updateSql = "update " + st.getTableEnName() + " o set " + setStr
                                        + " where o.uuid='" + uuid + "'";
                                SQLQuery sqlquery = session2.createSQLQuery(updateSql);
                                for (String key : keyMaps.keySet()) {
                                    if (keyMaps.get(key).equals("DATE")) {
                                        sqlquery.setTimestamp(key,
                                                valueMaps.get(key) == null ? null : (Date) valueMaps.get(key));
                                    } else if (keyMaps.get(key).equals("INTEGER")) {
                                        sqlquery.setInteger(
                                                key,
                                                valueMaps.get(key) == null ? 0 : Integer.parseInt(valueMaps.get(key)
                                                        .toString()));
                                    } else if (keyMaps.get(key).equals("FLOAT")) {
                                        sqlquery.setFloat(
                                                key,
                                                valueMaps.get(key) == null ? 0 : Float.parseFloat(valueMaps.get(key)
                                                        .toString()));
                                    } else if (keyMaps.get(key).equals("DOUBLE")) {
                                        sqlquery.setDouble(
                                                key,
                                                valueMaps.get(key) == null ? 0 : Double.parseDouble(valueMaps.get(key)
                                                        .toString()));
                                    } else if (keyMaps.get(key).equals("LONG")) {
                                        sqlquery.setLong(
                                                key,
                                                valueMaps.get(key) == null ? 0 : Long.parseLong(valueMaps.get(key)
                                                        .toString()));
                                    } else if (keyMaps.get(key).equals("CLOB")) {
                                        if (valueMaps.get(key) != null) {
                                            Clob clob = (Clob) valueMaps.get(key);
                                            sqlquery.setText(key, IOUtils.toString(clob.getCharacterStream())
                                                    .toString());
                                        } else {
                                            sqlquery.setText(key, "");
                                        }
                                    } else {
                                        sqlquery.setString(key, valueMaps.get(key) == null ? "" : valueMaps.get(key)
                                                .toString());
                                    }
                                }
                                sqlquery.executeUpdate();
                            } else {// 添加
                                // 插入字段的处理
                                StringBuffer columns = new StringBuffer("");
                                String idEntityField = ",uuid,creator,create_time,modifier,modify_time,rec_ver";
                                if (st.getType().equals(ExchangeConfig.SOURCETYPE_TABLE)) {
                                    idEntityField += ",form_uuid,status,signature_";
                                }
                                String insertFieldStr = "";// 要插入的字段
                                columns.append(idEntityField);
                                for (SynchronousSourceField fieldItem : st.getSynchronousSourceFields()) {
                                    if (idEntityField.indexOf("," + fieldItem.getFieldEnName()) == -1) {// 公共字段不重复加入
                                        columns.append("," + fieldItem.getFieldEnName());
                                    }
                                }
                                insertFieldStr = columns.toString().replaceFirst(",", "");
                                // 表数据的处理
                                Map<String, Object> valueMaps = new HashMap<String, Object>();
                                Map<String, String> keyMaps = new HashMap<String, String>();
                                StringBuffer valueKeys = new StringBuffer("");

                                valueKeys.append(",:uuid");
                                valueMaps.put("uuid", dataItem.get("uuid"));
                                keyMaps.put("uuid", "STRING");

                                valueKeys.append(",:creator");
                                valueMaps.put("creator", dataItem.get("creator"));
                                keyMaps.put("creator", "STRING");

                                valueKeys.append(",:create_time");
                                valueMaps.put("create_time", dataItem.get("create_time"));
                                keyMaps.put("create_time", "DATE");

                                valueKeys.append(",:modifier");
                                valueMaps.put("modifier", dataItem.get("modifier"));
                                keyMaps.put("modifier", "STRING");

                                valueKeys.append(",:modify_time");
                                valueMaps.put("modify_time", dataItem.get("modify_time"));
                                keyMaps.put("modify_time", "DATE");

                                valueKeys.append(",:rec_ver");
                                valueMaps.put("rec_ver", dataItem.get("rec_ver"));
                                keyMaps.put("rec_ver", "INTEGER");

                                if (st.getType().equals(ExchangeConfig.SOURCETYPE_TABLE)) {
                                    valueKeys.append(",:form_uuid");
                                    valueMaps.put("form_uuid", dataItem.get("form_uuid"));
                                    keyMaps.put("form_uuid", "STRING");

                                    valueKeys.append(",:status");
                                    valueMaps.put("status", dataItem.get("status"));
                                    keyMaps.put("status", "STRING");

                                    valueKeys.append(",:signature_");
                                    valueMaps.put("signature_", dataItem.get("signature_"));
                                    keyMaps.put("signature_", "STRING");
                                }
                                for (SynchronousSourceField fieldItem : st.getSynchronousSourceFields()) {
                                    if (idEntityField.indexOf("," + fieldItem.getFieldEnName()) == -1) {// 公共字段不重复加入
                                        valueKeys.append(",:" + fieldItem.getFieldEnName());
                                        valueMaps.put(fieldItem.getFieldEnName(),
                                                dataItem.get(fieldItem.getFieldEnName().toLowerCase()));
                                        keyMaps.put(fieldItem.getFieldEnName(), fieldItem.getDataType());
                                    }
                                }
                                String insertSql = "insert into " + st.getTableEnName() + " (" + insertFieldStr
                                        + ") values (" + valueKeys.toString().replaceFirst(",", "") + ")";
                                SQLQuery sqlquery = session2.createSQLQuery(insertSql);
                                for (String key : keyMaps.keySet()) {
                                    if (keyMaps.get(key).equals("DATE")) {
                                        sqlquery.setTimestamp(key,
                                                valueMaps.get(key) == null ? null : (Date) valueMaps.get(key));
                                    } else if (keyMaps.get(key).equals("INTEGER")) {
                                        sqlquery.setInteger(
                                                key,
                                                valueMaps.get(key) == null ? 0 : Integer.parseInt(valueMaps.get(key)
                                                        .toString()));
                                    } else if (keyMaps.get(key).equals("FLOAT")) {
                                        sqlquery.setFloat(
                                                key,
                                                valueMaps.get(key) == null ? 0 : Float.parseFloat(valueMaps.get(key)
                                                        .toString()));
                                    } else if (keyMaps.get(key).equals("DOUBLE")) {
                                        sqlquery.setDouble(
                                                key,
                                                valueMaps.get(key) == null ? 0 : Double.parseDouble(valueMaps.get(key)
                                                        .toString()));
                                    } else if (keyMaps.get(key).equals("LONG")) {
                                        sqlquery.setLong(
                                                key,
                                                valueMaps.get(key) == null ? 0 : Long.parseLong(valueMaps.get(key)
                                                        .toString()));
                                    } else if (keyMaps.get(key).equals("CLOB")) {
                                        if (valueMaps.get(key) != null) {
                                            Clob clob = (Clob) valueMaps.get(key);
                                            sqlquery.setText(key, IOUtils.toString(clob.getCharacterStream())
                                                    .toString());
                                        } else {
                                            sqlquery.setText(key, "");
                                        }
                                    } else {
                                        sqlquery.setString(key, valueMaps.get(key) == null ? "" : valueMaps.get(key)
                                                .toString());
                                    }
                                }
                                sqlquery.executeUpdate();
                            }
                            // 提取附件
                            CommonFtp myFtp = new CommonFtp(ftp_host, ftp_post, ftp_user_name, ftp_pass_word);
                            try {
                                if (!myFtp.isConnected()) {
                                    myFtp.connect();
                                }
                                String zuid = uuid + ".zip";
                                File downFile = myFtp.downFile("\\", zuid);
                                mongoFileService.importChangeInfo(downFile);
                                myFtp.deleteFile("\\", zuid);
                            } catch (Exception e) {
                                logger.error(e.getMessage(), e);
                            } finally {
                                myFtp.disconnect();
                            }

                            // 删除更新的控制表对应的关系表
                            if (!StringUtils.isBlank(st.getRelationTable())) {// 关系控制表
                                String relationTableStr = st.getRelationTable();
                                String[] relationTableArr = relationTableStr.split(";");
                                for (int j = 0; j < relationTableArr.length; j++) {
                                    SynchronousSourceTable relationTable = synchronousSourceTableService
                                            .getById(relationTableArr[j]);
                                    if (relationTable != null) {
                                        String field = "";
                                        for (SynchronousSourceField synchronousSourceField : relationTable
                                                .getSynchronousSourceFields()) {
                                            if (!StringUtils.isBlank(synchronousSourceField.getForeignKeyTable())
                                                    && synchronousSourceField.getForeignKeyTable().equals(
                                                    st.getTableEnName())) {
                                                field = synchronousSourceField.getFieldEnName();
                                            }
                                        }
                                        String delRelationSql = "delete " + relationTableArr[j] + " o where o." + field
                                                + "='" + uuid + "'";
                                        session2.createSQLQuery(delRelationSql).executeUpdate();
                                    }
                                }
                            }
                        }
                        session2.close();
                        IgnoreLoginUtils.logout();
                    }
                } catch (Exception e) {
                    session2.close();
                    IgnoreLoginUtils.logout();// 退出前置机租户
                    logger.error(e.getMessage(), e);
                    // logger.info(e.getMessage());
                }
            }
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // logger.info(e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public Boolean synchronousDataDelDb() {
        Map<String, SysProperties> sysPropertiess = exchangedataApiFacade.getAllSysProperties("SYSPROPERTIES");
        String tenant = Config.DEFAULT_TENANT;// 默认租户
        String xt_wl = sysPropertiess.get("xt_wl").getProValue();// 内网/外网 in/out
        String fromTenant = tenant;// 租户
        String toTenant = "";
        String ftp_host = "";
        String ftp_user_name = "";
        String ftp_pass_word = "";
        int ftp_post = 21;
        if (xt_wl.equals("in")) {// 当前是内网，内网到内网前置机
            toTenant = sysPropertiess.get("qzj_zh_nw_out").getProValue();
            if (StringUtils.isBlank(toTenant)) {
                return false;
            }
            ftp_host = sysPropertiess.get("qzj_ftp_nw_out_host").getProValue();
            ftp_user_name = sysPropertiess.get("qzj_ftp_nw_out_username").getProValue();
            ftp_pass_word = sysPropertiess.get("qzj_ftp_nw_out_password").getProValue();
            ftp_post = Integer.parseInt(sysPropertiess.get("qzj_ftp_nw_out_post").getProValue());
        } else if (xt_wl.equals("out")) {// 当前是外网，外网到外网前置机
            toTenant = sysPropertiess.get("qzj_zh_ww_out").getProValue();
            if (StringUtils.isBlank(toTenant)) {
                return false;
            }
            ftp_host = sysPropertiess.get("qzj_ftp_ww_out_host").getProValue();
            ftp_user_name = sysPropertiess.get("qzj_ftp_ww_out_username").getProValue();
            ftp_pass_word = sysPropertiess.get("qzj_ftp_ww_out_password").getProValue();
            ftp_post = Integer.parseInt(sysPropertiess.get("qzj_ftp_ww_out_post").getProValue());
        }
        // 删除前置机 输出oracle数据

        // 删除FTP数据，4台机器

        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobDetails> getJobDetails() {
        QueryInfo queryInfo = new QueryInfo();
        return jobDetailsService.query(queryInfo);
    }

    @Override
    public Boolean saveDataOperationLog(DataOperationLog dataOperationLog) {
        // TODO Auto-generated method stub
        try {
            dataOperationLogService.save(dataOperationLog);
            return true;
        } catch (Exception e) {
            logger.info(e.getMessage());
            return false;
        }
    }

    /***************************新版内外网同步*********************************/

    /**
     * 序列化成字符串
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataSynchronousService#serializeEntityToString(java.lang.Object)
     */
    public String serializeEntityToString(Object object) {
        // TODO Auto-generated method stub
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream;
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            String serStr = byteArrayOutputStream.toString("ISO-8859-1");
            serStr = java.net.URLEncoder.encode(serStr, "UTF-8");
            objectOutputStream.close();
            byteArrayOutputStream.close();
            return serStr;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.info(e.getMessage());
            return "";
        }
    }

    /**
     * 反序列化成对象
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataSynchronousService#deserializationToObject(java.lang.String)
     */
    @Override
    public Object deserializationToObject(String serStr) {
        // TODO Auto-generated method stub
        try {
            String redStr = java.net.URLDecoder.decode(serStr, "UTF-8");
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(redStr.getBytes("ISO-8859-1"));
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object object = objectInputStream.readObject();
            objectInputStream.close();
            byteArrayInputStream.close();
            return object;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.info(e.getMessage());
            return new Object();
        }
    }

    /**
     * 获取实体的值
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataSynchronousService#getEntityValue(java.io.Serializable, java.lang.Object[], java.lang.String[], org.hibernate.type.Type[])
     */
    public Map<String, Object> getEntityValue(Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        Map<String, Object> entityValue = new HashMap<String, Object>();
        for (int i = 0; i < state.length; i++) {
            entityValue.put(propertyNames[i].toLowerCase(), state[i]);
            entityValue.put(propertyNames[i].toLowerCase() + "_type", types[i]);
        }
        entityValue.put("uuid", id);
        return entityValue;
    }

    /**
     * 实体字段转成数据库字段
     *
     * @param field
     * @return
     */
    public String toDbField(String field) {
        String dbField = "";
        for (int i = 0; i < field.length(); i++) {
            if (!Character.isLowerCase(field.charAt(i))) {
                if (i == 0) {
                    dbField += (field.charAt(i) + "").toLowerCase();
                } else {
                    dbField += ("_" + field.charAt(i)).toLowerCase();
                }
            } else {
                dbField += field.charAt(i) + "";
            }
        }
        return dbField;
    }

    /**
     * 结构化实体对象
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataSynchronousService#structureEntity(java.lang.Object, java.io.Serializable, java.lang.Object[], java.lang.String[], org.hibernate.type.Type[])
     */
    public Object structureEntity(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        // TODO Auto-generated method stub
        Map<String, Object> entityValue = getEntityValue(id, state, propertyNames, types);
        Object obj = new Object();
        try {
            Class c = Class.forName(entity.getClass().getName());
            obj = c.newInstance();
            Map<String, Method> methodMap = new HashMap<String, Method>();
            Method[] methods = c.getMethods();
            // 遍历对象的方法
            for (Method method : methods) {
                String methodName = method.getName();
                Class[] parmts = method.getParameterTypes();
                methodMap.put(methodName, method);
                // 如果对象的方法以set开头
                if (methodName.startsWith("set")) {
                    // 根据方法名字得到数据表格中字段的名字
                    String columnName = methodName.substring(3, methodName.length());
                    // if (parmts[0] != null && parmts[0] == Set.class) {
                    // //处理1对多的映射关系
                    // System.out.print("columnName:" +
                    // columnName.toLowerCase());
                    // method.invoke(obj,
                    // entityValue.get(columnName.toLowerCase()));
                    // } else {
                    method.invoke(obj, entityValue.get(columnName.toLowerCase()));
                    // }
                }
            }
            // recVer为空时设置为0
            Method getRecVer = methodMap.get("getRecVer");
            Method setRecVer = methodMap.get("setRecVer");
            Object recVerInt = getRecVer.invoke(obj);
            if (recVerInt == null) {
                setRecVer.invoke(obj, 0);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.info(e.getMessage());
        }
        return obj;
    }

    /**
     * 数据变更切面执行的业务
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataSynchronousService#logEntityInterceptorUnit(java.lang.Object, java.io.Serializable, java.lang.Object[], java.lang.String[], org.hibernate.type.Type[], java.lang.Integer)
     */
    @Override
    @Transactional
    public void logEntityInterceptorTool(Object entity, Serializable id, Object[] state, String[] propertyNames,
                                         Type[] types, Integer methodNum) {
        int xt_wl = Integer.parseInt(Config.getValue("synchronous.xt_wl"));
        String donotSyn = Config.getValue("synchronous.donot");

        // Annotation[] anno = entity.getClass().getAnnotations();
        // String tableName = "";
        // for (int i = 0; i < anno.length; i++) {
        // if (anno[i] instanceof Table) {
        // Table table = (Table) anno[i];
        // tableName = table.name();
        // }
        // }

        // TODO Auto-generated method stub
        if (!entity.getClass().getSimpleName().equals("DataOperationLog")
                && donotSyn.indexOf(entity.getClass().getName() + ";") < 0) {// 数据操作记录表不进行记录
            Map<String, Object> dataMap = getEntityValue(id, state, propertyNames, types);
            String suuid = dataMap.get("uuid".toLowerCase()) == null ? "" : dataMap.get("uuid".toLowerCase())
                    .toString();
            int srecVer = dataMap.get("recVer".toLowerCase()) == null ? 0 : (Integer) dataMap.get("recVer"
                    .toLowerCase());
            DataOperationLog dataOperationLog = new DataOperationLog();
            Date screateTime = dataMap.get("createTime".toLowerCase()) == null ? new Date() : (Date) dataMap
                    .get("createTime".toLowerCase());
            String smodifier = dataMap.get("modifier".toLowerCase()) == null ? "" : dataMap.get(
                    "modifier".toLowerCase()).toString();
            Date smodifyTime = dataMap.get("modifyTime".toLowerCase()) == null ? new Date() : (Date) dataMap
                    .get("modifyTime".toLowerCase());
            String screator = dataMap.get("creator".toLowerCase()) == null ? "" : dataMap.get("creator".toLowerCase())
                    .toString();
            int size = this.getDataCountByJdbc("select count(*) from is_data_operation_log t where t.suuid='" + suuid
                    + "' and t.srec_ver = " + srecVer + " and t.action = " + methodNum);
            if (size == 0) {
                // int size = this.getDataCountByJdbc("select count(*) from " +
                // tableName + " t where t.uuid='" + suuid
                // + "' and t.rec_ver = " + srecVer);
                // if (size == 0 && methodNum != 2) {
                dataOperationLog.setStableType(1);
                dataOperationLog.setStableName(entity.getClass().getName());
                dataOperationLog.setSuuid(suuid);
                dataOperationLog.setSrecVer(srecVer);
                dataOperationLog.setScreator(screator);
                dataOperationLog.setScreateTime(screateTime);
                dataOperationLog.setSmodifier(smodifier);
                dataOperationLog.setSmodifyTime(smodifyTime);
                Object obj = structureEntity(entity, id, state, propertyNames, types);
                String serializeStr = serializeEntityToString(obj);
                dataOperationLog.setContent(Hibernate.getLobCreator(this.dao.getSession()).createClob(serializeStr));
                dataOperationLog.setStatus(1);// 未同步
                dataOperationLog.setDirection(xt_wl);// 内/外
                dataOperationLog.setAction(methodNum);// 变更
                saveDataOperationLog(dataOperationLog);
            }
        }
    }

    @Override
    @Transactional
    public void test_logEntityInterceptorTool(Object entity, Serializable id, Object[] state, String[] propertyNames,
                                              Type[] types, Integer methodNum) {
        // TODO Auto-generated method stub
        Map<String, Object> entityValue = getEntityValue(id, state, propertyNames, types);
        Object obj = new Object();
        try {
            Class c = Class.forName(entity.getClass().getName());
            if (c.getSimpleName().equals("User")) {
                obj = c.newInstance();
                Map<String, Method> methodMap = new HashMap<String, Method>();
                Method[] methods = c.getMethods();
                // 遍历对象的方法
                for (Method method : methods) {
                    String methodName = method.getName();
                    String[] type_ = method.getReturnType().toString().split("\\.");
                    String type = type_[(type_.length - 1)];
                    methodMap.put(methodName, method);
                    // 如果对象的方法以set开头
                    if (methodName.startsWith("set")) {
                        // 根据方法名字得到数据表格中字段的名字
                        String columnName = methodName.substring(3, methodName.length());
                        if (type.equals("Set") || type.equals("List")) {
                            // 处理1对多的映射关系
                            // System.out.print("columnName:" +
                            // columnName.toLowerCase());
                            // Object s =
                            // entityValue.get(columnName.toLowerCase());
                            method.invoke(obj, entityValue.get(columnName.toLowerCase()));
                        } else {
                            method.invoke(obj, entityValue.get(columnName.toLowerCase()));
                        }
                    } else if (methodName.startsWith("get")) {

                    }
                }
                // recVer为空时设置为0
                Method getRecVer = methodMap.get("getRecVer");
                Method setRecVer = methodMap.get("setRecVer");
                Object recVerInt = getRecVer.invoke(obj);
                if (recVerInt == null) {
                    setRecVer.invoke(obj, 0);
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.info(e.getMessage());
        }
    }

    @Override
    @Transactional
    public Boolean saveEntityObj(Object obj, String entityName) {
        try {
            Class c = Class.forName(entityName);
            // String tableName =
            // c.getName().substring(c.getName().lastIndexOf(".") + 1,
            // c.getName().length());
            Annotation[] anno = obj.getClass().getAnnotations();
            String tableName = "";
            for (int i = 0; i < anno.length; i++) {
                if (anno[i] instanceof Table) {
                    Table table = (Table) anno[i];
                    tableName = table.name();
                }
            }
            Map<String, Method> methodMap = new HashMap<String, Method>();
            Method[] methods = c.getMethods();
            // 遍历对象的方法
            for (Method method : methods) {
                String methodName = method.getName();
                if (((methodName.startsWith("get") && !methodName.startsWith("getClass")) || methodName
                        .startsWith("set")) && methodName.toLowerCase().indexOf("attach") < 0) {
                    methodMap.put(methodName, method);
                }
            }
            // Creator添加前缀，保证不会写入同步操作记录
            Method getCreator = methodMap.get("getCreator");
            Method setCreator = methodMap.get("setCreator");
            String creatorStr = getCreator.invoke(obj).toString();
            setCreator.invoke(obj, "syn_" + creatorStr);
            // 判断是否存在数据,不存在时jdbc插入数据
            Method getUuid = methodMap.get("getUuid");
            String uuid = getUuid.invoke(obj).toString();
            String selectSql = "select * from " + tableName + " o where o.uuid='" + uuid + "'";
            List dataList = this.dao.getSession().createSQLQuery(selectSql)
                    .setResultTransformer(ExchangeDataResultTransformer.INSTANCE).list();
            if (dataList.size() == 0) {
                jdbcInsertData(obj, entityName);
            } else {

            }
            // Hibernate更新数据
            Serializable entity = dao.get(c, uuid);
            entity = copyEntityObject((Serializable) obj, entity, entityName);
            dao.save(entity);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return null;
        }
        return true;
    }

    /**
     * 修改创建者字段
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataSynchronousService#modifyEntityCreator(java.lang.Object, java.lang.String)
     */
    public Object modifyEntityCreator(Object obj, String entityName) {
        try {
            Class c = Class.forName(entityName);
            Map<String, Method> methodMap = new HashMap<String, Method>();
            Method[] methods = c.getMethods();
            // 遍历对象的方法
            for (Method method : methods) {
                String methodName = method.getName();
                if (((methodName.startsWith("get") && !methodName.startsWith("getClass")) || methodName
                        .startsWith("set")) && methodName.toLowerCase().indexOf("attach") < 0) {
                    methodMap.put(methodName, method);
                }
            }
            // 如果参数为String类型，则从结果集中按照列名取得对应的值，并且执行改set方法
            Method getCreator = methodMap.get("getCreator");
            Method setCreator = methodMap.get("setCreator");
            String creatorStr = getCreator.invoke(obj).toString();
            setCreator.invoke(obj, "syn_" + creatorStr);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return null;
        }
        return obj;
    }

    /**
     * 还原创建者字段
     *
     * @param entity
     * @param state
     * @param propertyNames
     * @param types
     * @return
     */
    public Object backEntityCreator(Object entity, Object[] state, String[] propertyNames, Type[] types) {
        try {
            Class c = entity.getClass();
            Map<String, Method> methodMap = new HashMap<String, Method>();
            Method[] methods = c.getMethods();
            // 遍历对象的方法
            for (Method method : methods) {
                String methodName = method.getName();
                if (((methodName.startsWith("get") && !methodName.startsWith("getClass")) || methodName
                        .startsWith("set")) && methodName.toLowerCase().indexOf("attach") < 0) {
                    methodMap.put(methodName, method);
                }
            }
            Method getCreator = methodMap.get("getCreator");
            Method setCreator = methodMap.get("setCreator");
            String creatorStr = getCreator.invoke(entity).toString();
            if (!creatorStr.startsWith("syn_")) {
                return null;
            } else {
                setCreator.invoke(entity, creatorStr.replace("syn_", ""));
                for (int i = 0; i < state.length; i++) {
                    if (propertyNames[i].toLowerCase().equals("creator")) {
                        state[i] = creatorStr.replace("syn_", "");
                    }
                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return entity;
    }

    @Transactional
    public void jdbcInsertData(Object obj, String entityName) {
        // 定义一个sql字符串
        String sql = "insert into ";
        // 得到对象的类
        Class c = obj.getClass();
        // 得到对象中所有的方法
        Method[] methods = c.getMethods();
        // 得到对象类的名字
        String cName = c.getName();
        // 从类的名字中解析出表名
        Annotation[] anno = obj.getClass().getAnnotations();
        String tableName = "";
        for (int i = 0; i < anno.length; i++) {
            if (anno[i] instanceof Table) {
                Table table = (Table) anno[i];
                tableName = table.name();
            }
        }
        sql += tableName + "(";
        List<String> mList = new ArrayList<String>();
        List vList = new ArrayList();
        for (Method method : methods) {
            String mName = method.getName();
            if (mName.startsWith("get") && !mName.startsWith("getClass") && !mName.equals("getAttach")) {
                String fieldName = mName.substring(3, mName.length());
                mList.add(toDbField(fieldName));
                try {
                    Object value = method.invoke(obj, null);
                    if (value instanceof String) {
                        vList.add("'" + value + "'");
                    } else {
                        vList.add(null);
                    }
                } catch (Exception e) {
                    logger.info(e.getMessage());
                }
            }
        }
        for (int i = 0; i < mList.size(); i++) {
            if (i < mList.size() - 1) {
                sql += mList.get(i) + ",";
            } else {
                sql += mList.get(i) + ") values(";
            }
        }
        for (int i = 0; i < vList.size(); i++) {
            if (i < vList.size() - 1) {
                sql += vList.get(i) + ",";
            } else {
                sql += vList.get(i) + ")";
            }
        }
        this.dao.getSession().createSQLQuery(sql).executeUpdate();
        // this.dao.getSession().flush();
    }

    /**
     * 复制对象
     *
     * @param source
     * @param target
     * @param entityName
     * @return
     */
    public Serializable copyEntityObject(Serializable source, Serializable target, String entityName) {
        try {
            Class c = Class.forName(entityName);
            Map<String, Method> methodMap = new HashMap<String, Method>();
            Method[] methods = c.getMethods();
            String fildNames = ",";
            // 遍历对象的方法
            for (Method method : methods) {
                String methodName = method.getName();
                if (((methodName.startsWith("get") && !methodName.startsWith("getClass")) || methodName
                        .startsWith("set")) && methodName.toLowerCase().indexOf("attach") < 0) {
                    // && !methodName.equals("getUuid")) {
                    methodMap.put(methodName, method);
                    if (methodName.startsWith("get")) {
                        String fieldName = methodName.substring(3, methodName.length());
                        fildNames += fieldName + ",";
                    }
                }
            }
            String[] fildNameArray = fildNames.split(",");
            for (int i = 0; i < fildNameArray.length; i++) {
                if (!StringUtils.isBlank(fildNameArray[i])) {
                    try {
                        Method get = methodMap.get("get" + fildNameArray[i]);
                        Method set = methodMap.get("set" + fildNameArray[i]);
                        set.invoke(target, get.invoke(source));
                    } catch (IllegalArgumentException e) {
                        // TODO Auto-generated catch block
                        logger.info(e.getMessage());
                    } catch (IllegalAccessException e) {
                        // TODO Auto-generated catch block
                        logger.info(e.getMessage());
                    } catch (InvocationTargetException e) {
                        // TODO Auto-generated catch block
                        logger.info(e.getMessage());
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            logger.info(e.getMessage());
        }
        return target;
    }

    @Override
    @Transactional
    public Boolean newSynchronousOutData() {
        /**
         * 数据同步到前置机
         * 1、获取同步的信息
         * 2、提取系统数据
         * 3、变更数据到前置机
         * 4、修改系统数据状态
         */
        // TODO Auto-generated method stub
        try {
            /***********************1、获取同步的信息****************************/
            Map<String, SysProperties> sysPropertiess = exchangedataApiFacade.getAllSysProperties("SYSPROPERTIES");
            String tenant = Config.DEFAULT_TENANT;// 默认租户
            String xt_wl = sysPropertiess.get("xt_wl").getProValue();// 内网/外网
            // in/out
            String fromTenant = tenant;// 租户
            String toTenant = "";
            String ftp_host = "";
            String ftp_user_name = "";
            String ftp_pass_word = "";
            int direction = 0;// 1内/2外
            int ftp_post = 21;
            if (xt_wl.equals("in")) {// 当前是内网，内网到内网前置机
                direction = 1;
                toTenant = sysPropertiess.get("qzj_zh_nw_out").getProValue();
                if (StringUtils.isBlank(toTenant)) {
                    return false;
                }
                ftp_host = sysPropertiess.get("qzj_ftp_nw_out_host").getProValue();
                ftp_user_name = sysPropertiess.get("qzj_ftp_nw_out_username").getProValue();
                ftp_pass_word = sysPropertiess.get("qzj_ftp_nw_out_password").getProValue();
                ftp_post = Integer.parseInt(sysPropertiess.get("qzj_ftp_nw_out_post").getProValue());
            } else if (xt_wl.equals("out")) {// 当前是外网，外网到外网前置机
                direction = 2;
                toTenant = sysPropertiess.get("qzj_zh_ww_out").getProValue();
                if (StringUtils.isBlank(toTenant)) {
                    return false;
                }
                ftp_host = sysPropertiess.get("qzj_ftp_ww_out_host").getProValue();
                ftp_user_name = sysPropertiess.get("qzj_ftp_ww_out_username").getProValue();
                ftp_pass_word = sysPropertiess.get("qzj_ftp_ww_out_password").getProValue();
                ftp_post = Integer.parseInt(sysPropertiess.get("qzj_ftp_ww_out_post").getProValue());
            }
            int interval = Integer.parseInt(sysPropertiess.get("syn_fail_agin_interval").getProValue());// 同步中间隔时间再同步

            /***********************2、提取系统数据****************************/
            List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
            Session session2 = this.dao.getSession();
            try {
                String updateSql = "update is_data_operation_log o set o.syn_time=:syn_time where o.syn_time is null";
                SQLQuery sqlquery = session2.createSQLQuery(updateSql);
                sqlquery.setTimestamp("syn_time", new Date());
                sqlquery.executeUpdate();

                String querySql = "select * from is_data_operation_log o where (o.status=1 or o.status=3 or (o.status=2 and (sysdate-cast(o.syn_time as date))*24*60>"
                        + interval + ")) and o.direction = " + direction;// 未同步、同步失败、同步中超过jiange分钟
                dataList = session2.createSQLQuery(querySql)
                        .setResultTransformer(ExchangeDataResultTransformer.INSTANCE).list();
                if (dataList.size() == 0) {
                    logger.info("************* 本次同步0条数据 ************");
                    return true;
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                logger.error(e.getMessage(), e);
            } finally {
                session2.close();
                IgnoreLoginUtils.logout();// 退出源租户
            }

            /****************************3、变更数据到前置机*****************************/
            IgnoreLoginUtils.login(toTenant, toTenant);
            Session session3 = this.dao.getSession();
            try {
                for (Map<String, Object> dataItem : dataList) {
                    int stable_type = dataItem.get("stable_type") == null ? 0 : Integer.parseInt(dataItem.get(
                            "stable_type").toString());
                    String uuid = dataItem.get("uuid").toString();
                    if (stable_type == 4) {// 文件夹 先上传附件到ftp再插入数据
                        String suuid = dataItem.get("suuid").toString();
                        File zipFile = mongoFileService.exportChangeInfo(suuid, null);
                        // 附件的处理
                        if (zipFile != null) {
                            CommonFtp myFtp = new CommonFtp(ftp_host, ftp_post, ftp_user_name, ftp_pass_word);
                            try {
                                if (!myFtp.isConnected()) {
                                    myFtp.connect();
                                }
                                String zuid = uuid + ".zip";
                                // 文件名改成操作记录uuid
                                myFtp.deleteFile("\\", zuid);
                                myFtp.uploadFile(zipFile, "\\");// 上传
                                myFtp.rename("\\", zuid + ".oa");// 上传完成后修改文件名,去掉.oa后缀，保证同步成功（只有.zip与.rar支持同步）
                            } catch (Exception e) {
                                logger.error(e.getMessage(), e);
                            } finally {
                                myFtp.disconnect();
                            }
                        }
                    }
                    String querySql2 = "select o.uuid from is_data_operation_log o where o.uuid='" + uuid + "'";// 查询是否存在数据
                    List<Map<String, Object>> dataList2 = session3.createSQLQuery(querySql2)
                            .setResultTransformer(ExchangeDataResultTransformer.INSTANCE).list();
                    if (dataList2.size() > 0) {
                        String delSql = "delete is_data_operation_log o where o.uuid='" + uuid + "'";
                        session3.createSQLQuery(delSql).executeUpdate();
                    }

                    // 插入数据
                    String insertSql = "insert into is_data_operation_log "
                            + "(uuid,creator,create_time,modifier,modify_time,rec_ver,action,content,direction,remark,syn_time,suuid,status,stable_type,stable_name,srec_ver,smodify_time,smodifier,screator,screate_time,byzd0,byzd1,byzd2,byzd3,byzd4,byzd5,byzd6,byzd7,byzd8,byzd9) "
                            + "values "
                            + "(:uuid,:creator,:create_time,:modifier,:modify_time,:rec_ver,:action,:content,:direction,:remark,:syn_time,:suuid,:status,:stable_type,:stable_name,:srec_ver,:smodify_time,:smodifier,:screator,:screate_time,:byzd0,:byzd1,:byzd2,:byzd3,:byzd4,:byzd5,:byzd6,:byzd7,:byzd8,:byzd9)";
                    SQLQuery sqlquery = session3.createSQLQuery(insertSql);
                    // STRING
                    sqlquery.setString("uuid", dataItem.get("uuid") == null ? "" : dataItem.get("uuid").toString());
                    sqlquery.setString("suuid", dataItem.get("suuid") == null ? "" : dataItem.get("suuid").toString());
                    sqlquery.setString("creator", dataItem.get("creator") == null ? "" : dataItem.get("creator")
                            .toString());
                    sqlquery.setString("modifier", dataItem.get("modifier") == null ? "" : dataItem.get("modifier")
                            .toString());
                    sqlquery.setString("remark", dataItem.get("remark") == null ? "" : dataItem.get("remark")
                            .toString());
                    sqlquery.setString("stable_name",
                            dataItem.get("stable_name") == null ? "" : dataItem.get("stable_name").toString());
                    sqlquery.setString("smodifier", dataItem.get("smodifier") == null ? "" : dataItem.get("smodifier")
                            .toString());
                    sqlquery.setString("screator", dataItem.get("screator") == null ? "" : dataItem.get("screator")
                            .toString());
                    sqlquery.setString("byzd0", dataItem.get("byzd0") == null ? "" : dataItem.get("byzd0").toString());
                    sqlquery.setString("byzd1", dataItem.get("byzd1") == null ? "" : dataItem.get("byzd1").toString());
                    sqlquery.setString("byzd2", dataItem.get("byzd2") == null ? "" : dataItem.get("byzd2").toString());
                    sqlquery.setString("byzd3", dataItem.get("byzd3") == null ? "" : dataItem.get("byzd3").toString());
                    sqlquery.setString("byzd4", dataItem.get("byzd4") == null ? "" : dataItem.get("byzd4").toString());
                    sqlquery.setString("byzd5", dataItem.get("byzd5") == null ? "" : dataItem.get("byzd5").toString());
                    sqlquery.setString("byzd6", dataItem.get("byzd6") == null ? "" : dataItem.get("byzd6").toString());
                    sqlquery.setString("byzd7", dataItem.get("byzd7") == null ? "" : dataItem.get("byzd7").toString());
                    // DATE
                    sqlquery.setTimestamp("create_time",
                            dataItem.get("create_time") == null ? null : (Date) dataItem.get("create_time"));
                    sqlquery.setTimestamp("modify_time",
                            dataItem.get("modify_time") == null ? null : (Date) dataItem.get("modify_time"));
                    sqlquery.setTimestamp("syn_time",
                            dataItem.get("syn_time") == null ? null : (Date) dataItem.get("syn_time"));
                    sqlquery.setTimestamp("smodify_time",
                            dataItem.get("smodify_time") == null ? null : (Date) dataItem.get("smodify_time"));
                    sqlquery.setTimestamp("screate_time",
                            dataItem.get("screate_time") == null ? null : (Date) dataItem.get("screate_time"));
                    // INTEGER
                    sqlquery.setInteger("rec_ver",
                            dataItem.get("rec_ver") == null ? 0 : Integer.parseInt(dataItem.get("rec_ver").toString()));
                    sqlquery.setInteger("action",
                            dataItem.get("action") == null ? 0 : Integer.parseInt(dataItem.get("action").toString()));
                    sqlquery.setInteger(
                            "direction",
                            dataItem.get("direction") == null ? 0 : Integer.parseInt(dataItem.get("direction")
                                    .toString()));
                    sqlquery.setInteger("status", 2);// 同步中
                    sqlquery.setInteger(
                            "srec_ver",
                            dataItem.get("srec_ver") == null ? 0 : Integer
                                    .parseInt(dataItem.get("srec_ver").toString()));
                    sqlquery.setInteger(
                            "stable_type",
                            dataItem.get("stable_type") == null ? 0 : Integer.parseInt(dataItem.get("stable_type")
                                    .toString()));
                    // CLOB
                    sqlquery.setText(
                            "content",
                            dataItem.get("content") == null ? "" : IOUtils.toString(
                                    ((Clob) dataItem.get("content")).getCharacterStream()).toString());
                    sqlquery.setText(
                            "byzd8",
                            dataItem.get("byzd8") == null ? "" : IOUtils.toString(
                                    ((Clob) dataItem.get("byzd8")).getCharacterStream()).toString());
                    sqlquery.setText(
                            "byzd9",
                            dataItem.get("byzd9") == null ? "" : IOUtils.toString(
                                    ((Clob) dataItem.get("byzd9")).getCharacterStream()).toString());

                    sqlquery.executeUpdate();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                logger.error(e.getMessage(), e);
            } finally {
                session3.close();
                IgnoreLoginUtils.logout();// 退出源租户
            }

            /****************************4、修改系统数据状态*****************************/
            IgnoreLoginUtils.login(fromTenant, fromTenant);
            Session session4 = this.dao.getSession();
            try {
                String uuids = "";
                for (Map<String, Object> dataItem : dataList) {
                    String uuid = dataItem.get("uuid").toString();
                    uuids += ",'" + uuid + "'";
                }
                session4.createSQLQuery(
                        "update is_data_operation_log o set o.status=2 where o.uuid in (" + uuids.replaceFirst(",", "")
                                + ")").executeUpdate();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                session4.close();
                IgnoreLoginUtils.logout();// 退出源租户
            }
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // logger.info(e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public Boolean newSynchronousInData() {
        /**
         * 提取前置机的数据
         * 1、获取同步的信息
         * 2、提取前置机数据(同步中，同步成功/同步失败(握手))
         * 3、变更数据到系统,还原数据
         * 4、插入流出前置机同步状态(实现握手过程)
         */
        // TODO Auto-generated method stub
        try {
            /***********************1、获取同步的信息****************************/
            Map<String, SysProperties> sysPropertiess = exchangedataApiFacade.getAllSysProperties("SYSPROPERTIES");
            String tenant = Config.DEFAULT_TENANT;// 默认租户
            String xt_wl = sysPropertiess.get("xt_wl").getProValue();// 内网/外网
            // in/out
            String fromTenant = "";
            String toTenant = tenant;
            String outTenant = "";
            String ftp_host = "";
            String ftp_user_name = "";
            String ftp_pass_word = "";
            int ftp_post = 21;
            int direction = 0;// 1内/2外
            if (xt_wl.equals("in")) {// 当前是内网，从内网前置机提取数据
                direction = 1;// 内外提取外网数据
                fromTenant = sysPropertiess.get("qzj_zh_nw_in").getProValue();
                outTenant = sysPropertiess.get("qzj_zh_nw_out").getProValue();
                if (StringUtils.isBlank(fromTenant)) {
                    return false;
                }
                ftp_host = sysPropertiess.get("qzj_ftp_nw_in_host").getProValue();
                ftp_user_name = sysPropertiess.get("qzj_ftp_nw_in_username").getProValue();
                ftp_pass_word = sysPropertiess.get("qzj_ftp_nw_in_password").getProValue();
                ftp_post = Integer.parseInt(sysPropertiess.get("qzj_ftp_nw_in_post").getProValue());
            } else if (xt_wl.equals("out")) {// 当前是外网，从外网前置机提取数据
                direction = 2;// 外外提取内网数据
                fromTenant = sysPropertiess.get("qzj_zh_ww_in").getProValue();
                outTenant = sysPropertiess.get("qzj_zh_nw_out").getProValue();
                if (StringUtils.isBlank(fromTenant)) {
                    return false;
                }
                ftp_host = sysPropertiess.get("qzj_ftp_ww_in_host").getProValue();
                ftp_user_name = sysPropertiess.get("qzj_ftp_ww_in_username").getProValue();
                ftp_pass_word = sysPropertiess.get("qzj_ftp_ww_in_password").getProValue();
                ftp_post = Integer.parseInt(sysPropertiess.get("qzj_ftp_ww_in_post").getProValue());
            }
            IgnoreLoginUtils.logout();// 退出平台租户

            /***********************2、提取前置机数据(同步中，同步成功/同步失败(握手))****************************/
            List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
            IgnoreLoginUtils.login(fromTenant, fromTenant);// 虚拟登陆前置机租户
            Session session2 = this.dao.getSession();
            try {
                // 提取同步中及反馈的数据
                dataList = session2
                        .createSQLQuery(
                                "select * from is_data_operation_log d where (d.status=2 or d.status=6) and d.direction = "
                                        + (direction == 1 ? 2 : 1) + " and d.action!=2 order by d.create_time asc")
                        .setResultTransformer(ExchangeDataResultTransformer.INSTANCE).list();
                List delDataList = session2
                        .createSQLQuery(
                                "select * from is_data_operation_log d where (d.status=2 or d.status=6) and d.direction = "
                                        + (direction == 1 ? 2 : 1) + " and d.action=2 order by d.create_time desc")
                        .setResultTransformer(ExchangeDataResultTransformer.INSTANCE).list();
                dataList.addAll(delDataList);
                if (dataList.size() == 0) {
                    logger.info("************* 本次提取0条数据 ************");
                    return true;
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                logger.info(e.getMessage());
            } finally {
                session2.close();
                IgnoreLoginUtils.logout();// 退出前置机租户
            }

            /***********************3、变更数据到系统*****************************/
            Map<String, String> stupStatus = new HashMap<String, String>();
            IgnoreLoginUtils.login(toTenant, toTenant);// 虚拟平台租户
            Session session3 = this.dao.getSession();
            try {
                for (Map<String, Object> dataItem : dataList) {
                    String uuid = dataItem.get("uuid").toString();
                    int status = Integer.parseInt(dataItem.get("status").toString());

                    /******比较数据是否已经处理过*********/
                    String querySql2 = "select o.uuid from is_data_operation_log o where o.uuid='" + uuid + "'";// 查询是否存在数据
                    List<Map<String, Object>> dataList2 = session3.createSQLQuery(querySql2)
                            .setResultTransformer(ExchangeDataResultTransformer.INSTANCE).list();
                    if (dataList2.size() == 0) {
                        String insertSql = "insert into is_data_operation_log "
                                + "(uuid,creator,create_time,modifier,modify_time,rec_ver,action,content,direction,remark,syn_time,suuid,status,stable_type,stable_name,srec_ver,smodify_time,smodifier,screator,screate_time,byzd0,byzd1,byzd2,byzd3,byzd4,byzd5,byzd6,byzd7,byzd8,byzd9) "
                                + "values "
                                + "(:uuid,:creator,:create_time,:modifier,:modify_time,:rec_ver,:action,:content,:direction,:remark,:syn_time,:suuid,:status,:stable_type,:stable_name,:srec_ver,:smodify_time,:smodifier,:screator,:screate_time,:byzd0,:byzd1,:byzd2,:byzd3,:byzd4,:byzd5,:byzd6,:byzd7,:byzd8,:byzd9)";
                        SQLQuery sqlquery = session3.createSQLQuery(insertSql);
                        // STRING
                        sqlquery.setString("uuid", dataItem.get("uuid") == null ? "" : dataItem.get("uuid").toString());
                        sqlquery.setString("suuid", dataItem.get("suuid") == null ? "" : dataItem.get("suuid")
                                .toString());
                        sqlquery.setString("creator", dataItem.get("creator") == null ? "" : dataItem.get("creator")
                                .toString());
                        sqlquery.setString("modifier", dataItem.get("modifier") == null ? "" : dataItem.get("modifier")
                                .toString());
                        sqlquery.setString("remark", dataItem.get("remark") == null ? "" : dataItem.get("remark")
                                .toString());
                        sqlquery.setString("stable_name",
                                dataItem.get("stable_name") == null ? "" : dataItem.get("stable_name").toString());
                        sqlquery.setString("smodifier",
                                dataItem.get("smodifier") == null ? "" : dataItem.get("smodifier").toString());
                        sqlquery.setString("screator", dataItem.get("screator") == null ? "" : dataItem.get("screator")
                                .toString());
                        sqlquery.setString("byzd0", dataItem.get("byzd0") == null ? "" : dataItem.get("byzd0")
                                .toString());
                        sqlquery.setString("byzd1", dataItem.get("byzd1") == null ? "" : dataItem.get("byzd1")
                                .toString());
                        sqlquery.setString("byzd2", dataItem.get("byzd2") == null ? "" : dataItem.get("byzd2")
                                .toString());
                        sqlquery.setString("byzd3", dataItem.get("byzd3") == null ? "" : dataItem.get("byzd3")
                                .toString());
                        sqlquery.setString("byzd4", dataItem.get("byzd4") == null ? "" : dataItem.get("byzd4")
                                .toString());
                        sqlquery.setString("byzd5", dataItem.get("byzd5") == null ? "" : dataItem.get("byzd5")
                                .toString());
                        sqlquery.setString("byzd6", dataItem.get("byzd6") == null ? "" : dataItem.get("byzd6")
                                .toString());
                        sqlquery.setString("byzd7", dataItem.get("byzd7") == null ? "" : dataItem.get("byzd7")
                                .toString());
                        // DATE
                        sqlquery.setTimestamp("create_time", dataItem.get("create_time") == null ? null
                                : (Date) dataItem.get("create_time"));
                        sqlquery.setTimestamp("modify_time", dataItem.get("modify_time") == null ? null
                                : (Date) dataItem.get("modify_time"));
                        sqlquery.setTimestamp("syn_time",
                                dataItem.get("syn_time") == null ? null : (Date) dataItem.get("syn_time"));
                        sqlquery.setTimestamp("smodify_time", dataItem.get("smodify_time") == null ? null
                                : (Date) dataItem.get("smodify_time"));
                        sqlquery.setTimestamp("screate_time", dataItem.get("screate_time") == null ? null
                                : (Date) dataItem.get("screate_time"));
                        // INTEGER
                        sqlquery.setInteger(
                                "rec_ver",
                                dataItem.get("rec_ver") == null ? 0 : Integer.parseInt(dataItem.get("rec_ver")
                                        .toString()));
                        sqlquery.setInteger(
                                "action",
                                dataItem.get("action") == null ? 0 : Integer
                                        .parseInt(dataItem.get("action").toString()));
                        sqlquery.setInteger(
                                "direction",
                                dataItem.get("direction") == null ? 0 : Integer.parseInt(dataItem.get("direction")
                                        .toString()));
                        sqlquery.setInteger("status", 2);
                        sqlquery.setInteger(
                                "srec_ver",
                                dataItem.get("srec_ver") == null ? 0 : Integer.parseInt(dataItem.get("srec_ver")
                                        .toString()));
                        sqlquery.setInteger(
                                "stable_type",
                                dataItem.get("stable_type") == null ? 0 : Integer.parseInt(dataItem.get("stable_type")
                                        .toString()));
                        // CLOB
                        sqlquery.setText(
                                "content",
                                dataItem.get("content") == null ? "" : IOUtils.toString(
                                        ((Clob) dataItem.get("content")).getCharacterStream()).toString());
                        sqlquery.setText(
                                "byzd8",
                                dataItem.get("byzd8") == null ? "" : IOUtils.toString(
                                        ((Clob) dataItem.get("byzd8")).getCharacterStream()).toString());
                        sqlquery.setText(
                                "byzd9",
                                dataItem.get("byzd9") == null ? "" : IOUtils.toString(
                                        ((Clob) dataItem.get("byzd9")).getCharacterStream()).toString());
                        sqlquery.executeUpdate();
                        if (status == 6) {// 解析反馈的数据
                            String suuid = dataItem.get("suuid").toString();
                            int statusValue = Integer.parseInt(dataItem.get("byzd0").toString());
                            session3.createSQLQuery(
                                    "update into is_data_operation_log l set l.status=" + statusValue
                                            + " where s.uuid='" + suuid + "'").executeUpdate();
                            stupStatus.put(uuid + "_suuid", suuid);
                            stupStatus.put(uuid + "_statusValue", statusValue + "");
                        } else {
                            // 还原数据!!!重点处理
                            FtpDataBean ftpDataBean = new FtpDataBean();
                            ftpDataBean.setFtp_host(ftp_host);
                            ftpDataBean.setFtp_pass_word(ftp_pass_word);
                            ftpDataBean.setFtp_post(ftp_post);
                            ftpDataBean.setFtp_user_name(ftp_user_name);
                            if (backData(dataOperationLogService.getOne(uuid), ftpDataBean)) {
                                stupStatus.put(uuid, "success");
                            } else {
                                stupStatus.put(uuid, "fail");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                logger.info(e.getMessage());
            } finally {
                session3.close();
                IgnoreLoginUtils.logout();
            }
            /***********************4、插入流出前置机同步状态(实现握手过程)，反馈的数据更新前置机状态*****************************/
            IgnoreLoginUtils.login(outTenant, outTenant);
            Session session4 = this.dao.getSession();
            try {
                for (Map<String, Object> dataItem : dataList) {
                    String uuid = dataItem.get("uuid").toString();
                    if (stupStatus.get(uuid) != null) {
                        int status = Integer.parseInt(dataItem.get("status").toString());// 数据状态
                        if (status == 6) {// 修改前置机,同步状态
                            try {
                                session4.createSQLQuery(
                                        "update is_data_operation_log l set l.status="
                                                + stupStatus.get(uuid + "_statusValue") + " where l.uuid="
                                                + stupStatus.get(uuid + "_suuid")).executeUpdate();
                            } catch (Exception e) {
                                logger.error(e.getMessage(), e);
                            } finally {
                                session4.close();
                                IgnoreLoginUtils.logout();
                            }
                        } else {
                            // 插入
                            String insertSql = "insert into is_data_operation_log "
                                    + "(uuid,status,suuid,byzd0,direction) " + "values "
                                    + "(:uuid,:status,:suuid,:byzd0,:direction)";
                            SQLQuery sqlquery = session4.createSQLQuery(insertSql);
                            // STRING
                            sqlquery.setString("uuid", UUID.randomUUID().toString());
                            sqlquery.setInteger("status", 6);
                            sqlquery.setString("suuid", uuid);
                            if (stupStatus.get(uuid).equals("success")) {
                                sqlquery.setString("byzd0", "4");
                            } else {
                                sqlquery.setString("byzd0", "3");
                            }
                            sqlquery.setInteger("direction", direction);
                            sqlquery.executeUpdate();
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                logger.info(e.getMessage());
            } finally {
                session4.close();
                IgnoreLoginUtils.logout();
            }
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            logger.info(e.getMessage());
            return false;
        }
    }

    @Transactional
    public Boolean backEntity(DataOperationLog d) {
        // TODO Auto-generated method stub
        try {
            String content = IOUtils.toString(d.getContent().getCharacterStream()) == null ? "" : IOUtils.toString(
                    d.getContent().getCharacterStream()).toString();
            Object obj = this.deserializationToObject(content);
            // obj = exchangeDataSynchronousService.modifyEntityCreator(obj,
            // d.getStableName());
            // synchronousDao.save(obj);
            // this.dao.save((IdEntity) obj);
            this.saveEntityObj(obj, d.getStableName());
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // logger.info(e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public Boolean backData(DataOperationLog d, FtpDataBean ftpDataBean) {
        try {
            int stableType = d.getStableType();
            int action = d.getAction();
            String content = IOUtils.toString(d.getContent().getCharacterStream()) == null ? "" : IOUtils.toString(
                    d.getContent().getCharacterStream()).toString();
            Object obj = this.deserializationToObject(content);
            // 得到对象的类
            Class c = obj.getClass();
            String entityName = c.getName();
            // 得到对象中所有的方法
            Method[] methods = c.getMethods();
            // 从类的名字中解析出表名
            Annotation[] anno = obj.getClass().getAnnotations();
            String tableName = "";
            for (int i = 0; i < anno.length; i++) {
                if (anno[i] instanceof Table) {
                    Table table = (Table) anno[i];
                    tableName = table.name();
                }
            }
            String suuid = d.getSuuid();
            // 遍历对象的方法
            Map<String, Method> methodMap = new HashMap<String, Method>();
            for (Method method : methods) {
                methodMap.put(method.getName(), method);
            }
            String mainUuid = methodMap.get("getUuid").invoke(obj, null).toString();
            if (action == 2) {
                for (String mName : methodMap.keySet()) {
                    if ((mName.startsWith("is") || mName.startsWith("get")) && !mName.startsWith("getClass")
                            && !mName.equals("getAttach")) {
                        String fieldName = "";
                        if (mName.startsWith("is")) {
                            fieldName = mName.substring(2, mName.length());
                        } else {
                            fieldName = mName.substring(3, mName.length());
                        }
                        // 列表时，处理多对多的关系
                        Map<String, Object> map_ = systemTableService.getColumnPropOfSet(c, fieldName.substring(0, 1)
                                .toLowerCase() + fieldName.substring(1));
                        if (map_ != null && map_.get("oneToMany") != null && !(Boolean) map_.get("oneToMany")) {// 多对多
                            String collectionTableName = map_.get("collectionTableName").toString();
                            Column[] columns = (Column[]) map_.get("columns");
                            Column column1 = columns[0];
                            Column column2 = columns[1];
                            // 删除对应关系表数据
                            String mainColumnName = "";
                            String delCollectionSql = "delete " + collectionTableName + " d where d.";
                            if (column2.getValue() instanceof ManyToOne) {
                                String columnForEntityName = ((SimpleValue) column2.getValue()).getType().getName();
                                if (!StringUtils.isBlank(columnForEntityName) && entityName.equals(columnForEntityName)) {
                                    // 字段对应的实体=当前实体
                                    mainColumnName = column2.getName();
                                } else {
                                    mainColumnName = column1.getName();
                                }
                            } else {
                                String columnForEntityName = ((SimpleValue) column1.getValue()).getType().getName();
                                if (!StringUtils.isBlank(columnForEntityName) && entityName.equals(columnForEntityName)) {
                                    // 字段对应的实体=当前实体
                                    mainColumnName = column1.getName();
                                } else {
                                    mainColumnName = column2.getName();
                                }
                            }
                            delCollectionSql += mainColumnName + "='" + mainUuid + "'";
                            this.dao.getSession().createSQLQuery(delCollectionSql).executeUpdate();
                        }
                    }
                }
                // 没有多对多的关系时
                String delTableSql = "delete " + tableName + " s where s.uuid='" + suuid + "'";
                this.dao.getSession().createSQLQuery(delTableSql).executeUpdate();
            } else {
                if (stableType == 1 || stableType == 4 || stableType == 3) {// 1实体表/3关系表/4文件夹
                    String insertCollectionSql = "";
                    String fieldNames = "";
                    String fieldValues = "";
                    String upFieldSql = "";
                    Map<String, Object> typeMap = new HashMap<String, Object>();
                    Map<String, Object> valueMap = new HashMap<String, Object>();
                    for (String mName : methodMap.keySet()) {
                        Method method = methodMap.get(mName);
                        if ((mName.startsWith("is") || mName.startsWith("get")) && !mName.startsWith("getClass")
                                && !mName.equals("getAttach")) {
                            try {
                                String fieldName = "";
                                if (mName.startsWith("is")) {
                                    fieldName = mName.substring(2, mName.length());
                                } else {
                                    fieldName = mName.substring(3, mName.length());
                                }

                                String dbFieldName = toDbField(fieldName);
                                String[] type_ = method.getReturnType().toString().split("\\.");
                                String type = type_[(type_.length - 1)];
                                Object value = method.invoke(obj, null);
                                if (!type.equals("List") && !type.equals("Set")) {
                                    if (!type.equals("String") && !type.equals("Date") && !type.equals("Clob")
                                            && !type.equals("Integer") && !type.equals("int") && !type.equals("Long")
                                            && !type.equals("long") && !type.equals("Boolean")
                                            && !type.equals("boolean") && !type.equals("Double")
                                            && !type.equals("double") && !type.equals("float") && !type.equals("Float")) {// 取对象类型的字段
                                        Map<String, String> map_ = systemTableService.getColumnMap(c, fieldName
                                                .substring(0, 1).toLowerCase() + fieldName.substring(1));
                                        dbFieldName = map_.get("name");
                                    }
                                    if (dbFieldName != null) {
                                        fieldNames += "," + dbFieldName;
                                        fieldValues += ",:" + dbFieldName;
                                        upFieldSql += "," + dbFieldName + "=:" + dbFieldName;
                                        valueMap.put(dbFieldName, value);
                                        typeMap.put(dbFieldName, method.getReturnType());
                                    }
                                } else {// 列表时，处理多对多的关系
                                    Map<String, Object> map_ = systemTableService.getColumnPropOfSet(c, fieldName
                                            .substring(0, 1).toLowerCase() + fieldName.substring(1));
                                    if (map_ != null && map_.get("oneToMany") != null
                                            && !(Boolean) map_.get("oneToMany")) {// 多对多
                                        // 有多对多的关系就删除关系数据
                                        String collectionTableName = map_.get("collectionTableName").toString();
                                        Column[] columns = (Column[]) map_.get("columns");
                                        Column column1 = columns[0];
                                        Column column2 = columns[1];
                                        // 删除对应关系表数据
                                        String mainColumnName = "";
                                        String subColumnName = "";
                                        String delCollectionSql = "delete " + collectionTableName + " d where d.";
                                        if (column2.getValue() instanceof ManyToOne) {
                                            String columnForEntityName = ((SimpleValue) column2.getValue()).getType()
                                                    .getName();
                                            if (!StringUtils.isBlank(columnForEntityName)
                                                    && entityName.equals(columnForEntityName)) {
                                                // 字段对应的实体=当前实体
                                                mainColumnName = column2.getName();
                                                subColumnName = column1.getName();
                                            } else {
                                                mainColumnName = column1.getName();
                                                subColumnName = column2.getName();
                                            }
                                        } else {
                                            String columnForEntityName = ((SimpleValue) column1.getValue()).getType()
                                                    .getName();
                                            if (!StringUtils.isBlank(columnForEntityName)
                                                    && entityName.equals(columnForEntityName)) {
                                                // 字段对应的实体=当前实体
                                                mainColumnName = column1.getName();
                                                subColumnName = column2.getName();
                                            } else {
                                                mainColumnName = column2.getName();
                                                subColumnName = column1.getName();
                                            }
                                        }
                                        delCollectionSql += mainColumnName + "='" + mainUuid + "'";
                                        this.dao.getSession().createSQLQuery(delCollectionSql).executeUpdate();
                                        // 当关系表存在时添加关系，关系在hibernate的代理对象中
                                        if (value instanceof PersistentSet) {
                                            PersistentSet ps = (PersistentSet) value;
                                            Set<Object> sets = (Set<Object>) ps.getValue();
                                            for (Object subObj : sets) {
                                                Class subClass = subObj.getClass();
                                                for (Method subMethod : subClass.getMethods()) {
                                                    if (subMethod.getName().equals("getUuid")) {
                                                        String subUuidValue = subMethod.invoke(subObj, null).toString();
                                                        insertCollectionSql += "insert into " + collectionTableName
                                                                + " (" + mainColumnName + "," + subColumnName + ") "
                                                                + "values ('" + mainUuid + "','" + subUuidValue + "');";
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                logger.info(e.getMessage());
                            }
                        }
                    }
                    /************************变更数据*********************************/
                    List slist = this.dao.getSession()
                            .createSQLQuery("select uuid from " + tableName + " s where s.uuid='" + suuid + "'").list();
                    String sql = "";
                    if (slist.size() > 0) {
                        sql += "update " + tableName + " set " + upFieldSql.replaceFirst(",", "") + " where uuid='"
                                + suuid + "'";
                    } else {
                        sql += "insert into " + tableName + " (" + fieldNames.replaceFirst(",", "") + ") values ("
                                + fieldValues.replaceFirst(",", "") + ")";
                    }
                    SQLQuery sqlquery = this.dao.getSession().createSQLQuery(sql);
                    for (String key_ : valueMap.keySet()) {
                        Object fieldObj = valueMap.get(key_);
                        String[] type_ = typeMap.get(key_).toString().split("\\.");
                        String type = type_[(type_.length - 1)];
                        if (type.equals("String")) {
                            sqlquery.setString(key_, fieldObj == null ? "" : fieldObj.toString());
                        } else if (type.equals("Clob")) {
                            Clob clob = (Clob) fieldObj;
                            if (clob != null) {
                                sqlquery.setText(key_, IOUtils.toString(clob.getCharacterStream()).toString());
                            } else {
                                sqlquery.setText(key_, "");
                            }
                        } else if (type.equals("Date")) {
                            sqlquery.setTimestamp(key_, fieldObj == null ? null : (Date) fieldObj);
                        } else if (type.equals("Integer") || type.equals("int")) {
                            sqlquery.setInteger(key_, fieldObj == null ? 0 : Integer.parseInt(fieldObj.toString()));
                        } else if (type.equals("Long") || type.equals("long")) {
                            sqlquery.setLong(key_, fieldObj == null ? 0 : Long.parseLong(fieldObj.toString()));
                        } else if (type.equals("Float") || type.equals("float")) {
                            sqlquery.setFloat(key_, fieldObj == null ? 0 : Float.parseFloat(fieldObj.toString()));
                        } else if (type.equals("Double") || type.equals("double")) {
                            sqlquery.setDouble(key_, fieldObj == null ? 0 : Double.parseDouble(fieldObj.toString()));
                        } else if (type.equals("Boolean") || type.equals("boolean")) {
                            sqlquery.setBoolean(key_, fieldObj == null ? false : (Boolean) fieldObj);
                        } else {
                            if (fieldObj != null) {
                                Class c_ = fieldObj.getClass();
                                Method[] methods_ = c_.getMethods();
                                for (Method method : methods_) {
                                    if (method.getName().equals("getUuid")) {
                                        Object value = method.invoke(fieldObj, null);
                                        sqlquery.setString(key_, value == null ? "" : value.toString());
                                    }
                                }
                            } else {// 对象为空时
                                sqlquery.setString(key_, "");
                            }
                        }
                    }

                    if (stableType == 4) {// 文件夹提取附件数据
                        // 提取附件
                        CommonFtp myFtp = new CommonFtp(ftpDataBean.getFtp_host(), ftpDataBean.getFtp_post(),
                                ftpDataBean.getFtp_user_name(), ftpDataBean.getFtp_pass_word());
                        try {
                            if (!myFtp.isConnected()) {
                                myFtp.connect();
                            }
                            String zainUUid = mainUuid + ".zip";
                            File downFile = myFtp.downFile("\\", zainUUid);
                            mongoFileService.importChangeInfo(downFile);
                            myFtp.deleteFile("\\", zainUUid);
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                            logger.info(e.getMessage());
                        } finally {
                            myFtp.disconnect();
                        }
                    }
                    sqlquery.executeUpdate();
                    // 先保存实体后插入多对多的关系
                    if (!StringUtils.isBlank(insertCollectionSql)) {
                        String[] insertCollectionSqlItem = insertCollectionSql.split(";");
                        for (int i = 0; i < insertCollectionSqlItem.length; i++) {
                            this.dao.getSession().createSQLQuery(insertCollectionSqlItem[i]).executeUpdate();
                        }
                    }
                } else {// 2非实体表
                    // TODO
                    throw new RuntimeException("平台的依赖循环升级时，将该代码注释掉了，因为dyFormDataDao不得直接暴露出来给外部使用");
                    /*
                     * Map<String, Object> mapV = (Map<String, Object>) obj;
                     * String formUuid = (String) mapV.get("form_uuid"); if
                     * (action == 4) {
                     * dyFormDataDao.update(dyFormApiFacade.getFormDefinition
                     * (formUuid), mapV); } else {
                     * dyFormDataDao.save(dyFormApiFacade
                     * .getFormDefinition(formUuid), mapV); }
                     */
                }
            }
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.info(e.getMessage());
            return false;
        }
    }

    @Transactional(readOnly = true)
    public int getDataCountByJdbc(String sql) {
        int count = 0;
        Connection conn = ConManager.getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            logger.info(e.getMessage());
        } finally {
            ConManager.closeConnection();
        }
        return count;
    }
}