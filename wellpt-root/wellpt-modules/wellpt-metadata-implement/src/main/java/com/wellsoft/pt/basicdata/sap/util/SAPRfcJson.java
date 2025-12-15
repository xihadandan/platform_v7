package com.wellsoft.pt.basicdata.sap.util;

import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

/**
 * json参数工具类
 * Description: 如何描述该类
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-7-29.1  zhengky	2014-7-29	  Create
 * </pre>
 * @date 2014-7-29
 */
public class SAPRfcJson extends Object {

    public JSONObject rfcjsonobject;

    public SAPRfcJson() {
        this.rfcjsonobject = new JSONObject();
    }

    public SAPRfcJson(String jsonstr) {
        if (!StringUtils.isEmpty(jsonstr)) {
            this.rfcjsonobject = JSONObject.fromObject(jsonstr);
        } else {
            this.rfcjsonobject = new JSONObject();
        }
    }

    public JSONObject getRfcjsonobject() {
        return rfcjsonobject;
    }

    public void setRfcjsonobject(JSONObject rfcjsonobject) {
        this.rfcjsonobject = rfcjsonobject;
    }

    public void setField(String name, String value) {
        rfcjsonobject.put(name, value);
    }

    public void setStructure(String name, String jsonData) {
        rfcjsonobject.put(name, jsonData);
    }

    public void setRecord(String name, String jsonData) {
        rfcjsonobject.put(name, jsonData);
    }

    public String getFieldValue(String name) {
        return (String) rfcjsonobject.get(name);
    }

    public JSONObject getStructure(String name) {
        return rfcjsonobject.getJSONObject(name);
    }

    public JSONObject getRecord(String name) {
        return rfcjsonobject.getJSONObject(name);
    }

    public String getRfcJson() {
        return rfcjsonobject.toString();
    }

}
