package com.wellsoft.pt.integration.service.impl;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.integration.security.ExchangeConfig;
import com.wellsoft.pt.integration.service.ExchangeDataMonitorService;
import com.wellsoft.pt.integration.service.ExchangeDataService;
import com.wellsoft.pt.integration.service.ExchangeDataStatisticsService;
import com.wellsoft.pt.integration.support.DateUtil;
import com.wellsoft.pt.org.entity.User;
import com.wellsoft.pt.org.service.UserLoginLogService;
import com.wellsoft.pt.org.service.UserService;
import com.wellsoft.pt.unit.entity.CommonUnit;
import com.wellsoft.pt.unit.facade.service.UnitApiFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Description: 数据统计服务层实现类
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
@Service
public class ExchangeDataStatisticsServiceImpl implements ExchangeDataStatisticsService {
    @Autowired
    private ExchangeDataService exchangeDataService;
    @Autowired
    private UserService userDao;
    @Autowired
    private UserLoginLogService userLoginLogDao;
    @Autowired
    private UnitApiFacade unitApiFacade;
    @Autowired
    private ExchangeDataMonitorService exchangeDataMonitorService;

    /**
     * 根据typeId获取所有商事登记数量
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataStatisticsService#getAllCommercialRegistrationByTypeId(java.lang.String)
     */
    @Override
    public long getAllCommercialRegistrationByTypeId(String typeId) {
        long exchangeDataNumber = exchangeDataService.getAllCommercialSubjectByTypeId(typeId);
        return exchangeDataNumber;
    }

    /**
     * 根据typeId获取本月商事登记数量
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataStatisticsService#getCurrentMonthCommercialRegistrationByTypeId(java.lang.String)
     */
    @Override
    public long getCurrentMonthCommercialRegistrationByTypeId(String typeId) {
        Date firstDay = DateUtil.getFirstDayOfMonth();// 本月第一天
        Date lastDay = DateUtil.getLastDayOfMonth();// 本月最后一天
        long exchangeDataNumber = exchangeDataService.getCurrentMonthCommercialRegistrationByTypeId(typeId, firstDay,
                lastDay);
        return exchangeDataNumber;
    }

    /**
     * 传入已展示单位，获取未展示单位
     *
     * @return
     */
    public List<CommonUnit> getNotShowUnit(List<String> showUnitList) {
        List<CommonUnit> CommonUnitList = unitApiFacade
                .getCommonUnitsByBusinessTypeId(ExchangeConfig.EXCHANGE_BUSINESS_TYPE);
        Iterator<CommonUnit> commonUnitListIter = CommonUnitList.iterator();
        while (commonUnitListIter.hasNext()) {
            CommonUnit comm = commonUnitListIter.next();
            if (showUnitList.contains(comm.getId())) {
                commonUnitListIter.remove();// 这里要使用Iterator的remove方法移除当前对象，如果使用List的remove方法，则同样会出现ConcurrentModificationException
            }
        }
        return CommonUnitList;
    }

    /**
     * 上传逾期汇总表
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataStatisticsService#getSBYQStatistics()
     */
    @Override
    public List<QueryItem> getSBYQStatistics() {
        List<QueryItem> sbyqUnitIdList = exchangeDataService.getSBYQ();// 所有上报逾期（单位id,逾期数量）
        List<QueryItem> sbyqUnitNameList = new ArrayList<QueryItem>();// 所有上报逾期（单位名称，逾期数量）
        List<String> sbyqUnitIdList1 = new ArrayList<String>();// 所有上报逾期单位id集合
        List<QueryItem> notOverdueUnitNameList = new ArrayList<QueryItem>();// 所有未逾期（单位名称，逾期数量）

        for (QueryItem sbyq : sbyqUnitIdList) {
            CommonUnit unit = unitApiFacade.getCommonUnitById((String) sbyq.get("unit"));
            if (unit != null) {
                sbyqUnitIdList1.add(unit.getId());
                QueryItem unitNameQueryItem = new QueryItem();
                unitNameQueryItem.put(unit.getName(), sbyq.get("count1"));
                sbyqUnitNameList.add(unitNameQueryItem);
            }
        }
        List<CommonUnit> notOverdueUnitList = getNotShowUnit(sbyqUnitIdList1);
        for (CommonUnit notOverdueUnit : notOverdueUnitList) {
            QueryItem newMap = new QueryItem();
            newMap.put(notOverdueUnit.getName(), 0);
            notOverdueUnitNameList.add(newMap);
        }
        sbyqUnitNameList.removeAll(notOverdueUnitNameList);
        sbyqUnitNameList.addAll(notOverdueUnitNameList);
        return sbyqUnitNameList;
    }

