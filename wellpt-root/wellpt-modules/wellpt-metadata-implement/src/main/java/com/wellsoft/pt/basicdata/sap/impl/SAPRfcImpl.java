package com.wellsoft.pt.basicdata.sap.impl;

import com.sap.mw.jco.IFunctionTemplate;
import com.sap.mw.jco.JCO;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.sap.config.SAPDbConfig;
import com.wellsoft.pt.basicdata.sap.service.SAPRfcService;
import com.wellsoft.pt.basicdata.sap.util.SAPRfcJson;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * sapRFC函数实现类
 * Description: 如何描述该类
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-7-28.1  zhengky	2014-7-28	  Create
 * </pre>
 * @date 2014-7-28
 */

@Service
@Transactional
public class SAPRfcImpl implements SAPRfcService {

    private static final String CONFIGBEANID = "sapConnectConfig";
    private Map<String, SAPDbConfig> dbconfigMap;
    private java.text.SimpleDateFormat DF_DATE = new java.text.SimpleDateFormat("yyyy-MM-dd");

    private java.text.SimpleDateFormat DF_TIME = new java.text.SimpleDateFormat("HH:mm:ss");

    private Logger logger = LoggerFactory.getLogger(SAPRfcImpl.class);

    @Override
    public SAPRfcJson RFC(String SAPS, String functionName, String paraJSON, int start, int count, Object callBack) {
        long time1 = System.currentTimeMillis();
        SAPDbConfig config = new SAPDbConfig();
        //初始化连接
        if (StringUtils.isEmpty(SAPS)) {
            config = getDbConfig(CONFIGBEANID);
        } else {
            config = getDbConfig(SAPS);
        }

        JCO.Client mConnection = initConnection(config);

        mConnection.connect();
        JCO.Repository mRepository = new JCO.Repository("well-soft", mConnection);

        //设置input参数
        JCO.Function function = null;
        try {
            function = this.createFunction(functionName, mRepository);
            if (function == null) {
                System.out.println(functionName + " not found in SAP.");
            }

            JCO.ParameterList input = function.getImportParameterList();
            JCO.ParameterList output = function.getExportParameterList();
            JCO.ParameterList tables = function.getTableParameterList();
            JCO.Field field = null;
            JCO.Structure structure = null;

            if (input != null)
                input.clear();
            if (output != null)
                output.clear();
            if (tables != null)
                tables.clear();

            //解析input参数，设置参数的值
            JSONObject inputobjObject = JSONObject.fromObject(paraJSON);
            for (int j = 0; input != null && j < input.getFieldCount(); j++) {
                field = input.getField(j);
                if (inputobjObject.get(field.getName()) == null) {
                    continue;
                }
                if (field.isStructure()) {
                    structure = field.getStructure();
                    JSONObject params = inputobjObject.getJSONObject(field.getName());
                    Iterator it = params.keys();
                    while (it.hasNext()) {
                        String key = (String) it.next();
                        String value = params.getString(key);
                        structure.setValue(value, key);
                    }

                } else if (field.isTable()) {
                    if (inputobjObject.get(field.getName()) == null) {
                        continue;
                    }
                    setTableValue(inputobjObject, field);

                } else {
                    input.setValue(inputobjObject.getString(field.getName()), field.getName());
                }
            }
            //解析table参数，设置table参数值
            //input table
            for (int j = 0; tables != null && j < tables.getFieldCount(); j++) {
                JCO.Field tbfield = tables.getField(j);
                if (inputobjObject.get(tbfield.getName()) == null) {
                    continue;
                }
                setTableValue(inputobjObject, tbfield);
            }

            //函数执行
            boolean status = false;
            String msg = "";
            try {
                if (mConnection != null) {
                    mConnection.execute(function);
                    status = true;
                }
            } catch (Exception t) {
                logger.error(ExceptionUtils.getStackTrace(t));

                mConnection.disconnect();
                msg = t.toString();
                status = false;
            }

            //返回output参数
            //获得返回值
            JSONArray starrays = new JSONArray();
            JSONArray tbarrays = new JSONArray();
            //取单一参数
            JSONObject outputjson = new JSONObject();

            //执行成功再取
            if (status) {
                for (int j = 0; output != null && j < output.getFieldCount(); j++) {
                    field = output.getField(j);
                    if (field.isStructure()) {
                        structure = field.getStructure();
                        JSONObject stobj = new JSONObject();
                        for (int k = 0; k < structure.getFieldCount(); k++) {
                            JCO.Field stfield = structure.getField(k);
                            stobj.put(stfield.getName(), getFieldValue(stfield));
                        }
                        starrays.add(field.getName());
                        outputjson.put(field.getName(), stobj);
                    } else if (field.isTable()) {
                        generalTableOutPutJson(start, count, field, outputjson);
                        tbarrays.add(field.getName());
                    } else {
                        outputjson.put(field.getName(), getFieldValue(field));
                    }
                }
                //output table
                for (int j = 0; tables != null && j < tables.getFieldCount(); j++) {
                    JCO.Field tbfield = tables.getField(j);
                    generalTableOutPutJson(start, count, tbfield, outputjson);
                    tbarrays.add(tbfield.getName());
                }
                outputjson.put("_structureName_", starrays);
                outputjson.put("_recordName_", tbarrays);
            }
            //msg
            outputjson.put("_msg_", msg);
            //codes
            if (status) {
                outputjson.put("_code_", 1);
            } else {
                outputjson.put("_code_", 0);
            }
            mConnection.disconnect();

            long time2 = System.currentTimeMillis();
            logger.info("RFC 【" + functionName + "】 spent " + (time2 - time1) / 1000.0 + "s");
            logger.info("RFC 【" + functionName + "】 outputjson", outputjson.toString());
            return new SAPRfcJson(outputjson.toString());
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));

