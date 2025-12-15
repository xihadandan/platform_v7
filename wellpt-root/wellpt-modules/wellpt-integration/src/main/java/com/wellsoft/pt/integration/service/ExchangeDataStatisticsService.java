package com.wellsoft.pt.integration.service;

import com.wellsoft.context.jdbc.support.QueryItem;

import java.util.List;
import java.util.Map;

/**
 * Description: 数据统计服务层接口
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-20.1	zhouyq		2013-12-20		Create
 * </pre>
 * @date 2013-12-20
 */
public interface ExchangeDataStatisticsService {

    /**
     * 根据typeId获取所有商事登记数量
     *
     * @param string
     * @return
     */
    public long getAllCommercialRegistrationByTypeId(String typeId);

    /**
     * 根据typeId获取本月商事登记数量
     *
     * @param string
     * @return
     */
    public long getCurrentMonthCommercialRegistrationByTypeId(String typeId);

    /**
     * 部门许可数量汇总表
     *
     * @return
     */
    public List<QueryItem> getBmxkslhzStatistics();

    /**
     * 上传逾期汇总表
     *
     * @return
     */
    public List<QueryItem> getSBYQStatistics();

    /**
     * 接收逾期汇总表
     *
     * @return
     */
    public List<QueryItem> getJsyqStatistics();

    /**
     * 获得从未登录的单位
     *
     * @return
     */
    public List<Map<String, Object>> getNeverLoginUnit();

}
