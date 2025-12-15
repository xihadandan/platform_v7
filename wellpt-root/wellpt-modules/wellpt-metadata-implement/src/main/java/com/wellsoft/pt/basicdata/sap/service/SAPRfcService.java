package com.wellsoft.pt.basicdata.sap.service;

import com.wellsoft.pt.basicdata.sap.util.SAPRfcJson;

/**
 * RFC函数调用接口
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
public interface SAPRfcService {

    /**
     * 如何描述该方法
     *
     * @param SAPS         连接定义beanid
     * @param functionName 函数名
     * @param paraJSON     参数json
     * @param start
     * @param count
     * @param callBack     回调函数
     * @return SAPRfcJson
     */
    public SAPRfcJson RFC(String SAPS, String functionName, String paraJSON, int start, int count, Object callBack);

}
