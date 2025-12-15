package com.wellsoft.pt.basicdata.sap.test;

import com.wellsoft.pt.basicdata.sap.util.SAPRfcJson;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Iterator;

/**
 * input参数设置.
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
public class InputJsonCreate {

    public static void main(String[] args) {
        String kk = new InputJsonCreate().createInputJson();
        System.out.println(kk);
    }

    public String createInputJson() {

        SAPRfcJson util = new SAPRfcJson();
        util.setField("PI_MATNR", "abc");
        util.setField("PI_WERKS", "efg");
        /**
         * 三种参数形式json格式
         *
         *
         */

        //构造体参数
        JSONObject k1 = new JSONObject();
        k1.put("MATNR", "1");
        k1.put("MATNR1", "21");
        util.setStructure("PI_TAB", k1.toString());

        //表格json创建
        JSONObject tbobj = new JSONObject();
        //第一行
        JSONObject column1 = new JSONObject();
        column1.put("MATNR", "1");
        column1.put("WERKS", "2");
        JSONArray row1 = new JSONArray();
        row1.add(0, column1);

        JSONObject column11 = new JSONObject();
        column11.put("MATNR", "3");
        column11.put("WERKS", "4");
        row1.add(1, column11);

        tbobj.put("row", row1);

        util.setRecord("PT_TAB", tbobj.toString());

        String json = util.getRfcJson();

        JSONArray rowarrays = tbobj.getJSONArray("row");
        for (int k = 0; k < rowarrays.size(); k++) {
            JSONObject column = rowarrays.getJSONObject(k);
            Iterator it = column.keys();
            while (it.hasNext()) {
                String key = (String) it.next();
                String value = column.getString(key);
                System.out.println("column:" + key + "  value:" + value + "----------tb------------");
            }
        }

        return json;

    }

}