    /**
     * 接收逾期汇总表
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataStatisticsService#getJsyqStatistics()
     */
    @Override
    public List<QueryItem> getJsyqStatistics() {
        List<QueryItem> jsyqUnitIdList = exchangeDataMonitorService.getJSYQ();// 所有接收逾期（单位id,逾期数量）
        List<QueryItem> jsyqUnitNameList = new ArrayList<QueryItem>();// 所有接收逾期（单位名称，逾期数量）
        List<String> jsyqUnitIdList1 = new ArrayList<String>();// 所有接收逾期单位id集合
        List<QueryItem> notOverdueUnitNameList = new ArrayList<QueryItem>();// 所有未逾期（单位名称，逾期数量）

        for (QueryItem jsyq : jsyqUnitIdList) {
            CommonUnit unit = unitApiFacade.getCommonUnitById((String) jsyq.get("unit"));
            if (unit != null) {
                jsyqUnitIdList1.add(unit.getId());
                QueryItem unitNameQueryItem = new QueryItem();
                unitNameQueryItem.put(unit.getName(), jsyq.get("count1"));
                jsyqUnitNameList.add(unitNameQueryItem);
            }
        }
        List<CommonUnit> notOverdueUnitList = getNotShowUnit(jsyqUnitIdList1);
        for (CommonUnit notOverdueUnit : notOverdueUnitList) {
            QueryItem newMap = new QueryItem();
            newMap.put(notOverdueUnit.getName(), 0);
            notOverdueUnitNameList.add(newMap);
        }
        jsyqUnitNameList.removeAll(notOverdueUnitNameList);
        jsyqUnitNameList.addAll(notOverdueUnitNameList);
        return jsyqUnitNameList;
    }

    /**
     * 部门许可数量汇总表
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataStatisticsService#getBmxkslhzStatistics()
     */
    @Override
    public List<QueryItem> getBmxkslhzStatistics() {
        List<QueryItem> uploadBMXKUnitIdList = exchangeDataService.getBMXK();// 有上传许可的单位（单位id,许可数量）
        List<QueryItem> uploadBMXKUnitNameList = new ArrayList<QueryItem>();// 有上传许可的单位（单位名称，逾期数量）
        List<String> uploadBMXKUnitIdList1 = new ArrayList<String>();// 有上传许可的单位id集合
        List<QueryItem> notUploadBMXKUnitNameList = new ArrayList<QueryItem>();// 无上传许可的单位（单位名称，许可数量）

        for (QueryItem bmxk : uploadBMXKUnitIdList) {
            CommonUnit unit = unitApiFacade.getCommonUnitById((String) bmxk.get("unit"));
            if (unit != null) {
                uploadBMXKUnitIdList1.add(unit.getId());
                QueryItem unitNameQueryItem = new QueryItem();
                unitNameQueryItem.put(unit.getName(), bmxk.get("count1"));
                uploadBMXKUnitNameList.add(unitNameQueryItem);
            }
        }
        List<CommonUnit> notUploadBMXKUnitList = getNotShowUnit(uploadBMXKUnitIdList1);
        for (CommonUnit notUploadBMXKUnit : notUploadBMXKUnitList) {
            QueryItem newMap = new QueryItem();
            newMap.put(notUploadBMXKUnit.getName(), 0);
            notUploadBMXKUnitNameList.add(newMap);
        }
        uploadBMXKUnitNameList.removeAll(notUploadBMXKUnitNameList);
        uploadBMXKUnitNameList.addAll(notUploadBMXKUnitNameList);
        return uploadBMXKUnitNameList;
    }

    /**
     * 获得从未登录的单位
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataStatisticsService#getNeverLoginUnit()
     */
    @Override
    public List<Map<String, Object>> getNeverLoginUnit() {
        // TODO Auto-generated method stub
        // 获得所有单位
        List<CommonUnit> allUnits = unitApiFacade.getCommonUnitsByBusinessTypeId(ExchangeConfig.EXCHANGE_BUSINESS_TYPE);
        // 登录过的单位
        List<CommonUnit> loginUnits = new ArrayList<CommonUnit>();
        String unitIds = "";// 用于保证单位唯一
        String hql = "select u.userUuid from UserLoginLog u group by u.userUuid";
        List<String> userLoginLogs = userLoginLogDao.find(hql, new HashMap());
        for (String u : userLoginLogs) {
            User user = userDao.get(u);
            if (user != null) {
                List<CommonUnit> commonUnits = unitApiFacade.getCommonUnitsByBusinessTypeIdAndUserId(
                        ExchangeConfig.EXCHANGE_BUSINESS_TYPE, user.getId());
                for (CommonUnit c : commonUnits) {
                    if (unitIds.indexOf(c.getId()) < 0) {
                        if (c.getId().equals("00414209X")) {
                            System.out.print(user);
                        }
                        loginUnits.add(c);
                        unitIds += "," + c.getId();
                    }
                }
            }
        }
        // 过滤未登录过的单位
        List<Map<String, Object>> notLoginUnits = new ArrayList<Map<String, Object>>();
        for (CommonUnit unit : allUnits) {
            int flag = 0;// 假设单位没登录了
            for (CommonUnit unit1 : loginUnits) {
                if (unit.getUuid().equals(unit1.getUuid())) {
                    flag = 1;// 单位不在登录列表里即使未登录
                }
            }
            if (flag == 0) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("id", unit.getId());
                map.put("name", unit.getName());
                Long receiveCount = exchangeDataMonitorService.countByUnitId(unit.getId());
                map.put("receiveCount", receiveCount);
                notLoginUnits.add(map);
            }
        }
        return notLoginUnits;
    }
}
