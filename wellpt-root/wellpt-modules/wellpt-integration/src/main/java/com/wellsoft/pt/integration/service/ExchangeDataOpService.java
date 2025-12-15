package com.wellsoft.pt.integration.service;

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
public interface ExchangeDataOpService {

    public Boolean backData(String serializeStr);

    public String serializeEntityToString(Object obj);

}
