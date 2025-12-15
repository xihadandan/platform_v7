package com.wellsoft.pt.integration.service.impl;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.util.UuidUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.dto.DyformSubformFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.integration.entity.DataOperationLog;
import com.wellsoft.pt.integration.entity.ExchangeData;
import com.wellsoft.pt.integration.entity.ExchangeDataBatch;
import com.wellsoft.pt.integration.service.*;
import com.wellsoft.pt.integration.support.ExchangeDataResultTransformer;
import com.wellsoft.pt.integration.support.WebServiceMessage;
import com.wellsoft.pt.jpa.dao.UniversalDao;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

/*import com.wellsoft.pt.file.facade.FileManagerApiFacade;*/

/**
 * Description: 更新数据
 *
 * @author ruanhg
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-6-23.1	ruanhg		2014-6-23		Create
 * </pre>
 * @date 2014-6-23
 */
@Service
@Transactional
public class ExchangeDataUpdateServiceImpl extends BaseServiceImpl implements
        ExchangeDataUpdateService {
    // Logger logger = Logger.getLogger(ExchangeDataUpdateServiceImpl.class);
    @Autowired
    private ExchangeDataService exchangeDataService;

    @Autowired
    private ExchangeDataClientService exchangeDataClientService;
    @Autowired
    private ExchangeDataFlowService exchangeDataFlowService;
    @Autowired
    private DyFormFacade dyFormApiFacade;
    /*
     * @Autowired private FileManagerApiFacade fileManagerApiFacade;
     */
    @Autowired
    private DataOperationLogService dataOperationLogService;
    @Autowired
    private UniversalDao synchronousDao;
    @Autowired
    private ExchangeDataSynchronousService exchangeDataSynchronousService;
    @Autowired
    private ExchangeDataMonitorService exchangeDataMonitorService;
    @Autowired
    private ExchangeDataSendMonitorService exchangeDataSendMonitorService;

    public static List<Map<String, Object>> executeQuery(String sql, Connection conn) {
        // Connection conn = null;

        Statement st = null;

        ResultSet set = null;
        try {

            // conn = getConnection();

            st = conn.createStatement();

            set = st.executeQuery(sql);

            ResultSetMetaData metaData = set.getMetaData();

            List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

            int columnCount = metaData.getColumnCount();

            while (set.next()) {

                Map<String, Object> map = new HashMap<String, Object>();

                for (int i = 1; i <= columnCount; i++) {

                    String name = metaData.getColumnName(i);

                    Object value = set.getObject(name);

                    map.put(name, value);

                }

                result.add(map);

            }

            return result;

        } catch (SQLException e) {

            throw new IllegalArgumentException(e);

        } finally {
            if (set != null) {

                try {
                    set.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block

                }
            }
            if (st != null) {

                try {
                    st.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    System.out.println(e.getMessage());
                }
            }
            if (conn != null) {

                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    System.out.println(e.getMessage());
                }
            }

        }

    }

    /**
     * @return
     */
    public static Connection getConnection() {
        String driver = "oracle.jdbc.driver.OracleDriver";
        String url = "jdbc:oracle:thin:@10.26.8.10:1521:ORCL";
        String username = "XZSP_01";
        String password = "XZSP_01";
        Connection conn = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataUpdateService#deleteDataByDataId(java.lang.String)
     */
    @Override
    public void deleteDataByDataId(String dataId) {
        // TODO Auto-generated method stub
        long startTime = System.currentTimeMillis();
        String dataSql = "select d.uuid,d.batch_id as buuid from is_exchange_data d where d.data_id='" + dataId + "'";
        List<Map<String, Object>> dataList = exchangeDataClientService.selectQueryItemDataBySql(
                dataSql);
        for (Map<String, Object> data : dataList) {
            String duuid = data.get("uuid").toString();
            // String buuid = data.get("buuid").toString();
            String sendSql = "select s.uuid as uuid from is_exchange_send_monitor s where s.data_uuid = '" + duuid
                    + "'";
            List<Map<String, Object>> sendList = exchangeDataClientService.selectQueryItemDataBySql(
                    sendSql);
            for (Map<String, Object> send : sendList) {
                String sUuid = send.get("uuid").toString();
                exchangeDataMonitorService.deleteBySendId(sUuid);
                exchangeDataSendMonitorService.delete(sUuid);
            }
            String deldataSql = "delete is_exchange_data d where d.uuid='" + duuid + "'";
            exchangeDataService.delete(duuid);
            // String delbatchSql =
            // "delete is_exchange_data_batch b where b.uuid='" + buuid + "'";
            // exchangeDataDao.getSession().createSQLQuery(delbatchSql).executeUpdate();
        }
        long endTime1 = System.currentTimeMillis();
        System.out.println(dataId + "执行时间: " + (endTime1 - startTime) + " ms");
    }

    @Override
    public List<ExchangeData> getExchangeDataByFileSubFormData() {
        // TODO Auto-generated method stub
        String hql = "from ExchangeData e where e.exchangeDataBatch.typeId=:typeId and e.exchangeDataBatch.fromId=:fromId and e.createTime between :beginTime and :endTime and e.validData=:validData";
        Map<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put("typeId", "004140203SZ");
        valueMap.put("fromId", "004140203");
        valueMap.put("validData", "no");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// 小写的mm表示的是分钟
        try {
            Date beginTime = sdf.parse("2014-8-6");
            Date endTime = sdf.parse("2014-8-12");
            valueMap.put("beginTime", beginTime);
            valueMap.put("endTime", endTime);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            logger.info(e.getMessage());
        }
        return exchangeDataService.listByHQL(hql, valueMap);
    }

    @Override
    public void xmlUpdateSubFormDate(String xml, String mainDataUuid, String mainFormUuid) {
        xml = xml.replaceAll("userform_", "uf_");
        DyFormData dyformdata = this.dyFormApiFacade.getDyFormData(mainFormUuid, mainDataUuid);
        // TODO Auto-generated method stub
        try {
            // TODO Auto-generated method stub
            DyFormFormDefinition dyFormDefinition = dyFormApiFacade.getFormDefinition(mainFormUuid);
            Document document = DocumentHelper.parseText(xml);
            // 获得根节点
            Element root = document.getRootElement();
            // 从XML的根结点开始遍历
            for (Iterator i = root.elementIterator(); i.hasNext(); ) {
                Element elementOneLevel = (Element) i.next();
                String isList = elementOneLevel.attributeValue("isList");
                if (isList != null && isList.equals("1")) {// 从表
                    String subFormName = elementOneLevel.getName();// 当前节点从表名
                    List<DyformSubformFormDefinition> subformDefinitions = dyFormDefinition.doGetSubformDefinitions();
                    for (DyformSubformFormDefinition subformDefinition : subformDefinitions) {
                        if (subformDefinition.getName().equals(subFormName)) {
                            List tempList = dyformdata.getFormDatas(
                                    subformDefinition.getFormUuid());
                            if (tempList == null || (tempList != null && tempList.size() == 0)) {// 判断是否有从表数据，没有时插入
                                // 该种表单的行数据遍历
                                List subLineNodes = elementOneLevel.elements("item");
                                for (Iterator it = subLineNodes.iterator(); it.hasNext(); ) {
                                    String newDataUuid = UuidUtils.createUuid();
                                    DyFormData subformData = dyformdata.getDyFormDataByFormId(
                                            subFormName, newDataUuid);
                                    subformData.setFieldValue(IdEntity.UUID, newDataUuid);
                                    // 一行数据的列遍历
                                    List subFieldNodes = ((Element) it.next()).elements();
                                    for (Iterator it2 = subFieldNodes.iterator(); it2.hasNext(); ) {
                                        Element elm2 = (Element) it2.next();
                                        subformData.setFieldValue(elm2.getName(),
                                                elm2.getTextTrim());
                                    }
                                }
                            }
                        }
                    }
                }
            }
            this.dyFormApiFacade.saveFormData(dyformdata);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void moniCallbackClientAndreceiveClient(String dataUuid) {
        // TODO Auto-generated method stub
        ExchangeData ed = exchangeDataService.getOne(dataUuid);
        ExchangeDataBatch edbTemp = ed.getExchangeDataBatch();
        WebServiceMessage msg = new WebServiceMessage();
        msg.setWay("callbackClientAndreceiveClient");
        msg.setBatchId(edbTemp.getId());
        msg.setUnitId(edbTemp.getFromId());// 发件人
        msg.setTypeId(edbTemp.getTypeId());
        msg.setCc(edbTemp.getCc());
        msg.setBcc(edbTemp.getBcc());
        msg.setToId(edbTemp.getToId());
        exchangeDataFlowService.callbackClientAndreceiveClient(msg);
    }

    /**
     * 导入旧建管系统项目
     *
     * @return
     */
    public void importproject(Integer begin, Integer end) {
    }

    @Override
    public void test1() {
        // TODO Auto-generated method stub
        try {
            List<DataOperationLog> list = dataOperationLogService.listAll();
            for (DataOperationLog d : list) {
                String content = IOUtils.toString(
                        d.getContent().getCharacterStream()) == null ? "" : IOUtils.toString(
                        d.getContent().getCharacterStream()).toString();
                Object obj = exchangeDataSynchronousService.deserializationToObject(content);
                // obj = exchangeDataSynchronousService.modifyEntityCreator(obj,
                // d.getStableName());
                // synchronousDao.save(obj);
                // this.dao.save((IdEntity) obj);
                exchangeDataSynchronousService.saveEntityObj(obj, d.getStableName());
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.info(e.getMessage());
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            logger.info(e.getMessage());
        }
    }

    @Override
    public void test2() {
        // TODO Auto-generated method stub
        try {
            List<DataOperationLog> list = dataOperationLogService.listAll();
            for (DataOperationLog d : list) {
                exchangeDataSynchronousService.backData(d, null);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.info(e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> queryList(String sql) {
        // TODO Auto-generated method stub
        Session session = this.dao.getSession();
        return session.createSQLQuery(sql).setResultTransformer(
                ExchangeDataResultTransformer.INSTANCE).list();
    }

    @Override
    public void executeUpdate(String sql) {
        // TODO Auto-generated method stub
        Session session = this.dao.getSession();
        session.createSQLQuery(sql).executeUpdate();
    }

    @Override
    public String saveYhq(Map<String, Object> dataItem) {
        // TODO Auto-generated method stub
        String insertSql = "insert into uf_tg_test "
                + "(UUID,SPID,SPMC,SPZT,SPXQYLJDZ,SPYJLM,TBKLJ,SPJG,SPYXL,SRBL,YJ,MJWW,MJID,DPMC,PTLX,YHQID,YHQZL,YHQSYL,YHQME,YHQKSSJ,YHQJSSJ,YHQLJ,SPYHQTGLJ) "
                + "values "
                + "(:UUID,:SPID,:SPMC,:SPZT,:SPXQYLJDZ,:SPYJLM,:TBKLJ,:SPJG,:SPYXL,:SRBL,:YJ,:MJWW,:MJID,:DPMC,:PTLX,:YHQID,:YHQZL,:YHQSYL,:YHQME,:YHQKSSJ,:YHQJSSJ,:YHQLJ,:SPYHQTGLJ)";
        SQLQuery sqlquery = this.dao.getSession().createSQLQuery(insertSql);
        sqlquery.setString("UUID", UUID.randomUUID().toString());
        for (String key : dataItem.keySet()) {
            if (dataItem.get(key) != null) {
                sqlquery.setString(key.toUpperCase(), dataItem.get(key).toString());
            } else {
                sqlquery.setString(key.toUpperCase(), "");
            }
        }
        sqlquery.executeUpdate();
        return "success";
    }

}
