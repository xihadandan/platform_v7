package com.wellsoft.pt.integration.dao.impl;

import com.wellsoft.pt.integration.dao.DataOperationLogDao;
import com.wellsoft.pt.integration.entity.DataOperationLog;
import com.wellsoft.pt.integration.support.ExchangeDataResultTransformer;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author ruanhg
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-8-5.1	ruanhg		2014-8-5		Create
 * </pre>
 * @date 2014-8-5
 */
@Repository
public class DataOperationLogDaoImpl extends AbstractJpaDaoImpl<DataOperationLog, String> implements
        DataOperationLogDao {

    public List<DataOperationLog> getDataOperationLogSynchronous() {
        String hql = "from DataOperationLog d where d.status =:status and d.";
        // Map<String, Object> values = new HashMap<String, Object>();
        // values.put("typeId", typeId);
        // return super.find(hql, values);
        return null;
    }

    public Boolean saveDataOperationLogOfNotEntity(String tableName, String uuid, Integer action) {
        String selectSql = "select * from " + tableName + " t where t.uuid='" + uuid + "'";
        SQLQuery sqlquery = this.getSession().createSQLQuery(selectSql);
        List<Map<String, Object>> dataList = sqlquery.setResultTransformer(ExchangeDataResultTransformer.INSTANCE)
                .list();
        if (dataList.size() > 0) {
            Map<String, Object> map = dataList.get(0);
            DataOperationLog dataOperationLog = new DataOperationLog();
            dataOperationLog.setStableType(2);
            dataOperationLog.setStableName(tableName);
            dataOperationLog.setSuuid(uuid);
            dataOperationLog.setSrecVer(Integer.parseInt(map.get("rec_ver").toString()));
            dataOperationLog.setScreator(map.get("creator").toString());
            dataOperationLog.setScreateTime((Date) map.get("create_time"));
            dataOperationLog.setSmodifier(map.get("modifier").toString());
            dataOperationLog.setSmodifyTime((Date) map.get("modify_time"));
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream;
                objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(map);
                String serStr = byteArrayOutputStream.toString("ISO-8859-1");
                serStr = java.net.URLEncoder.encode(serStr, "UTF-8");
                objectOutputStream.close();
                byteArrayOutputStream.close();
                dataOperationLog.setContent(Hibernate.getLobCreator(this.getSession()).createClob(serStr));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                LOGGER.info(e.getMessage());
            }
            dataOperationLog.setStatus(1);
            String sql = "select s.pro_value from is_sys_properties s where s.pro_en_name='xt_wl'";
            List<Map<String, Object>> list = this.getSession().createSQLQuery(sql)
                    .setResultTransformer(ExchangeDataResultTransformer.INSTANCE).list();
            dataOperationLog.setDirection(list.get(0).get("pro_value").equals("in") ? 1 : 2);// 内/外
            dataOperationLog.setAction(action);
            Calendar calendar = Calendar.getInstance();
            if (action == 3) {
                dataOperationLog.setModifier(SpringSecurityUtils.getCurrentUserId());
                dataOperationLog.setCreateTime(calendar.getTime());
                dataOperationLog.setModifyTime(calendar.getTime());
                dataOperationLog.setCreator(SpringSecurityUtils.getCurrentUserId());
            } else {
                dataOperationLog.setModifyTime(calendar.getTime());
                dataOperationLog.setModifier(SpringSecurityUtils.getCurrentUserId());
            }

            this.save(dataOperationLog);
        }
        return true;
    }
}
