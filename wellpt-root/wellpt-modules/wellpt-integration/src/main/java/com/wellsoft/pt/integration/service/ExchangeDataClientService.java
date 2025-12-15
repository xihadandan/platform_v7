package com.wellsoft.pt.integration.service;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.integration.bean.ExchangeDataBean;
import com.wellsoft.pt.integration.bean.ExchangeDataDetailBean;
import com.wellsoft.pt.integration.entity.ExchangeData;
import com.wellsoft.pt.integration.entity.ExchangeDataMonitor;
import com.wellsoft.pt.integration.entity.ExchangeDataSendMonitor;
import com.wellsoft.pt.integration.entity.ExchangeDataType;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: 用户收发界面接口
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-17.1	wbx		2013-11-17		Create
 * </pre>
 * @date 2013-11-17
 */
public interface ExchangeDataClientService {

    public String saveAndSendData(ExchangeDataBean bean);

    public ExchangeData getExchangeDataByUUid(String uuid);

    public Map getExchangeObjByDataUUid(String uuid);

    public String signData(String uuid);

    public String refuseData(String uuid, String msg, String type);

    public String cancel(Integer rel, String dataId, Integer recVer, String unitIds, String msg, String fromId,
                         String matterIds);

    public String reapply(Integer rel, String sendMonitorUuid, String unitIds);

    public ExchangeDataDetailBean getSendDetailByExDataUuid(String uuid);

    public Map getSendDataValue(String dataId, Integer recVer);

    public ExchangeDataMonitor getExchangeDataMonitor(String EchangeDataUuid, String unitId);

    public ExchangeDataType getExchangeTypeByDataUuid(String uuid);

    public ExchangeDataType getExchangeDataTypeById(String id);

    public void uploadFile(List<Map<String, Object>> files);

    public File generateZipFile(Collection<String> uuids) throws Exception;

    public String reapeat(Integer rel, String uuid, String unitIds);

    public List getSSDJBDetailDate(String zch);

    public String getSSZTName(String zch);

    public ExchangeDataMonitor getExchangeDataMonitorByCorrelationandUnitId(String correlationId,
                                                                            String correlationRecVer, String unitId);

    public List<Map> getFormDataBySql(String tableName, String whereHql);

    public List getXZXKGCDetail(String ywlsh, String uuid);

    public ExchangeDataDetailBean getReceiveDetailByExDataUuid(String uuid);

    public Map getSendDataValueByUuid(String uuid);

    public ExchangeData toZZDJXX(String zch);

    public Map<String, String> getFJSpecial(String sendMonitorUuid);

    public ExchangeDataSendMonitor getFromEdsmByEdUuid(String exchangeDataUuid);

    public String getSendMonitorByZch(String zch);

    public List<QueryItem> queryExchangeQueryItemData(String hql, Map<String, Object> values, PagingInfo pagingInfo);

    public List<Map<String, Object>> selectQueryItemDataBySql(String hql);

    public Boolean saveQueryItem(String tableName, Map<String, Object> params);

    public Long queryExchangeQueryItemCount(String hql, Map<String, Object> values);

    public Boolean delDataLog(String name, String dataUuid);

}
