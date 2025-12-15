package com.wellsoft.pt.basicdata.viewcomponent.web;

import com.google.gson.*;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.basicdata.viewcomponent.facade.support.calendar.CalendarComponentDataProvider;
import com.wellsoft.pt.basicdata.viewcomponent.facade.support.calendar.CalendarEventEntity;
import com.wellsoft.pt.basicdata.viewcomponent.facade.support.calendar.CalendarEventParams;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author yaolq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月26日.1	yaolq		2018年3月26日		Create
 * </pre>
 * @date 2018年3月26日
 */
@Controller
@SuppressWarnings({"rawtypes", "unchecked"})
@RequestMapping("/basicdata/calendarcomponent")
public class CalendarComponentController extends BaseController {

    @RequestMapping("/save")
    @ResponseBody
    public ResultMessage saveEvent(@RequestBody String json) throws SQLException {
        ResultMessage resultMessage = new ResultMessage();
        JSONObject jsonObject = JSONObject.fromObject(json);
        String dataProviderId = jsonObject.getString("dataProviderId");
        String data = jsonObject.getJSONObject("data").toString();
        CalendarComponentDataProvider dp = this.getDataProvider(dataProviderId);
        try {
            //日期支持格式
            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDeserializer() {
                @Override
                public Object deserialize(JsonElement jsonElement, Type type,
                                          JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                    String dataStr = jsonElement.getAsString();
                    if (StringUtils.isBlank(dataStr)) {
                        return null;
                    }
                    try {
                        return DateUtils.parseDate(dataStr, "yyyy-MM-dd HH:mm:ss", "yyyy年MM月dd日 HH时mm分ss秒",
                                "yyyy/MM/dd HH:mm:ss", "yyyy-MM-dd", "yyyy年MM月dd日");
                    } catch (Exception e) {
                        throw new RuntimeException("不支持的时间格式转换：" + dataStr);
                    }
                }
            }).create();
            CalendarEventEntity obj = (CalendarEventEntity) gson.fromJson(data, dp.getEntityClass());
            String uuid = dp.saveEvent(obj);
            obj.setUuid(uuid);
            if (StringUtils.isNotBlank(uuid)) {
                resultMessage.setData(obj);
            } else {
                resultMessage.setSuccess(false);
                resultMessage.setMsg(new StringBuilder("保存失败"));
            }
        } catch (Exception e) {
            resultMessage.setSuccess(false);
            resultMessage.setMsg(null == e.getMessage() ? null : new StringBuilder(e.getMessage()));
            logger.error(e.getMessage(), e);
        }
        return resultMessage;
    }

    @RequestMapping("/delete")
    @ResponseBody
    public ResultMessage deleteEvent(@RequestParam(value = "dataProviderId") String dataProviderId,
                                     @RequestParam(value = "uuid", required = true) String uuid) {
        ResultMessage resultMessage = new ResultMessage();
        CalendarComponentDataProvider dp = this.getDataProvider(dataProviderId);
        dp.deleteEvent(uuid);
        return resultMessage;
    }

    private CalendarComponentDataProvider getDataProvider(String dataProviderId) {
        try {
            Object dp = ApplicationContextHolder.getBean(Class.forName(dataProviderId));
            return (CalendarComponentDataProvider) dp;
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @RequestMapping("/loadEvents")
    @ResponseBody
    public ResultMessage loadEvents(@RequestBody String json) {
        ResultMessage resultMessage = new ResultMessage();
        try {
            CalendarEventParams ep = (CalendarEventParams) JsonUtils.gson2Object(json, CalendarEventParams.class);
            CalendarComponentDataProvider dp = this.getDataProvider(ep.getDataProviderId());
            List list = dp.loadEvents(ep);
            resultMessage.setData(list);
        } catch (Exception e) {
            resultMessage.setSuccess(false);
            if (e != null && e.getMessage() != null) {
                resultMessage.setMsg(new StringBuilder(e.getMessage()));
            } else {
                resultMessage.setMsg(new StringBuilder("未知错误"));
            }
            logger.error(e.getMessage(), e);
        }
        return resultMessage;
    }

    @RequestMapping("/getEvent")
    @ResponseBody
    public ResultMessage getEvent(@RequestParam(value = "dataProviderId") String dataProviderId,
                                  @RequestParam(value = "uuid", required = true) String uuid) {
        ResultMessage resultMessage = new ResultMessage();
        CalendarComponentDataProvider dp = this.getDataProvider(dataProviderId);
        CalendarEventEntity event = dp.getEvent(uuid);
        resultMessage.setData(event);
        return resultMessage;
    }

    @RequestMapping("/getProviderInfo")
    @ResponseBody
    public ResultMessage getProviderInfo(@RequestParam(value = "dataProviderId") String dataProviderId) {
        ResultMessage resultMessage = new ResultMessage();
        CalendarComponentDataProvider dp = this.getDataProvider(dataProviderId);
        Map<String, Object> infos = new HashMap<String, Object>();
        infos.put("statusFieldName", dp.getStatusFieldName());
        infos.put("resourceFieldName", dp.getResourceFieldName());
        resultMessage.setData(infos);
        return resultMessage;
    }

}
