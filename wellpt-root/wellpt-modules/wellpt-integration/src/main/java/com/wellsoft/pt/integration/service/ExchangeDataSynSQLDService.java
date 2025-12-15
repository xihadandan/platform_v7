package com.wellsoft.pt.integration.service;

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
 * 2014-12-8.1	ruanhg		2014-12-8		Create
 * </pre>
 * @date 2014-12-8
 */
public interface ExchangeDataSynSQLDService {

    public void synOutData() throws Exception;

    public void synInData(String unitId) throws Exception;

    public void backupData(String unitId) throws Exception;

    public void clearSynData() throws Exception;

    public void synCommontoTenant() throws Exception;

    public void trunkSynOutData() throws Exception;

    public void trunkSynInData(Map<String, List<Map<String, Object>>> dataMap) throws Exception;

    /**
     * 调用总线次数统计 （集美区--> 市级） 集美发送
     *
     * @throws Exception
     */
    public void triggerCountOut() throws Exception;

    /**
     * 调用总线次数统计 （集美区--> 市级） 市级接受
     *
     * @param dataMap
     * @throws Exception
     */
    public void triggerCountIn(Map<String, Long> dataMap) throws Exception;
}
