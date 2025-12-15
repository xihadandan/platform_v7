package com.wellsoft.pt.integration.service.impl;

import com.wellsoft.context.config.Config;
import com.wellsoft.pt.integration.service.ExchangeDataSynSQLDService;
import com.wellsoft.pt.integration.support.ExchangeDataResultTransformer;
import com.wellsoft.pt.integration.support.SerializableUtil;
import com.wellsoft.pt.integration.support.XZSPBIZ;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Clob;
import java.text.SimpleDateFormat;
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
 * 2014-12-8.1	ruanhg		2014-12-8		Create
 * </pre>
 * @date 2014-12-8
 */
@Service
@Transactional
public class ExchangeDataSynSQLDServiceImpl extends BaseServiceImpl implements ExchangeDataSynSQLDService {

    public static Integer DATANUM = 50;
    @Autowired
    private ZongXianExchangeServiceImpl zongXianExchangeServiceImpl;

    // @Autowired
    // private XzspZxdxRecordDao xzspZxdxRecordDao;
    // @Autowired
    // private XzspZxjmRecordDao xzspZxjmRecordDao;
    // @Autowired
    // private XzspZxsjRecordDao xzspZxsjRecordDao;

    public static Map<String, String> getQJUnitId() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("sm", Config.getValue("sqld.sm.unit.id"));
        map.put("hc", Config.getValue("sqld.hc.unit.id"));
        map.put("hl", Config.getValue("sqld.hl.unit.id"));
        map.put("xa", Config.getValue("sqld.xa.unit.id"));
        map.put("ta", Config.getValue("sqld.ta.unit.id"));
        map.put("jm", Config.getValue("sqld.jm.unit.id"));
        return map;
    }

    @Override
    public void synCommontoTenant() throws Exception {

        Session session = this.getDao("commonSessionFactory").getSession();
        String sql1 = "select * from tig_table_data t";

        String sql2 = "select * from tig_column_data t";

        String sql3 = "select * from tig_column_clob t";
        List<Map<String, Object>> dataList = session.createSQLQuery(sql1)
                .setResultTransformer(ExchangeDataResultTransformer.INSTANCE).list();
        List<Map<String, Object>> columDataList = session.createSQLQuery(sql2)
                .setResultTransformer(ExchangeDataResultTransformer.INSTANCE).list();
        List<Map<String, Object>> CLOBList = session.createSQLQuery(sql3)
                .setResultTransformer(ExchangeDataResultTransformer.INSTANCE).list();
        for (Map<String, Object> dataItem : dataList) {
            String uuid = dataItem.get("uuid").toString();
            // 业务字段表
            String delSql2 = "delete tig_column_data t where t.tig_owner_uuid = '" + uuid + "'";
            session.createSQLQuery(delSql2).executeUpdate();

            String delSql3 = "delete tig_column_clob t where t.tig_owner_uuid = '" + uuid + "'";
            session.createSQLQuery(delSql3).executeUpdate();

            String delSql1 = "delete tig_table_data t where t.uuid = '" + uuid + "'";
            session.createSQLQuery(delSql1).executeUpdate();
        }
        session.close();

        Session session3 = this.dao.getSession();
        try {
            // 字段表插入
            for (Map<String, Object> dataItem : columDataList) {
                String insertSql = "insert into tig_column_data "
                        + "(,tig_owner_uuid,tig_column_name,tig_data_type,data_time,data_float,data_number,data_clob,data_char,data_date,data_varchar_2) "
                        + "values "
                        + "(,:tig_owner_uuid,:tig_column_name,:tig_data_type,:data_time,:data_float,:data_number,:data_clob,:data_char,:data_date,:data_varchar_2) ";
                for (String columKey : dataItem.keySet()) {
                    if (dataItem.get(columKey) == null) {
                        insertSql = insertSql.replace("," + columKey, "").replace(",:" + columKey, "");
                    }
                }
                insertSql = insertSql.replace("(,", "(");
                SQLQuery sqlquery = session3.createSQLQuery(insertSql);
                if (dataItem.get("tig_owner_uuid") != null) {
                    sqlquery.setString("tig_owner_uuid", dataItem.get("tig_owner_uuid").toString());
                }
                if (dataItem.get("tig_column_name") != null) {
                    sqlquery.setString("tig_column_name", dataItem.get("tig_column_name").toString());
                }
                if (dataItem.get("tig_data_type") != null) {
                    sqlquery.setString("tig_data_type", dataItem.get("tig_data_type").toString());
                }
                if (dataItem.get("data_time") != null) {
                    sqlquery.setTimestamp("data_time", (Date) dataItem.get("data_time"));
                }
                if (dataItem.get("data_float") != null) {
                    sqlquery.setFloat("data_float", Float.parseFloat(dataItem.get("data_float").toString()));
                }
                if (dataItem.get("data_number") != null) {
                    sqlquery.setDouble("data_number", Double.parseDouble(dataItem.get("data_number").toString()));
                }
                if (dataItem.get("data_clob") != null) {
                    sqlquery.setText("data_clob", dataItem.get("data_clob").toString());
                }
                if (dataItem.get("data_char") != null) {
                    sqlquery.setCharacter("data_char", dataItem.get("data_char").toString().charAt(0));
                }
                if (dataItem.get("data_date") != null) {
                    sqlquery.setDate("data_date", (Date) dataItem.get("data_date"));
                }
                if (dataItem.get("data_varchar_2") != null) {
                    sqlquery.setString("data_varchar_2", dataItem.get("data_varchar_2").toString());
                }
                sqlquery.executeUpdate();
            }
            // 插入数据
            for (Map<String, Object> dataItem : dataList) {
                // 插入数据
                String insertSql = "insert into tig_table_data "
                        + "(,uuid,create_time,order_id,action,feedback,direction,remark,stable_name,status,suuid,syn_time,composite_key,cloum_num) "
                        + "values "
                        + "(,:uuid,:create_time,syn_order_id.NEXTVAL,:action,:feedback,:direction,:remark,:stable_name,:status,:suuid,:syn_time,:composite_key,:cloum_num)";
                for (String dataKey : dataItem.keySet()) {
                    if (dataItem.get(dataKey) == null && !dataKey.equals("syn_time")) {
                        insertSql = insertSql.replace("," + dataKey, "").replace(",:" + dataKey, "");
                    }
                }
                insertSql = insertSql.replace("(,", "(");
                SQLQuery sqlquery = session3.createSQLQuery(insertSql);
                if (dataItem.get("uuid") != null) {
                    sqlquery.setString("uuid", dataItem.get("uuid").toString());
                }
                if (dataItem.get("create_time") != null) {
                    sqlquery.setTimestamp("create_time", (Date) dataItem.get("create_time"));
                }
                // if (dataItem.get("order_id") != null) {
                // sqlquery.setInteger("order_id",
                // Integer.parseInt(dataItem.get("order_id").toString()));
                // }
                if (dataItem.get("action") != null) {
                    sqlquery.setInteger("action", Integer.parseInt(dataItem.get("action").toString()));
                }
                if (dataItem.get("feedback") != null) {
                    sqlquery.setString("feedback", dataItem.get("feedback").toString());
                }
                if (dataItem.get("direction") != null) {
                    sqlquery.setInteger("direction", Integer.parseInt(dataItem.get("direction").toString()));
                }
                if (dataItem.get("remark") != null) {
                    sqlquery.setString("remark", dataItem.get("remark").toString());
                }
                if (dataItem.get("stable_name") != null) {
                    sqlquery.setString("stable_name", dataItem.get("stable_name").toString());
                }
                if (dataItem.get("status") != null) {
                    sqlquery.setInteger("status", 1);
                }
                if (dataItem.get("suuid") != null) {
                    sqlquery.setString("suuid", dataItem.get("suuid").toString());
                }
                sqlquery.setTimestamp("syn_time", new Date());
                if (dataItem.get("composite_key") != null) {
                    sqlquery.setString("composite_key", dataItem.get("composite_key").toString());
                }
                if (dataItem.get("cloum_num") != null) {
                    sqlquery.setInteger("cloum_num", Integer.parseInt(dataItem.get("cloum_num").toString()));
                }
                sqlquery.executeUpdate();
            }
            // 大字段插入
            for (Map<String, Object> dataItem : CLOBList) {
                String insertSql = "insert into tig_column_clob "
                        + "(,tig_owner_uuid,tig_column_name,data_uuid,composite_key,data_clob,data_status,direction,stable_name,syn_time,create_time) "
                        + "values "
                        + "(,:tig_owner_uuid,:tig_column_name,:data_uuid,:composite_key,:data_clob,:data_status,:direction,:stable_name,:syn_time,:create_time) ";
                for (String columKey : dataItem.keySet()) {
                    if (dataItem.get(columKey) == null && !columKey.equals("syn_time")) {
                        insertSql = insertSql.replace("," + columKey, "").replace(",:" + columKey, "");
                    }
                }
                insertSql = insertSql.replace("(,", "(");
                SQLQuery sqlquery = session3.createSQLQuery(insertSql);
                if (dataItem.get("tig_owner_uuid") != null) {
                    sqlquery.setString("tig_owner_uuid", dataItem.get("tig_owner_uuid").toString());
                }
                if (dataItem.get("tig_column_name") != null) {
                    sqlquery.setString("tig_column_name", dataItem.get("tig_column_name").toString());
                }
                if (dataItem.get("data_uuid") != null) {
                    sqlquery.setString("data_uuid", dataItem.get("data_uuid").toString());
                }
                if (dataItem.get("composite_key") != null) {
                    sqlquery.setString("composite_key", dataItem.get("composite_key").toString());
                }
                if (dataItem.get("data_clob") != null) {
                    sqlquery.setText("data_clob",
                            IOUtils.toString(((Clob) dataItem.get("data_clob")).getCharacterStream()).toString());
                }
                if (dataItem.get("data_char") != null) {
                    sqlquery.setCharacter("data_char", dataItem.get("data_char").toString().charAt(0));
                }
                if (dataItem.get("data_status") != null) {
                    sqlquery.setInteger("data_status", 1);
                }
                if (dataItem.get("direction") != null) {
                    sqlquery.setInteger("direction", Integer.parseInt(dataItem.get("direction").toString()));
                }
                if (dataItem.get("stable_name") != null) {
                    sqlquery.setString("stable_name", dataItem.get("stable_name").toString());
                }
                if (dataItem.get("create_time") != null) {
                    sqlquery.setTimestamp("create_time", (Date) dataItem.get("create_time"));
                }
                sqlquery.setTimestamp("syn_time", new Date());
                sqlquery.executeUpdate();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            throw e;
        } finally {
            session3.close();
        }

    }

    @Override
    public void synOutData() throws Exception {
        // TODO Auto-generated method stub
        String unitId = Config.getValue("center.unit.id");
        Date synTime = new Date();// 同步时间
        /*********************** 1、提取系统数据 先提取表数据再提取字段数据 ****************************/
        try {
            List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> columDataList = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> CLOBList = new ArrayList<Map<String, Object>>();

            Session session2 = this.dao.getSession();
            try {
                // 提取同步数据
                String querySql = "select list.* from (select * from tig_table_data o where o.status=1 order by o.order_id asc) list where rownum<="
                        + DATANUM;// 未同步
                // 提取大字段
                String clobSql = "select list.* from (select * from tig_column_clob t where t.data_status=1) list where rownum<="
                        + DATANUM;
                List<Map<String, Object>> clobDataByOneData = session2.createSQLQuery(clobSql)
                        .setResultTransformer(ExchangeDataResultTransformer.INSTANCE).list();
                CLOBList.addAll(clobDataByOneData);
                dataList = session2.createSQLQuery(querySql)
                        .setResultTransformer(ExchangeDataResultTransformer.INSTANCE).list();
                if (dataList.size() == 0 && CLOBList.size() == 0) {
                    return;
                }
                // 提取字段数据
                for (Map<String, Object> dataItem : dataList) {
                    String uuid = dataItem.get("uuid").toString();
                    // 业务字段表
                    String subQuerySql = "select * from tig_column_data t where t.tig_owner_uuid = '" + uuid + "'";
                    List<Map<String, Object>> columDataByOneData = session2.createSQLQuery(subQuerySql)
                            .setResultTransformer(ExchangeDataResultTransformer.INSTANCE).list();
                    int columDataByOneDataNum = columDataByOneData.size();
                    columDataList.addAll(columDataByOneData);
                    // 写入同步时间及更新的字段数量及状态
                    String updateSql = "update tig_table_data o set o.syn_time=:syn_time,o.cloum_num=:columDataByOneDataNum,o.status=2 where o.uuid = :uuid";
                    SQLQuery sqlquery = session2.createSQLQuery(updateSql);
                    sqlquery.setTimestamp("syn_time", synTime);
                    sqlquery.setInteger("columDataByOneDataNum", columDataByOneDataNum);
                    sqlquery.setString("uuid", uuid);
                    sqlquery.executeUpdate();
                    dataItem.put("cloum_num", columDataByOneDataNum);
                }
                for (Map<String, Object> dataItem : CLOBList) {// 更新同步时间 及状态
                    String tig_owner_uuid = dataItem.get("tig_owner_uuid").toString();
                    String tig_column_name = dataItem.get("tig_column_name").toString();
                    String updateSql = "update tig_column_clob o set o.syn_time=:syn_time,o.data_status=2 where o.tig_owner_uuid='"
                            + tig_owner_uuid + "' and o.tig_column_name='" + tig_column_name + "'";
                    SQLQuery sqlquery = session2.createSQLQuery(updateSql);
                    sqlquery.setTimestamp("syn_time", synTime);
                    sqlquery.executeUpdate();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                throw e;
            } finally {
                session2.close();
            }

            /**************************** 2、变更数据到前置机 先插入字段数据再插入表数据 *****************************/
            Session session3 = this.getDao("QZJ" + unitId).getSession();
            try {
                // 字段表插入
                for (Map<String, Object> dataItem : columDataList) {
                    String insertSql = "insert into tig_column_data "
                            + "(,tig_owner_uuid,tig_column_name,tig_data_type,data_time,data_float,data_number,data_clob,data_char,data_date,data_varchar_2) "
                            + "values "
                            + "(,:tig_owner_uuid,:tig_column_name,:tig_data_type,:data_time,:data_float,:data_number,:data_clob,:data_char,:data_date,:data_varchar_2) ";
                    for (String columKey : dataItem.keySet()) {
                        if (dataItem.get(columKey) == null) {
                            insertSql = insertSql.replace("," + columKey, "").replace(",:" + columKey, "");
                        }
                    }
                    insertSql = insertSql.replace("(,", "(");
                    SQLQuery sqlquery = session3.createSQLQuery(insertSql);
                    if (dataItem.get("tig_owner_uuid") != null) {
                        sqlquery.setString("tig_owner_uuid", dataItem.get("tig_owner_uuid").toString());
                    }
                    if (dataItem.get("tig_column_name") != null) {
                        sqlquery.setString("tig_column_name", dataItem.get("tig_column_name").toString());
                    }
                    if (dataItem.get("tig_data_type") != null) {
                        sqlquery.setString("tig_data_type", dataItem.get("tig_data_type").toString());
                    }
                    if (dataItem.get("data_time") != null) {
                        sqlquery.setTimestamp("data_time", (Date) dataItem.get("data_time"));
                    }
                    if (dataItem.get("data_float") != null) {
                        sqlquery.setFloat("data_float", Float.parseFloat(dataItem.get("data_float").toString()));
                    }
                    if (dataItem.get("data_number") != null) {
                        sqlquery.setDouble("data_number", Double.parseDouble(dataItem.get("data_number").toString()));
                    }
                    if (dataItem.get("data_clob") != null) {
                        sqlquery.setText("data_clob", dataItem.get("data_clob").toString());
                    }
                    if (dataItem.get("data_char") != null) {
                        sqlquery.setCharacter("data_char", dataItem.get("data_char").toString().charAt(0));
                    }
                    if (dataItem.get("data_date") != null) {
                        sqlquery.setDate("data_date", (Date) dataItem.get("data_date"));
                    }
                    if (dataItem.get("data_varchar_2") != null) {
                        sqlquery.setString("data_varchar_2", dataItem.get("data_varchar_2").toString());
                    }
                    sqlquery.executeUpdate();
                }
                // 插入数据
                for (Map<String, Object> dataItem : dataList) {
                    // 插入数据
                    String insertSql = "insert into tig_table_data "
                            + "(,uuid,create_time,order_id,action,feedback,direction,remark,stable_name,status,suuid,syn_time,composite_key,cloum_num) "
                            + "values "
                            + "(,:uuid,:create_time,:order_id,:action,:feedback,:direction,:remark,:stable_name,:status,:suuid,:syn_time,:composite_key,:cloum_num)";
                    for (String dataKey : dataItem.keySet()) {
                        if (dataItem.get(dataKey) == null && !dataKey.equals("syn_time")) {
                            insertSql = insertSql.replace("," + dataKey, "").replace(",:" + dataKey, "");
                        }
                    }
                    insertSql = insertSql.replace("(,", "(");
                    SQLQuery sqlquery = session3.createSQLQuery(insertSql);
                    if (dataItem.get("uuid") != null) {
                        sqlquery.setString("uuid", dataItem.get("uuid").toString());
                    }
                    if (dataItem.get("create_time") != null) {
                        sqlquery.setTimestamp("create_time", (Date) dataItem.get("create_time"));
                    }
                    if (dataItem.get("order_id") != null) {
                        sqlquery.setInteger("order_id", Integer.parseInt(dataItem.get("order_id").toString()));
                    }
                    if (dataItem.get("action") != null) {
                        sqlquery.setInteger("action", Integer.parseInt(dataItem.get("action").toString()));
                    }
                    if (dataItem.get("feedback") != null) {
                        sqlquery.setString("feedback", dataItem.get("feedback").toString());
                    }
                    if (dataItem.get("direction") != null) {
                        sqlquery.setInteger("direction", Integer.parseInt(dataItem.get("direction").toString()));
                    }
                    if (dataItem.get("remark") != null) {
                        sqlquery.setString("remark", dataItem.get("remark").toString());
                    }
                    if (dataItem.get("stable_name") != null) {
                        sqlquery.setString("stable_name", dataItem.get("stable_name").toString());
                    }
                    if (dataItem.get("status") != null) {
                        sqlquery.setInteger("status", 1);
                    }
                    if (dataItem.get("suuid") != null) {
                        sqlquery.setString("suuid", dataItem.get("suuid").toString());
                    }
                    sqlquery.setTimestamp("syn_time", synTime);
                    if (dataItem.get("composite_key") != null) {
                        sqlquery.setString("composite_key", dataItem.get("composite_key").toString());
                    }
                    if (dataItem.get("cloum_num") != null) {
                        sqlquery.setInteger("cloum_num", Integer.parseInt(dataItem.get("cloum_num").toString()));
                    }
                    sqlquery.executeUpdate();
                }
                // 大字段插入
                for (Map<String, Object> dataItem : CLOBList) {
                    String insertSql = "insert into tig_column_clob "
                            + "(,tig_owner_uuid,tig_column_name,data_uuid,composite_key,data_clob,data_status,direction,stable_name,syn_time,create_time) "
                            + "values "
                            + "(,:tig_owner_uuid,:tig_column_name,:data_uuid,:composite_key,:data_clob,:data_status,:direction,:stable_name,:syn_time,:create_time) ";
                    for (String columKey : dataItem.keySet()) {
                        if (dataItem.get(columKey) == null && !columKey.equals("syn_time")) {
                            insertSql = insertSql.replace("," + columKey, "").replace(",:" + columKey, "");
                        }
                    }
                    insertSql = insertSql.replace("(,", "(");
                    SQLQuery sqlquery = session3.createSQLQuery(insertSql);
                    if (dataItem.get("tig_owner_uuid") != null) {
                        sqlquery.setString("tig_owner_uuid", dataItem.get("tig_owner_uuid").toString());
                    }
                    if (dataItem.get("tig_column_name") != null) {
                        sqlquery.setString("tig_column_name", dataItem.get("tig_column_name").toString());
                    }
                    if (dataItem.get("data_uuid") != null) {
                        sqlquery.setString("data_uuid", dataItem.get("data_uuid").toString());
                    }
                    if (dataItem.get("composite_key") != null) {
                        sqlquery.setString("composite_key", dataItem.get("composite_key").toString());
                    }
                    if (dataItem.get("data_clob") != null) {
                        sqlquery.setText("data_clob",
                                IOUtils.toString(((Clob) dataItem.get("data_clob")).getCharacterStream()).toString());
                    }
                    if (dataItem.get("data_char") != null) {
                        sqlquery.setCharacter("data_char", dataItem.get("data_char").toString().charAt(0));
                    }
                    if (dataItem.get("data_status") != null) {
                        sqlquery.setInteger("data_status", 1);
                    }
                    if (dataItem.get("direction") != null) {
                        sqlquery.setInteger("direction", Integer.parseInt(dataItem.get("direction").toString()));
                    }
                    if (dataItem.get("stable_name") != null) {
                        sqlquery.setString("stable_name", dataItem.get("stable_name").toString());
                    }
                    if (dataItem.get("create_time") != null) {
                        sqlquery.setTimestamp("create_time", (Date) dataItem.get("create_time"));
                    }
                    sqlquery.setTimestamp("syn_time", synTime);
                    sqlquery.executeUpdate();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                throw e;
            } finally {
                session3.close();
            }
        } catch (Exception e) {
            logger.error("**********************synOutData " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void synInData(String unitId) throws Exception {
        // TODO Auto-generated method stub
        Date backupTime = new Date();// 提取时间
        try {

            /*********************** 1、提取前置机数据(同步中，同步成功/同步失败(握手)) ****************************/
            List<Map<String, Object>> dataListTemp = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> columDataList = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> CLOBList = new ArrayList<Map<String, Object>>();
            Session session2 = this.getDao("QZJ" + unitId).getSession();
            try {
                String querySql = "select list.* from (select * from tig_table_data o where o.status=1 order by o.order_id asc) list where rownum<="
                        + DATANUM;
                // 提取大字段
                String clobSql = "select list.* from (select * from tig_column_clob t where t.data_status=1) list where rownum<="
                        + DATANUM;
                CLOBList = session2.createSQLQuery(clobSql)
                        .setResultTransformer(ExchangeDataResultTransformer.INSTANCE).list();
                // 提取同步中及反馈的数据
                dataListTemp = session2.createSQLQuery(querySql)
                        .setResultTransformer(ExchangeDataResultTransformer.INSTANCE).list();

                if (dataListTemp.size() == 0 && CLOBList.size() == 0) {
                    return;
                }
                // 还原的字段与同步的字段是否一致
                for (Map<String, Object> dataItem : dataListTemp) {
                    String uuid = dataItem.get("uuid").toString();
                    int cloum_num = Integer.parseInt(dataItem.get("cloum_num").toString());
                    // 业务字段表
                    String subQuerySql = "select * from tig_column_data t where t.tig_owner_uuid = '" + uuid + "'";
                    List<Map<String, Object>> columDataByOneData = session2.createSQLQuery(subQuerySql)
                            .setResultTransformer(ExchangeDataResultTransformer.INSTANCE).list();
                    int columDataByOneDataNum = columDataByOneData.size();
                    if (cloum_num == columDataByOneDataNum) {
                        dataList.add(dataItem);
                        columDataList.addAll(columDataByOneData);
                    } else {
                        continue;
                    }
                }
            } catch (Exception e) {
                throw e;
            } finally {
                session2.close();
            }

            /*********************** 2、变更数据到系统 *****************************/
            Session session3 = this.getDao("QJ" + unitId).getSession();
            try {
                String mainUuids = ",";
                Map<String, Map<String, Object>> columMap = new HashMap<String, Map<String, Object>>();
                // 字段表插入
                for (Map<String, Object> dataItem : columDataList) {
                    String tig_owner_uuid = dataItem.get("tig_owner_uuid").toString();
                    String tig_column_name = dataItem.get("tig_column_name").toString();
                    if (mainUuids.indexOf("," + tig_owner_uuid + ",") < 0) {
                        mainUuids += tig_owner_uuid + ",";
                    }
                    columMap.put(tig_owner_uuid + "_" + tig_column_name, dataItem);

                    String insertSql = "insert into tig_column_data "
                            + "(,tig_owner_uuid,tig_column_name,tig_data_type,data_time,data_float,data_number,data_clob,data_char,data_date,data_varchar_2) "
                            + "values "
                            + "(,:tig_owner_uuid,:tig_column_name,:tig_data_type,:data_time,:data_float,:data_number,:data_clob,:data_char,:data_date,:data_varchar_2) ";
                    for (String columKey : dataItem.keySet()) {
                        if (dataItem.get(columKey) == null) {
                            insertSql = insertSql.replace("," + columKey, "").replace(",:" + columKey, "");
                        }
                    }
                    insertSql = insertSql.replace("(,", "(");
                    SQLQuery sqlquery = session3.createSQLQuery(insertSql);
                    if (dataItem.get("tig_owner_uuid") != null) {
                        sqlquery.setString("tig_owner_uuid", dataItem.get("tig_owner_uuid").toString());
                    }
                    if (dataItem.get("tig_column_name") != null) {
                        sqlquery.setString("tig_column_name", dataItem.get("tig_column_name").toString());
                    }
                    if (dataItem.get("tig_data_type") != null) {
                        sqlquery.setString("tig_data_type", dataItem.get("tig_data_type").toString());
                    }
                    if (dataItem.get("data_time") != null) {
                        sqlquery.setTimestamp("data_time", (Date) dataItem.get("data_time"));
                    }
                    if (dataItem.get("data_float") != null) {
                        sqlquery.setFloat("data_float", Float.parseFloat(dataItem.get("data_float").toString()));
                    }
                    if (dataItem.get("data_number") != null) {
                        sqlquery.setDouble("data_number", Double.parseDouble(dataItem.get("data_number").toString()));
                    }
                    if (dataItem.get("data_clob") != null) {
                        sqlquery.setText("data_clob", dataItem.get("data_clob").toString());
                    }
                    if (dataItem.get("data_char") != null) {
                        sqlquery.setCharacter("data_char", dataItem.get("data_char").toString().charAt(0));
                    }
                    if (dataItem.get("data_date") != null) {
                        sqlquery.setDate("data_date", (Date) dataItem.get("data_date"));
                    }
                    if (dataItem.get("data_varchar_2") != null) {
                        sqlquery.setString("data_varchar_2", dataItem.get("data_varchar_2").toString());
                    }
                    sqlquery.executeUpdate();
                }
                // 插入数据
                for (Map<String, Object> dataItem : dataList) {
                    // 插入数据
                    String insertSql = "insert into tig_table_data "
                            + "(,uuid,create_time,order_id,action,feedback,direction,remark,stable_name,status,suuid,syn_time,composite_key,cloum_num,backup_time) "
                            + "values "
                            + "(,:uuid,:create_time,:order_id,:action,:feedback,:direction,:remark,:stable_name,:status,:suuid,:syn_time,:composite_key,:cloum_num,:backup_time)";
                    for (String dataKey : dataItem.keySet()) {
                        if (dataItem.get(dataKey) == null && !dataKey.equals("backup_time")) {
                            insertSql = insertSql.replace("," + dataKey, "").replace(",:" + dataKey, "");
                        }
                    }
                    insertSql = insertSql.replace("(,", "(");
                    SQLQuery sqlquery = session3.createSQLQuery(insertSql);

                    if (dataItem.get("uuid") != null) {
                        sqlquery.setString("uuid", dataItem.get("uuid").toString());
                    }
                    if (dataItem.get("create_time") != null) {
                        sqlquery.setTimestamp("create_time", (Date) dataItem.get("create_time"));
                    }
                    if (dataItem.get("order_id") != null) {
                        sqlquery.setInteger("order_id", Integer.parseInt(dataItem.get("order_id").toString()));
                    }
                    if (dataItem.get("action") != null) {
                        sqlquery.setInteger("action", Integer.parseInt(dataItem.get("action").toString()));
                    }
                    if (dataItem.get("feedback") != null) {
                        sqlquery.setString("feedback", dataItem.get("feedback").toString());
                    }
                    if (dataItem.get("direction") != null) {
                        sqlquery.setInteger("direction", Integer.parseInt(dataItem.get("direction").toString()));
                    }
                    if (dataItem.get("remark") != null) {
                        sqlquery.setString("remark", dataItem.get("remark").toString());
                    }
                    if (dataItem.get("stable_name") != null) {
                        sqlquery.setString("stable_name", dataItem.get("stable_name").toString());
                    }
                    if (dataItem.get("status") != null) {
                        sqlquery.setInteger("status", 2);
                    }
                    if (dataItem.get("suuid") != null) {
                        sqlquery.setString("suuid", dataItem.get("suuid").toString());
                    }
                    if (dataItem.get("syn_time") != null) {
                        sqlquery.setTimestamp("syn_time", (Date) dataItem.get("syn_time"));
                    }
                    if (dataItem.get("composite_key") != null) {
                        sqlquery.setString("composite_key", dataItem.get("composite_key").toString());
                    }
                    if (dataItem.get("cloum_num") != null) {
                        sqlquery.setInteger("cloum_num", Integer.parseInt(dataItem.get("cloum_num").toString()));
                    }
                    sqlquery.setTimestamp("backup_time", backupTime);
                    sqlquery.executeUpdate();
                }
                // 大字段的处理
                for (Map<String, Object> dataItem : CLOBList) {
                    // 更新大字段
                    String insertSql = "insert into tig_column_clob "
                            + "(,tig_owner_uuid,tig_column_name,data_uuid,composite_key,data_clob,data_status,direction,stable_name,syn_time,create_time,backup_time) "
                            + "values "
                            + "(,:tig_owner_uuid,:tig_column_name,:data_uuid,:composite_key,:data_clob,:data_status,:direction,:stable_name,:syn_time,:create_time,:backup_time) ";
                    for (String columKey : dataItem.keySet()) {
                        if (dataItem.get(columKey) == null && !columKey.equals("backup_time")) {
                            insertSql = insertSql.replace("," + columKey, "").replace(",:" + columKey, "");
                        }
                    }
                    // 操作大字段同步记录
                    insertSql = insertSql.replace("(,", "(");
                    SQLQuery sqlquery = session3.createSQLQuery(insertSql);
                    if (dataItem.get("tig_owner_uuid") != null) {
                        sqlquery.setString("tig_owner_uuid", dataItem.get("tig_owner_uuid").toString());
                    }
                    if (dataItem.get("tig_column_name") != null) {
                        sqlquery.setString("tig_column_name", dataItem.get("tig_column_name").toString());
                    }
                    if (dataItem.get("data_uuid") != null) {
                        sqlquery.setString("data_uuid", dataItem.get("data_uuid").toString());
                    }
                    if (dataItem.get("composite_key") != null) {
                        sqlquery.setString("composite_key", dataItem.get("composite_key").toString());
                    }
                    if (dataItem.get("data_clob") != null) {
                        sqlquery.setText("data_clob",
                                IOUtils.toString(((Clob) dataItem.get("data_clob")).getCharacterStream()).toString());
                    }
                    if (dataItem.get("data_char") != null) {
                        sqlquery.setCharacter("data_char", dataItem.get("data_char").toString().charAt(0));
                    }
                    if (dataItem.get("data_status") != null) {
                        sqlquery.setInteger("data_status", 2);
                    }
                    if (dataItem.get("direction") != null) {
                        sqlquery.setInteger("direction", Integer.parseInt(dataItem.get("direction").toString()));
                    }
                    if (dataItem.get("stable_name") != null) {
                        sqlquery.setString("stable_name", dataItem.get("stable_name").toString());
                    }
                    if (dataItem.get("create_time") != null) {
                        sqlquery.setTimestamp("create_time", (Date) dataItem.get("create_time"));
                    }
                    if (dataItem.get("syn_time") != null) {
                        sqlquery.setTimestamp("syn_time", (Date) dataItem.get("syn_time"));
                    }
                    sqlquery.setTimestamp("backup_time", backupTime);
                    sqlquery.executeUpdate();
                }
            } catch (Exception e) {
                throw e;
            } finally {
                session3.close();
            }
            /*********************** 3、修改流入前置机状态（避免多次提取） *****************************/
            Session session4 = this.getDao("QZJ" + unitId).getSession();
            try {
                String dataUuids = "";
                for (Map<String, Object> dataItem : dataList) {
                    String uuid = dataItem.get("uuid").toString();
                    dataUuids += ",'" + uuid + "'";
                }
                if (!StringUtils.isBlank(dataUuids)) {
                    SQLQuery sqlquery = session4
                            .createSQLQuery("update tig_table_data o set o.status=2,o.backup_time=:backup_time where o.uuid in ("
                                    + dataUuids.replaceFirst(",", "") + ")");
                    sqlquery.setTimestamp("backup_time", backupTime);
                    sqlquery.executeUpdate();
                }

                for (Map<String, Object> dataItem : CLOBList) {
                    String tig_owner_uuid = dataItem.get("tig_owner_uuid").toString();
                    String tig_column_name = dataItem.get("tig_column_name").toString();
                    SQLQuery sqlquery = session4
                            .createSQLQuery("update tig_column_clob o set o.data_status=2,o.backup_time=:backup_time where o.tig_owner_uuid='"
                                    + tig_owner_uuid + "' and o.tig_column_name='" + tig_column_name + "'");
                    sqlquery.setTimestamp("backup_time", backupTime);
                    sqlquery.executeUpdate();
                }
            } catch (Exception e) {
                throw e;
            } finally {
                session4.close();
            }

        } catch (Exception e) {
            logger.error("**********************synInData " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 还原数据(非大字段)
     *
     * @param dataItem
     * @param columDataFormat
     * @param ftpDataBean
     * @return
     * @throws Exception
     */
    public void backData(String unitId) throws Exception {
        Date feedbackTime = new Date();
        List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> columDataList = new ArrayList<Map<String, Object>>();
        Map<String, List<Map<String, Object>>> columDataFormat = new HashMap<String, List<Map<String, Object>>>();
        /***************** 1、查询还原的数据 ************/
        Session session = this.getDao("QJ" + unitId).getSession();
        String uuid = "";
        try {
            String querySql = "select list.* from (select * from tig_table_data o where o.status=2 order by o.order_id asc) list where rownum<="
                    + DATANUM;
            // 提取同步中及反馈的数据
            List<Map<String, Object>> dataListTemp = session.createSQLQuery(querySql)
                    .setResultTransformer(ExchangeDataResultTransformer.INSTANCE).list();
            if (dataListTemp.size() == 0) {
                return;
            }
            // 还原的字段与同步的字段是否一致
            for (Map<String, Object> dataItem : dataListTemp) {
                uuid = dataItem.get("uuid").toString();
                int cloum_num = Integer.parseInt(dataItem.get("cloum_num").toString());
                // 业务字段表
                String subQuerySql = "select * from tig_column_data t where t.tig_owner_uuid = '" + uuid + "'";
                List<Map<String, Object>> columDataByOneData = session.createSQLQuery(subQuerySql)
                        .setResultTransformer(ExchangeDataResultTransformer.INSTANCE).list();
                int columDataByOneDataNum = columDataByOneData.size();
                if (cloum_num == columDataByOneDataNum) {
                    dataList.add(dataItem);
                    columDataList.addAll(columDataByOneData);
                    columDataFormat.put(uuid, columDataByOneData);
                } else {
                    continue;
                }
            }
            /****************** 2、还原数据 **********************/
            for (Map<String, Object> dataItem : dataList) {
                uuid = dataItem.get("uuid").toString();
                String tableName = dataItem.get("stable_name") == null ? "" : dataItem.get("stable_name").toString();
                int action = Integer.parseInt(dataItem.get("action").toString());
                String suuid = dataItem.get("suuid").toString();
                if (action == 3) {// 删除
                    String backDelSql = "";
                    String compositeKey = dataItem.get("composite_key") == null ? "" : dataItem.get("composite_key")
                            .toString();
                    if (!StringUtils.isBlank(compositeKey)) {// 联合主键（多对多关系表）
                        String[] compositeKeyArr = compositeKey.split(";");
                        String[] compositeValueArr = suuid.split(";");
                        String where = "";
                        for (int i = 0; i < compositeKeyArr.length; i++) {
                            where += " and " + compositeKeyArr[i] + "='" + compositeValueArr[i] + "'";
                        }
                        where = where.replaceFirst(" and ", "");
                        backDelSql = "delete " + tableName + " where " + where;
                    } else {
                        backDelSql = "delete " + tableName + " where uuid = '" + suuid + "'";
                    }

                    session.createSQLQuery(backDelSql).executeUpdate();
                } else {// 更新插入
                    // 处理字段数据
                    List<Map<String, Object>> columList = columDataFormat.get(uuid);
                    if (columList != null) {
                        String columns = "";
                        String insertColumn = "";
                        String updateColumn = "";
                        Map<String, Object> values = new HashMap<String, Object>();
                        Map<String, String> types = new HashMap<String, String>();

                        for (Map<String, Object> columMap : columList) {
                            String columnName = columMap.get("tig_column_name").toString();
                            String columnType = columMap.get("tig_data_type").toString();

                            if (columnType.equals("TIMESTAMP(6)") && columMap.get("data_time") != null) {
                                columns += "," + columnName;
                                insertColumn += ",:" + columnName;
                                updateColumn += "," + columnName + "=:" + columnName;
                                values.put(columnName, columMap.get("data_time"));
                                types.put(columnName, "TIMESTAMP(6)");
                            } else if (columnType.equals("FLOAT") && columMap.get("data_float") != null) {
                                columns += "," + columnName;
                                insertColumn += ",:" + columnName;
                                updateColumn += "," + columnName + "=:" + columnName;
                                values.put(columnName, columMap.get("data_float"));
                                types.put(columnName, "FLOAT");
                            } else if (columnType.equals("NUMBER") && columMap.get("data_number") != null) {
                                columns += "," + columnName;
                                insertColumn += ",:" + columnName;
                                updateColumn += "," + columnName + "=:" + columnName;
                                values.put(columnName, columMap.get("data_number"));
                                types.put(columnName, "NUMBER");
                            } else if (columnType.equals("CLOB") && columMap.get("data_clob") != null) {
                                columns += "," + columnName;
                                insertColumn += ",:" + columnName;
                                updateColumn += "," + columnName + "=:" + columnName;
                                values.put(columnName, columMap.get("data_clob"));
                                types.put(columnName, "CLOB");
                            } else if (columnType.equals("CHAR") && columMap.get("data_char") != null) {
                                columns += "," + columnName;
                                insertColumn += ",:" + columnName;
                                updateColumn += "," + columnName + "=:" + columnName;
                                values.put(columnName, columMap.get("data_char"));
                                types.put(columnName, "CHAR");
                            } else if (columnType.equals("DATE") && columMap.get("data_date") != null) {
                                columns += "," + columnName;
                                insertColumn += ",:" + columnName;
                                updateColumn += "," + columnName + "=:" + columnName;
                                values.put(columnName, columMap.get("data_date"));
                                types.put(columnName, "DATE");
                            } else if (columnType.equals("VARCHAR2")) {
                                columns += "," + columnName;
                                insertColumn += ",:" + columnName;
                                updateColumn += "," + columnName + "=:" + columnName;
                                values.put(columnName,
                                        columMap.get("data_varchar_2") == null ? "" : columMap.get("data_varchar_2")
                                                .toString());
                                types.put(columnName, "VARCHAR2");
                            }
                        }
                        columns = columns.replaceFirst(",", "");
                        insertColumn = insertColumn.replaceFirst(",", "");
                        updateColumn = updateColumn.replaceFirst(",", "");
                        // 插入更新数据
                        String compositeKey = dataItem.get("composite_key") == null ? "" : dataItem
                                .get("composite_key").toString();
                        String where = "";
                        if (!StringUtils.isBlank(compositeKey)) {// 联合主键（多对多关系表）
                            String[] compositeKeyArr = compositeKey.split(";");
                            String[] compositeValueArr = suuid.split(";");
                            for (int i = 0; i < compositeKeyArr.length; i++) {
                                where += " and " + compositeKeyArr[i] + "='" + compositeValueArr[i] + "'";
                            }
                            where = where.replaceFirst(" and ", "");
                        } else {
                            where = " uuid = '" + suuid + "'";
                        }

                        String sql = "";
                        if (action == 1) {// 插入
                            sql = "insert into " + tableName + "(" + columns + ") values (" + insertColumn + ")";
                        } else if (action == 2) {// 更新
                            sql = "update " + tableName + " set " + updateColumn + " where " + where;
                        }
                        SQLQuery dataSqlquery = session.createSQLQuery(sql);
                        for (String key : values.keySet()) {
                            if (types.get(key).equals("TIMESTAMP(6)")) {
                                dataSqlquery.setTimestamp(key, (Date) values.get(key));
                            } else if (types.get(key).equals("FLOAT")) {
                                dataSqlquery.setFloat(key, Float.parseFloat(values.get(key).toString()));
                            } else if (types.get(key).equals("NUMBER")) {
                                dataSqlquery.setDouble(key, Double.parseDouble(values.get(key).toString()));
                            } else if (types.get(key).equals("CLOB")) {
                                dataSqlquery.setString(key, values.get(key).toString());
                            } else if (types.get(key).equals("CHAR")) {
                                dataSqlquery.setCharacter(key, values.get(key).toString().charAt(0));
                            } else if (types.get(key).equals("DATE")) {
                                dataSqlquery.setTimestamp(key, (Date) values.get(key));
                            } else if (types.get(key).equals("VARCHAR2")) {
                                dataSqlquery.setString(key, values.get(key).toString());
                            }
                        }
                        dataSqlquery.executeUpdate();
                    }
                }
                // 还原成功，更新状态及还原时间
                SQLQuery sqlquery = session
                        .createSQLQuery("update tig_table_data o set o.status=3,o.feedback_time=:feedback_time where o.uuid = '"
                                + uuid + "'");
                sqlquery.setTimestamp("feedback_time", feedbackTime);
                sqlquery.executeUpdate();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("**********************backData " + uuid + " " + e.getMessage(), e);
            throw e;
        } finally {
            session.close();
        }
    }

    /**
     * 还原数据（大字段）
     *
     * @param dataItem
     * @param columDataFormat
     * @param ftpDataBean
     * @return
     * @throws Exception
     */
    public void backDataCLOB(String unitId) {
        Date feedbackTime = new Date();
        List<Map<String, Object>> CLOBList = new ArrayList<Map<String, Object>>();
        Session session = this.getDao("QJ" + unitId).getSession();
        String clobSql = "select list.* from (select * from tig_column_clob t where t.data_status=2) list where rownum<="
                + DATANUM;
        CLOBList = session.createSQLQuery(clobSql).setResultTransformer(ExchangeDataResultTransformer.INSTANCE).list();
        for (Map<String, Object> dataItem : CLOBList) {
            String tig_owner_uuid = dataItem.get("tig_owner_uuid").toString();
            String tig_column_name = dataItem.get("tig_column_name").toString();
            try {
                String tableName = dataItem.get("stable_name") == null ? "" : dataItem.get("stable_name").toString();
                String dataUuid = dataItem.get("data_uuid").toString();
                String compositeKey = dataItem.get("composite_key") == null ? "" : dataItem.get("composite_key")
                        .toString();
                String where = "";
                if (!StringUtils.isBlank(compositeKey)) {// 联合主键（多对多关系表）
                    String[] compositeKeyArr = compositeKey.split(";");
                    String[] compositeValueArr = dataUuid.split(";");
                    for (int i = 0; i < compositeKeyArr.length; i++) {
                        where += " and " + compositeKeyArr[i] + "='" + compositeValueArr[i] + "'";
                    }
                    where = where.replaceFirst(" and ", "");
                } else {
                    where = " uuid = '" + dataUuid + "'";
                }
                String querySql = "select count(*) as count_ from " + tableName + " where " + where;
                List<Map<String, Object>> countList = session.createSQLQuery(querySql)
                        .setResultTransformer(ExchangeDataResultTransformer.INSTANCE).list();
                int count_ = Integer.parseInt(countList.get(0).get("count_").toString());
                if (count_ == 0) {
                    continue;
                }

                String columnName = dataItem.get("tig_column_name").toString();
                String dataVal = "";
                if (dataItem.get("data_clob") != null) {
                    Clob clob = (Clob) dataItem.get("data_clob");
                    dataVal = IOUtils.toString(clob.getCharacterStream()).toString();
                }
                String sql = "update " + tableName + " set " + columnName + "=:" + columnName + " where " + where;
                SQLQuery dataSqlquery = session.createSQLQuery(sql);
                dataSqlquery.setText(columnName, dataVal);
                dataSqlquery.executeUpdate();
                // 还原成功，更新状态及还原时间
                SQLQuery sqlquery = session
                        .createSQLQuery("update tig_column_clob o set o.data_status=3,o.feedback_time=:feedback_time where o.tig_owner_uuid = :tig_owner_uuid,tig_column_name = :tig_column_name");
                sqlquery.setTimestamp("feedback_time", feedbackTime);
                sqlquery.setString("tig_owner_uuid", tig_owner_uuid);
                sqlquery.setString("tig_column_name", tig_column_name);
                sqlquery.executeUpdate();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                logger.error(
                        "**********************backData " + tig_owner_uuid + " " + tig_column_name + " "
                                + e.getMessage(), e);
            }
        }
    }

    /**
     * 备份交换成功的数据，同时清理数据
     */
    @Override
    public void clearSynData() throws Exception {
        // TODO Auto-generated method stub
        String unitId = Config.getValue("center.unit.id");
        if (unitId.equals(Config.getValue("sqld.sj.unit.id"))) {// 市级系统
            // 清理各个区级租户还原成功的数据
            Map<String, String> qjUnitId = getQJUnitId();
            for (String key : qjUnitId.keySet()) {
                Session session = this.getDao("QJ" + qjUnitId.get(key)).getSession();
                try {
                    session.createSQLQuery("delete tig_table_data o where o.status=3")
                            .setResultTransformer(ExchangeDataResultTransformer.INSTANCE).executeUpdate();
                    session.createSQLQuery("delete tig_column_clob o where o.data_status=3")
                            .setResultTransformer(ExchangeDataResultTransformer.INSTANCE).executeUpdate();
                } catch (Exception e) {
                    throw e;
                } finally {
                    session.close();
                }
            }
        } else {
            // 清理本租户的数据
            Session session = this.dao.getSession();
            try {
                session.createSQLQuery("delete tig_table_data o where o.status=2")
                        .setResultTransformer(ExchangeDataResultTransformer.INSTANCE).executeUpdate();
                session.createSQLQuery("delete tig_column_clob o where o.data_status=2")
                        .setResultTransformer(ExchangeDataResultTransformer.INSTANCE).executeUpdate();
            } catch (Exception e) {
                throw e;
            } finally {
                session.close();
            }
            // 清理前置机数据
            Session session1 = this.getDao("QZJ" + unitId).getSession();
            try {
                session1.createSQLQuery("delete tig_table_data o where o.status=2")
                        .setResultTransformer(ExchangeDataResultTransformer.INSTANCE).executeUpdate();
                session1.createSQLQuery("delete tig_column_clob o where o.data_status=2")
                        .setResultTransformer(ExchangeDataResultTransformer.INSTANCE).executeUpdate();
            } catch (Exception e) {
                throw e;
            } finally {
                session1.close();
            }
        }
    }

    public boolean isSeries(List<Map<String, Object>> dataList, String cloumName) {
        int seriesVal = 0;
        for (Map<String, Object> obj : dataList) {
            Integer val = Integer.parseInt(obj.get(cloumName).toString());
            if (seriesVal == 0) {
                seriesVal = val;
            } else {
                if (seriesVal <= val) {
                    logger.error("****************数据不连号,还原中断，等待同步：order_id=" + val);
                    return false;
                }
                seriesVal++;
            }
        }
        return true;
    }

    @Override
    public void backupData(String unitId) throws Exception {
        // TODO Auto-generated method stub
        this.backData(unitId);
        this.backDataCLOB(unitId);

    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataSynSQLDService#trunkSynOutData()
     */
    @SuppressWarnings("unchecked")
    @Override
    public void trunkSynOutData() throws Exception {

        // String unitId = Config.getValue("center.unit.id");
        Date synTime = new Date();// 同步时间
        Map<String, List<Map<String, Object>>> resultMap = new HashMap<String, List<Map<String, Object>>>();
        byte[] resultBtyes = null;
        /*********************** 1、提取系统数据 先提取表数据再提取字段数据 ****************************/
        try {
            List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> columDataList = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> CLOBList = new ArrayList<Map<String, Object>>();

            try {
                // 提取同步数据
                String querySql = "select list.* from (select * from tig_table_data o where o.status=1 order by o.order_id asc) list where rownum<="
                        + DATANUM;// 未同步
                // 提取大字段
                String clobSql = "select list.* from (select * from tig_column_clob t where t.data_status=1) list where rownum<="
                        + DATANUM;
                List<Map<String, Object>> clobDataByOneData = this.dao.getSession().createSQLQuery(clobSql)
                        .setResultTransformer(ExchangeDataResultTransformer.INSTANCE).list();
                CLOBList.addAll(clobDataByOneData);
                dataList = this.dao.getSession().createSQLQuery(querySql)
                        .setResultTransformer(ExchangeDataResultTransformer.INSTANCE).list();
                if (dataList.size() == 0 && CLOBList.size() == 0) {
                    return;
                }
                // 提取字段数据
                for (Map<String, Object> dataItem : dataList) {
                    String uuid = dataItem.get("uuid").toString();
                    // 业务字段表
                    String subQuerySql = "select * from tig_column_data t where t.tig_owner_uuid = '" + uuid + "'";
                    List<Map<String, Object>> columDataByOneData = this.dao.getSession().createSQLQuery(subQuerySql)
                            .setResultTransformer(ExchangeDataResultTransformer.INSTANCE).list();
                    int columDataByOneDataNum = columDataByOneData.size();
                    columDataList.addAll(columDataByOneData);
                    // 写入同步时间及更新的字段数量及状态
                    String updateSql = "update tig_table_data o set o.syn_time=:syn_time,o.cloum_num=:columDataByOneDataNum,o.status=2 where o.uuid = :uuid";
                    SQLQuery sqlquery = this.dao.getSession().createSQLQuery(updateSql);
                    sqlquery.setTimestamp("syn_time", synTime);
                    sqlquery.setInteger("columDataByOneDataNum", columDataByOneDataNum);
                    sqlquery.setString("uuid", uuid);
                    sqlquery.executeUpdate();
                    dataItem.put("cloum_num", columDataByOneDataNum);
                }
                for (Map<String, Object> dataItem : CLOBList) {// 更新同步时间 及状态
                    dataItem.put("data_clob", IOUtils.toString(((Clob) dataItem.get("data_clob")).getCharacterStream()));
                    String tig_owner_uuid = dataItem.get("tig_owner_uuid").toString();
                    String tig_column_name = dataItem.get("tig_column_name").toString();
                    String updateSql = "update tig_column_clob o set o.syn_time=:syn_time,o.data_status=2 where o.tig_owner_uuid='"
                            + tig_owner_uuid + "' and o.tig_column_name='" + tig_column_name + "'";
                    SQLQuery sqlquery = this.dao.getSession().createSQLQuery(updateSql);
                    sqlquery.setTimestamp("syn_time", synTime);
                    sqlquery.executeUpdate();
                }
                resultMap.put("tig_table_data", dataList);
                resultMap.put("tig_column_data", columDataList);
                resultMap.put("tig_column_clob", CLOBList);
                resultBtyes = SerializableUtil.ObjectToByte(resultMap);
                zongXianExchangeServiceImpl.submit("", resultBtyes, null, "", false);
            } catch (Exception e) {
                logger.error("**********************trunkSynOutData " + e.getMessage(), e);
                throw e;
            }
        } catch (Exception e) {
            logger.error("**********************trunkSynOutData " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataSynSQLDService#trunkSynInData(java.lang.String)
     */
    @Override
    public void trunkSynInData(Map<String, List<Map<String, Object>>> dataMap) throws Exception {
        if (dataMap == null) {
            return;
        }
        Date backupTime = new Date();// 提取时间
        try {

            /*********************** 1、提取前置机数据(同步中，同步成功/同步失败(握手)) ****************************/
            List<Map<String, Object>> dataList = dataMap.get("tig_table_data") == null ? new ArrayList<Map<String, Object>>()
                    : dataMap.get("tig_table_data");
            List<Map<String, Object>> columDataList = dataMap.get("tig_column_data") == null ? new ArrayList<Map<String, Object>>()
                    : dataMap.get("tig_column_data");
            List<Map<String, Object>> CLOBList = dataMap.get("tig_column_clob") == null ? new ArrayList<Map<String, Object>>()
                    : dataMap.get("tig_column_clob");
            /*********************** 2、变更数据到系统 *****************************/
            Session session1 = this.getDao("QJ" + XZSPBIZ.JM_AREA_CODE).getSession();
            try {
                String mainUuids = ",";
                Map<String, Map<String, Object>> columMap = new HashMap<String, Map<String, Object>>();
                // 字段表插入
                for (Map<String, Object> dataItem : columDataList) {
                    String tig_owner_uuid = dataItem.get("tig_owner_uuid").toString();
                    String tig_column_name = dataItem.get("tig_column_name").toString();
                    if (mainUuids.indexOf("," + tig_owner_uuid + ",") < 0) {
                        mainUuids += tig_owner_uuid + ",";
                    }
                    columMap.put(tig_owner_uuid + "_" + tig_column_name, dataItem);

                    String insertSql = "insert into tig_column_data "
                            + "(,tig_owner_uuid,tig_column_name,tig_data_type,data_time,data_float,data_number,data_clob,data_char,data_date,data_varchar_2) "
                            + "values "
                            + "(,:tig_owner_uuid,:tig_column_name,:tig_data_type,:data_time,:data_float,:data_number,:data_clob,:data_char,:data_date,:data_varchar_2) ";
                    for (String columKey : dataItem.keySet()) {
                        if (dataItem.get(columKey) == null) {
                            insertSql = insertSql.replace("," + columKey, "").replace(",:" + columKey, "");
                        }
                    }
                    insertSql = insertSql.replace("(,", "(");
                    SQLQuery sqlquery = session1.createSQLQuery(insertSql);
                    if (dataItem.get("tig_owner_uuid") != null) {
                        sqlquery.setString("tig_owner_uuid", dataItem.get("tig_owner_uuid").toString());
                    }
                    if (dataItem.get("tig_column_name") != null) {
                        sqlquery.setString("tig_column_name", dataItem.get("tig_column_name").toString());
                    }
                    if (dataItem.get("tig_data_type") != null) {
                        sqlquery.setString("tig_data_type", dataItem.get("tig_data_type").toString());
                    }
                    if (dataItem.get("data_time") != null) {
                        sqlquery.setTimestamp("data_time", (Date) dataItem.get("data_time"));
                    }
                    if (dataItem.get("data_float") != null) {
                        sqlquery.setFloat("data_float", Float.parseFloat(dataItem.get("data_float").toString()));
                    }
                    if (dataItem.get("data_number") != null) {
                        sqlquery.setDouble("data_number", Double.parseDouble(dataItem.get("data_number").toString()));
                    }
                    if (dataItem.get("data_clob") != null) {
                        sqlquery.setText("data_clob", dataItem.get("data_clob").toString());
                    }
                    if (dataItem.get("data_char") != null) {
                        sqlquery.setCharacter("data_char", dataItem.get("data_char").toString().charAt(0));
                    }
                    if (dataItem.get("data_date") != null) {
                        sqlquery.setDate("data_date", (Date) dataItem.get("data_date"));
                    }
                    if (dataItem.get("data_varchar_2") != null) {
                        sqlquery.setString("data_varchar_2", dataItem.get("data_varchar_2").toString());
                    }
                    sqlquery.executeUpdate();
                }
                // 插入数据
                for (Map<String, Object> dataItem : dataList) {
                    // 插入数据
                    String insertSql = "insert into tig_table_data "
                            + "(,uuid,create_time,order_id,action,feedback,direction,remark,stable_name,status,suuid,syn_time,composite_key,cloum_num,backup_time) "
                            + "values "
                            + "(,:uuid,:create_time,:order_id,:action,:feedback,:direction,:remark,:stable_name,:status,:suuid,:syn_time,:composite_key,:cloum_num,:backup_time)";
                    for (String dataKey : dataItem.keySet()) {
                        if (dataItem.get(dataKey) == null && !dataKey.equals("backup_time")) {
                            insertSql = insertSql.replace("," + dataKey, "").replace(",:" + dataKey, "");
                        }
                    }
                    insertSql = insertSql.replace("(,", "(");
                    SQLQuery sqlquery = session1.createSQLQuery(insertSql);

                    if (dataItem.get("uuid") != null) {
                        sqlquery.setString("uuid", dataItem.get("uuid").toString());
                    }
                    if (dataItem.get("create_time") != null) {
                        sqlquery.setTimestamp("create_time", (Date) dataItem.get("create_time"));
                    }
                    if (dataItem.get("order_id") != null) {
                        sqlquery.setInteger("order_id", Integer.parseInt(dataItem.get("order_id").toString()));
                    }
                    if (dataItem.get("action") != null) {
                        sqlquery.setInteger("action", Integer.parseInt(dataItem.get("action").toString()));
                    }
                    if (dataItem.get("feedback") != null) {
                        sqlquery.setString("feedback", dataItem.get("feedback").toString());
                    }
                    if (dataItem.get("direction") != null) {
                        sqlquery.setInteger("direction", Integer.parseInt(dataItem.get("direction").toString()));
                    }
                    if (dataItem.get("remark") != null) {
                        sqlquery.setString("remark", dataItem.get("remark").toString());
                    }
                    if (dataItem.get("stable_name") != null) {
                        sqlquery.setString("stable_name", dataItem.get("stable_name").toString());
                    }
                    if (dataItem.get("status") != null) {
                        sqlquery.setInteger("status", 2);
                    }
                    if (dataItem.get("suuid") != null) {
                        sqlquery.setString("suuid", dataItem.get("suuid").toString());
                    }
                    if (dataItem.get("syn_time") != null) {
                        sqlquery.setTimestamp("syn_time", (Date) dataItem.get("syn_time"));
                    }
                    if (dataItem.get("composite_key") != null) {
                        sqlquery.setString("composite_key", dataItem.get("composite_key").toString());
                    }
                    if (dataItem.get("cloum_num") != null) {
                        sqlquery.setInteger("cloum_num", Integer.parseInt(dataItem.get("cloum_num").toString()));
                    }
                    sqlquery.setTimestamp("backup_time", backupTime);
                    sqlquery.executeUpdate();
                }
                // 大字段的处理
                for (Map<String, Object> dataItem : CLOBList) {
                    // 更新大字段
                    String insertSql = "insert into tig_column_clob "
                            + "(,tig_owner_uuid,tig_column_name,data_uuid,composite_key,data_clob,data_status,direction,stable_name,syn_time,create_time,backup_time) "
                            + "values "
                            + "(,:tig_owner_uuid,:tig_column_name,:data_uuid,:composite_key,:data_clob,:data_status,:direction,:stable_name,:syn_time,:create_time,:backup_time) ";
                    for (String columKey : dataItem.keySet()) {
                        if (dataItem.get(columKey) == null && !columKey.equals("backup_time")) {
                            insertSql = insertSql.replace("," + columKey, "").replace(",:" + columKey, "");
                        }
                    }
                    // 操作大字段同步记录
                    insertSql = insertSql.replace("(,", "(");
                    SQLQuery sqlquery = session1.createSQLQuery(insertSql);
                    if (dataItem.get("tig_owner_uuid") != null) {
                        sqlquery.setString("tig_owner_uuid", dataItem.get("tig_owner_uuid").toString());
                    }
                    if (dataItem.get("tig_column_name") != null) {
                        sqlquery.setString("tig_column_name", dataItem.get("tig_column_name").toString());
                    }
                    if (dataItem.get("data_uuid") != null) {
                        sqlquery.setString("data_uuid", dataItem.get("data_uuid").toString());
                    }
                    if (dataItem.get("composite_key") != null) {
                        sqlquery.setString("composite_key", dataItem.get("composite_key").toString());
                    }
                    if (dataItem.get("data_clob") != null) {
                        sqlquery.setText("data_clob", (dataItem.get("data_clob")).toString());
                    }
                    if (dataItem.get("data_char") != null) {
                        sqlquery.setCharacter("data_char", dataItem.get("data_char").toString().charAt(0));
                    }
                    if (dataItem.get("data_status") != null) {
                        sqlquery.setInteger("data_status", 2);
                    }
                    if (dataItem.get("direction") != null) {
                        sqlquery.setInteger("direction", Integer.parseInt(dataItem.get("direction").toString()));
                    }
                    if (dataItem.get("stable_name") != null) {
                        sqlquery.setString("stable_name", dataItem.get("stable_name").toString());
                    }
                    if (dataItem.get("create_time") != null) {
                        sqlquery.setTimestamp("create_time", (Date) dataItem.get("create_time"));
                    }
                    if (dataItem.get("syn_time") != null) {
                        sqlquery.setTimestamp("syn_time", (Date) dataItem.get("syn_time"));
                    }
                    sqlquery.setTimestamp("backup_time", backupTime);
                    sqlquery.executeUpdate();
                }
                session1.flush();
            } catch (Exception e) {
                logger.error("**********************trunkSynInData " + e.getMessage(), e);
                throw e;
            } finally {
                saveReadAndWriteRecord(dataList, columDataList, CLOBList);
            }

        } catch (Exception e) {
            logger.error("**********************trunkSynInData " + e.getMessage(), e);
            throw e;
        }

    }

    @SuppressWarnings("unchecked")
    public void saveReadAndWriteRecord(List<Map<String, Object>> dataList, List<Map<String, Object>> columDataList,
                                       List<Map<String, Object>> CLOBList) {
        Date cuurrentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String datestr = sdf.format(cuurrentDate);
        StringBuffer tableDataSql = new StringBuffer();
        tableDataSql.append("select * from tig_table_data where to_char(syn_time,'yyyy-mm-dd')='");
        tableDataSql.append(datestr);
        tableDataSql.append("'");

        StringBuffer columnDataSql = new StringBuffer();
        columnDataSql
                .append("select c.*  from tig_column_data c join tig_table_data t on  c.tig_owner_uuid=t.uuid  where to_char(t.syn_time,'yyyy-mm-dd')='");
        columnDataSql.append(datestr);
        columnDataSql.append("'");

        StringBuffer columnClobSql = new StringBuffer();
        columnClobSql.append("select *  from tig_column_clob where to_char(syn_time,'yyyy-mm-dd')='");
        columnClobSql.append(datestr);
        columnClobSql.append("'");
        Session session1 = this.getDao("QJ" + XZSPBIZ.JM_AREA_CODE).getSession();

        List<Map<String, Object>> tableCounts = session1.createSQLQuery(tableDataSql.toString()).list();
        List<Map<String, Object>> columnCounts = session1.createSQLQuery(columnDataSql.toString()).list();
        List<Map<String, Object>> clobCounts = session1.createSQLQuery(columnClobSql.toString()).list();
        long tableCount = 0, columnCount = 0, clobCount = 0;
        if (tableCounts != null && !tableCounts.isEmpty()) {
            tableCount = tableCounts.size();
        }
        if (columnCounts != null && !columnCounts.isEmpty()) {
            columnCount = columnCounts.size();
        }
        if (clobCounts != null && !clobCounts.isEmpty()) {
            clobCount = clobCounts.size();
        }

        // 屏蔽具体的业务代码 --zyguo
        // List<XzspZxdxRecord> records =
        // xzspZxdxRecordDao.getRecord(cuurrentDate, null, 1);
        // if (records != null && !records.isEmpty()) {
        // for (XzspZxdxRecord record : records) {
        // String tableName = record.getTableName();
        // long size = 0;
        // if ("tig_table_data".equals(tableName)) {
        // size = dataList.size();
        // record.setSjJsCount(tableCount);
        // } else if ("tig_column_data".equals(tableName)) {
        // size = columDataList.size();
        // record.setSjJsCount(columnCount);
        // } else {
        // size = CLOBList.size();
        // record.setSjJsCount(clobCount);
        // }
        // record.setJmXrCount(record.getJmXrCount() + size);
        // }
        // } else {
        // records = new ArrayList<XzspZxdxRecord>();
        // XzspZxdxRecord record1 = new XzspZxdxRecord();
        // record1.setSjJsCount(tableCount);
        // record1.setJmXrCount((long) dataList.size());
        // record1.setTriggerTime(cuurrentDate);
        // record1.setTableName("tig_table_data");
        // record1.setDataType(1);
        //
        // XzspZxdxRecord record2 = new XzspZxdxRecord();
        // record2.setSjJsCount(columnCount);
        // record2.setJmXrCount((long) columDataList.size());
        // record2.setTriggerTime(cuurrentDate);
        // record2.setTableName("tig_column_data");
        // record2.setDataType(1);
        //
        // XzspZxdxRecord record3 = new XzspZxdxRecord();
        // record3.setSjJsCount(clobCount);
        // record3.setJmXrCount((long) CLOBList.size());
        // record3.setTriggerTime(cuurrentDate);
        // record3.setTableName("tig_column_clob");
        // record3.setDataType(1);
        //
        // records.add(record1);
        // records.add(record2);
        // records.add(record3);
        // }
        // xzspZxdxRecordDao.saveAll(records);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataSynSQLDService#triggerCountOut()
     */
    @Override
    public void triggerCountOut() throws Exception {
        // 屏蔽业务代码 --zyguo
        // long count = xzspZxjmRecordDao.getReCordCount(new Date());
        // Map<String, Long> map = new HashMap<String, Long>();
        // map.put("triggerCount", count);
        // byte[] resultBtyes = SerializableUtil.ObjectToByte(map);
        // zongXianExchangeServiceImpl.submit("", resultBtyes, null, "", false);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataSynSQLDService#triggerCountIn(java.util.Map)
     */
    @Override
    public void triggerCountIn(Map<String, Long> dataMap) throws Exception {
        // 屏蔽业务代码 --zyguo
        // if (dataMap == null) {
        // return;
        // }
        // Date currentDate = new Date();
        // long triggerCount = dataMap.get("triggerCount") == null ? 0 :
        // dataMap.get("triggerCount");
        // long count = xzspZxsjRecordDao.getReCordCount(currentDate);
        // XzspZxdxRecord record = new XzspZxdxRecord();
        // record.setTriggerTime(currentDate);
        // record.setJmXrCount(triggerCount);
        // record.setSjJsCount(count);
        // record.setDataType(2);
        // xzspZxdxRecordDao.save(record);
    }
}