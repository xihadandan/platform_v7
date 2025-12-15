package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.response.WorkHourResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-2-3.1	Administrator		2015-2-3		Create
 * </pre>
 * @date 2015-2-3
 */
public class WorkHourRequest extends WellptRequest<WorkHourResponse> {

    public static final String METHOD_WORKTIME = "WorkTime";
    public static final String METHOD_POINTTIME = "PointTime";
    public static final String TYPE_DAY = "day";
    public static final String TYPE_HOUR = "hour";

    private Map<String, String> pMap = new HashMap<String, String>();

    public WorkHourRequest() {
        super();
    }

    public WorkHourRequest(String methodFlag) {
        super();
        pMap.put("methodFlag", methodFlag);
    }

    /**
     * 获取服务名称
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptRequest#getApiServiceName()
     */
    @Override
    public String getApiServiceName() {
        return ApiServiceName.WORKHOURSERVICE;
    }

    /**
     * 获取响应类型
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptRequest#getResponseClass()
     */
    @Override
    public Class<WorkHourResponse> getResponseClass() {
        return WorkHourResponse.class;
    }

    /**
     * 根据起始时间、结束时间返回工作时间
     *
     * @param startTime 开始时间,可以为负
     * @param endTime   结束时间{METHOD_WORKTIME,METHOD_POINTTIME}
     */
    public void setParm(Date startTime, Date endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

        pMap.put("startTime", sdf.format(startTime));
        pMap.put("endTime", sdf.format(endTime));
    }

    /**
     * 根据起始时间，花费时间返回结束时间
     *
     * @param startTime 起始时间
     * @param time      花费时间,可以为负
     * @param type      花费时间类型{"day","hour"}
     */
    public void setParm(Date startTime, double time, String type) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

        pMap.put("startTime", sdf.format(startTime));
        pMap.put("time", String.valueOf(time));
        pMap.put("type", type);
    }

    public Map<String, String> getpMap() {
        return pMap;
    }

    public void setpMap(Map<String, String> pMap) {
        this.pMap = pMap;
    }

    public void setValue(String key, String value) {
        pMap.put(key, value);
    }

    public String getValue(String key) {
        return pMap.get(key);
    }
}
