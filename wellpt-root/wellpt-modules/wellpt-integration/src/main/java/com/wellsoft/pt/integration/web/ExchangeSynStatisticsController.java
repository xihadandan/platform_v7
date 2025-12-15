package com.wellsoft.pt.integration.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.integration.service.ExchangeDataSynService;
import com.wellsoft.pt.integration.service.SynDataStaticsService;
import com.wellsoft.pt.integration.service.SynDataStaticsService.DataRecord;
import com.wellsoft.pt.integration.support.SynStaticsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年8月14日.1	zhongzh		2016年8月14日		Create
 * </pre>
 * @date 2016年8月14日
 */
@Controller
@RequestMapping("/exchangesyn/statistics")
public class ExchangeSynStatisticsController extends BaseController {

    public static final String DATA_LIST = "list";

    public static final String DATA_STATUS = "status";

    public static final String DATA_SIZE = "dsize";

    public static final String DATA_SPEED = "dspeed";

    public static final String DATA_STATE = "dstate";

    public static final String DATA_RECORDS = "drecords";

    public static final String BOOL_CLOB_DATA = "bclob";

    public static final String PARAM_DAO_BEAN = "dao";

    public static final String PARAM_SUUID_FIELD = "suuid";

    public static final String PARAM_STABLE_FIELD = "stable";

    @Autowired
    private SynDataStaticsService synDataStaticsService;

    @Autowired
    private ExchangeDataSynService exchangeDataSynService;

    @RequestMapping(value = "/index")
    public String toIndex(Model model) {
        model.addAllAttributes(synDataStaticsService.statistics());
        return "pt/exchangedata/synstatus";
    }

    /**
     * 内外网同步统计视图
     *
     * @param model
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/data", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> statistics() {
        return synDataStaticsService.statistics();
    }

    /**
     * 同步心跳
     *
     * @param model
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/beat", produces = MediaType.APPLICATION_JSON_VALUE)
    public Boolean heartbeat() {
        return exchangeDataSynService.beat();
    }

    /**
     * 同步心跳上下文
     *
     * @param model
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/beatcontext", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> beatcontext() {
        return exchangeDataSynService.getBeatContext();
    }

    /**
     * 内外网同步统计视图
     *
     * @param model
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/dataInQueue", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> dataInQueue() {
        Map<String, Object> data = new HashMap<String, Object>();
        Map<String, Number> dataInQueue = synDataStaticsService.dataInQueue();
        data.putAll(dataInQueue);
        data.put(SynDataStaticsService.RULE_BEAN_FIELD, SynStaticsUtils.instance);
        return data;
    }

    /**
     * 应用内外网同步统计视图
     *
     * @param model
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/applyInQueue", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> applyInQueue() {
        Map<String, Object> data = new HashMap<String, Object>();
        Map<String, Number> dataInQueue = synDataStaticsService.dataInQueue();
        data.putAll(dataInQueue);
        SynStaticsUtils.doUpdate(dataInQueue);// 手动更新
        data.put(SynDataStaticsService.RULE_BEAN_FIELD, SynStaticsUtils.instance);
        return data;
    }

    /**
     * 启用严格顺序模式
     *
     * @param model
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/enableSort", produces = MediaType.APPLICATION_JSON_VALUE)
    public Boolean enableStrictSort() {
        exchangeDataSynService.setStrictSort(true);
        return true;
    }

    /**
     * 关掉严格顺序模式
     *
     * @param model
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/disableSort", produces = MediaType.APPLICATION_JSON_VALUE)
    public Boolean disableStrictSort() {
        exchangeDataSynService.setStrictSort(false);
        return false;
    }

    /**
     * 顺序上下文
     *
     * @param model
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/preContext", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> preContext() {
        return exchangeDataSynService.getPreContext();
    }

    /**
     * 重置内外网同步统计视图
     *
     * @param model
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/resetInQueue", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> resetInQueue() {
        Map<String, Object> data = new HashMap<String, Object>();
        Map<String, Number> dataInQueue = synDataStaticsService.dataInQueue();
        data.putAll(dataInQueue);
        SynStaticsUtils.doReset();
        data.put(SynDataStaticsService.RULE_BEAN_FIELD, SynStaticsUtils.instance);
        return data;
    }

    @RequestMapping(value = "/datalist/{status}")
    public String dataList(HttpServletRequest request, @PathVariable("status") Integer status, Model model) {
        String dao = request.getParameter(PARAM_DAO_BEAN);
        String suuid = request.getParameter(PARAM_SUUID_FIELD);
        String stable = request.getParameter(PARAM_STABLE_FIELD);
        model.addAttribute(DATA_STATUS, status);
        model.addAttribute(DATA_LIST, synDataStaticsService.queryDataList(dao, status, stable, suuid));
        model.addAttribute(SynDataStaticsService.DIRECTION_FIELD, exchangeDataSynService.getDirection());
        return "pt/exchangedata/syndatalist";
    }

    @RequestMapping(value = "/cloblist/{status}")
    public String clobList(HttpServletRequest request, @PathVariable("status") Integer status, Model model) {
        String dao = request.getParameter(PARAM_DAO_BEAN);
        String stable = request.getParameter(PARAM_STABLE_FIELD);
        model.addAttribute(DATA_STATUS, status);
        model.addAttribute(BOOL_CLOB_DATA, true);
        model.addAttribute(DATA_LIST, synDataStaticsService.queryClobList(dao, status, stable));
        model.addAttribute(SynDataStaticsService.DIRECTION_FIELD, exchangeDataSynService.getDirection());
        return "pt/exchangedata/syndatalist";
    }

    @RequestMapping(value = "/datadetail/{uuid}")
    public String dataDetail(HttpServletRequest request, @PathVariable("uuid") String uuid, Model model) {
        String dao = request.getParameter(PARAM_DAO_BEAN);
        model.addAttribute(DATA_LIST, synDataStaticsService.queryDataDetail(dao, uuid));
        model.addAttribute(SynDataStaticsService.DIRECTION_FIELD, exchangeDataSynService.getDirection());
        return "pt/exchangedata/syndatalist";
    }

    @RequestMapping(value = "/charts")
    @ResponseBody
    public synchronized Map<String, Object> charts(Model model) {
        Map<String, Object> data = new HashMap<String, Object>();
        Map<String, Number> dataSpeed = synDataStaticsService.dataSpeed();
        Collection<DataRecord> dataRecord = synDataStaticsService.dataRecord();

        data.put(DATA_SPEED, dataSpeed);
        data.put(DATA_RECORDS, dataRecord);
        return data;
    }

}
