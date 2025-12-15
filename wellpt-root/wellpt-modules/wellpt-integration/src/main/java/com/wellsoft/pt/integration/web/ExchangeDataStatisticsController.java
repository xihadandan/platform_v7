package com.wellsoft.pt.integration.web;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.integration.service.ExchangeDataStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * Description: 数据统计控制层
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-19.1	zhouyq		2013-12-19		Create
 * </pre>
 * @date 2013-12-19
 */
@Controller
@RequestMapping("/exchangedata/statistics")
public class ExchangeDataStatisticsController {
    @Autowired
    private ExchangeDataStatisticsService exchangeDataStatisticsService;

    /**
     * 商事登记汇总表
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/toSSDJStatistics")
    public String toSSDJStatistics(Model model) {
        long allCommercialSubject = exchangeDataStatisticsService.getAllCommercialRegistrationByTypeId("004140203SZ");//所有商事主体数量
        long allAdministrativeLicense = exchangeDataStatisticsService
                .getAllCommercialRegistrationByTypeId("000000000XK");//所有行政许可数量
        long currentMonthCommercialSubject = exchangeDataStatisticsService
                .getCurrentMonthCommercialRegistrationByTypeId("004140203SZ");//本月商事主体数量
        long currentMonthAdministrativeLicense = exchangeDataStatisticsService
                .getCurrentMonthCommercialRegistrationByTypeId("000000000XK");//本月行政许可数量

        model.addAttribute("allCommercialSubject", allCommercialSubject);//所有商事主体数量
        model.addAttribute("allAdministrativeLicense", allAdministrativeLicense);//所有行政许可数量
        model.addAttribute("currentMonthCommercialSubject", currentMonthCommercialSubject);//本月商事主体数量
        model.addAttribute("currentMonthAdministrativeLicense", currentMonthAdministrativeLicense);//本月行政许可数量

        return "pt/exchangedata/toSSDJStatistics";
    }

    /**
     * 部门许可数量汇总表
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/toBMXKSLHZStatistics")
    public String toBMXKSLHZStatistics(Model model) {
        List<QueryItem> bmxkslhzStatisticsMapList = exchangeDataStatisticsService.getBmxkslhzStatistics();//部门许可数量汇总
        model.addAttribute("bmxkslhzStatisticsMapList", bmxkslhzStatisticsMapList);
        return "pt/exchangedata/toBMXKSLHZStatistics";
    }

    /**
     * 上传逾期汇总表
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/toSBYQStatistics")
    public String toSBYQStatistics(Model model) {
        List<QueryItem> sbyqStatisticsMapList = exchangeDataStatisticsService.getSBYQStatistics();//上传逾期汇总
        model.addAttribute("sbyqStatisticsMapList", sbyqStatisticsMapList);
        return "pt/exchangedata/toSBYQStatistics";
    }

    /**
     * 接收逾期汇总表
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/toJSYQStatistics")
    public String toJSYQStatistics(Model model) {
        List<QueryItem> jsyqStatisticsMapList = exchangeDataStatisticsService.getJsyqStatistics();//接收逾期汇总
        model.addAttribute("jsyqStatisticsMapList", jsyqStatisticsMapList);
        return "pt/exchangedata/toJSYQStatistics";
    }

    /**
     * 上传汇总表（单位，上传总数，逾期数量）
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/toSCHZStatistics")
    public String toSCHZStatistics(Model model) {
        return "pt/exchangedata/toSCHZStatistics";
    }

    /**
     * 接收汇总表（单位，接收总数，逾期数量）
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/toJSHZStatistics")
    public String toJSHZStatistics(Model model) {
        return "pt/exchangedata/toJSHZStatistics";
    }

    /**
     * 上传汇总明细表（登记时间，上传时间，数据类型，上传单位，主体名称，事项名称）
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/toSCHZMXStatistics")
    public String toSCHZMXStatistics(Model model) {
        return "pt/exchangedata/toSCHZMXStatistics";
    }

    /**
     * 接收汇总明细表（登记时间，接收时间，数据类型，上传单位，主体名称，事项名称）
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/toJSHZMXStatistics")
    public String toJSHZMXStatistics(Model model) {
        return "pt/exchangedata/toJSHZMXStatistics";
    }

    /**
     * 工商上传许可事项明细(商事统计)
     *
     * @return
     */
    @RequestMapping(value = "/toGSXKMXStatistics")
    public String toGSXKMXStatistics() {
        return "pt/exchangedata/toGSXKMXStatistics";
    }

    /**
     * 单位许可事项(商事统计)
     *
     * @return
     */
    @RequestMapping(value = "/toDWXKStatistics")
    public String toDWXKStatistics() {
        return "pt/exchangedata/toDWXKStatistics";
    }

    /**
     * 待收件明细(商事统计)
     *
     * @return
     */
    @RequestMapping(value = "/toDSJMXStatistics")
    public String toDSJMXStatistics() {
        return "pt/exchangedata/toDSJMXStatistics";
    }

    /**
     * 待收件单位(商事统计)
     *
     * @return
     */
    @RequestMapping(value = "/toDSJDWStatistics")
    public String toDSJDWStatistics() {
        return "pt/exchangedata/toDSJDWStatistics";
    }

    /**
     * 上传数据情况(商事统计)
     *
     * @return
     */
    @RequestMapping(value = "/toSCSJQKStatistics")
    public String toSCSJQKStatistics() {
        return "pt/exchangedata/toSCSJQKStatistics";
    }

    /**
     * 从未登录平台的单位(商事统计)
     *
     * @return
     */
    @RequestMapping(value = "/toWDLDWStatistics")
    public String toWDLDWStatistics(Model model) {
        List<Map<String, Object>> notLoginCommonUnitList = exchangeDataStatisticsService.getNeverLoginUnit();
        model.addAttribute("notLoginCommonUnitList", notLoginCommonUnitList);
        model.addAttribute("size", notLoginCommonUnitList.size());
        return "pt/exchangedata/toWDLDWStatistics";
    }

    /**
     * 市工商从未推送过相关商事主体登记数据的单位(商事统计)
     *
     * @return
     */
    @RequestMapping(value = "/toWTSSSZTDWStatistics")
    public String toWTSSSZTDWStatistics() {
        return "pt/exchangedata/toWTSSSZTDWStatistics";
    }

    /**
     * 短信统计(商事统计)
     *
     * @return
     */
    @RequestMapping(value = "/toDXTJStatistics")
    public String toDXTJStatistics() {
        return "pt/exchangedata/toDXTJStatistics";
    }

    /**
     * 数据统计(商事统计)
     *
     * @return
     */
    @RequestMapping(value = "/toSJTJStatistics")
    public String toSJTJStatistics() {
        return "pt/exchangedata/toSJTJStatistics";
    }

    /**
     * 商事统计报表统一访问方法
     *
     * @return
     */
    @RequestMapping(value = "/toShangShiStatistics")
    public String toShangShiStatistics(@RequestParam(value = "reportletPath") String reportletPath,
                                       @RequestParam(value = "bypagesize", defaultValue = "", required = false) String bypagesize, Model model) {
        model.addAttribute("reportletPath", reportletPath);
        model.addAttribute("bypagesize", bypagesize);
        return "pt/exchangedata/toShangShiStatistics";
    }

}