            mConnection.disconnect();
        }
        return null;
    }

    /**
     * 初始化连接
     *
     * @param config
     */
    private JCO.Client initConnection(SAPDbConfig config) {
		/*	config = new SAPDbConfig();
			config.setClient("500");
			config.setHostname("192.168.0.102");
			config.setLanguage("");
			config.setPassword("localpsd");
			config.setSysnumber("00");
			config.setUserid("sys_rfc");*/

        try {
            // Change the logon information to your own system/user
            return JCO.createClient(config.getClient(), // SAP client
                    config.getUserid(), // userid
                    config.getPassword(), // password
                    config.getLanguage(), // language
                    config.getHostname(), // host name
                    config.getSysnumber()); // system number

        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
        }
        return null;
    }

    /**
     * 根据json跟field设置table的值.
     *
     * @param inputobjObject
     * @param tbfield
     */
    private void setTableValue(JSONObject inputobjObject, JCO.Field tbfield) {
        JCO.Table table = tbfield.getTable();
        JSONObject tbobj = inputobjObject.getJSONObject(tbfield.getName());
        JSONArray rowarrays = tbobj.getJSONArray("row");
        //遍历array行
        for (int k = 0; k < rowarrays.size(); k++) {
            table.appendRow();
            JSONObject column = rowarrays.getJSONObject(k);
            Iterator it = column.keys();
            while (it.hasNext()) {
                String key = (String) it.next();
                String value = column.getString(key);
                table.setValue(value, key);
            }
        }

    }

    /**
     * 创建rfc函数.
     *
     * @param name
     * @return
     * @throws Exception
     */
    private JCO.Function createFunction(String name, JCO.Repository mRepository) throws Exception {
        try {
            IFunctionTemplate ft = mRepository.getFunctionTemplate(name.toUpperCase());
            if (ft == null)
                return null;
            return ft.getFunction();
        } catch (Exception ex) {
            throw new Exception(ex);
        }
    }

    /**
     * 获得输出table的json数据
     *
     * @param start
     * @param count
     * @param field
     * @param outputjson
     */
    private void generalTableOutPutJson(int start, int count, JCO.Field field, JSONObject outputjson) {
        JCO.Table table;
        JSONArray totalrowarrary = new JSONArray();
        JSONObject jsontb = new JSONObject();
        table = field.getTable();
        jsontb.put("start", (new Integer(start)).toString());
        jsontb.put("count", (new Integer(count)).toString());
        jsontb.put("rows", (new Integer(table.getNumRows())).toString());
        for (int k = 0; k < table.getNumRows(); k++) {
            if (count == -1 || (start + count > k + 1 && k + 1 >= start)) {
                table.setRow(k);
                JSONObject rowobj = new JSONObject();
                for (int l = 0; l < table.getFieldCount(); l++) {
                    JCO.Field rowfield = table.getField(l);
                    rowobj.put(rowfield.getName(), getFieldValue(rowfield));
                }
                totalrowarrary.add(k, rowobj);
            }
        }
        jsontb.put("row", totalrowarrary);
        outputjson.put(field.getName(), jsontb);
    }

    /**
     * 通过configbeanid获得配置
     * 如何描述该方法
     *
     * @param configid
     * @return
     */
    private SAPDbConfig getDbConfig(String configid) {
        if (this.dbconfigMap == null)
            this.dbconfigMap = new HashMap();
        if (!this.dbconfigMap.containsKey(configid)) {
            SAPDbConfig dbconfig = (SAPDbConfig) ApplicationContextHolder.getBean(configid, SAPDbConfig.class);
            this.dbconfigMap.put(configid, dbconfig);
        }
        return (SAPDbConfig) this.dbconfigMap.get(configid);
    }

    /**
     * 如何描述该方法
     *
     * @return
     */
    private String getFieldValue(JCO.Field field) {
        Object fieldvalue = field.getValue();
        String returnvalue;
        if (fieldvalue == null) {
            returnvalue = "";
        } else if (field.getType() == JCO.TYPE_DATE) {
            returnvalue = DF_DATE.format(field.getDate());
        } else if (field.getType() == JCO.TYPE_TIME) {
            returnvalue = DF_TIME.format(field.getTime());
        } else {
            returnvalue = field.getString();
        }
        return returnvalue;
    }

}
