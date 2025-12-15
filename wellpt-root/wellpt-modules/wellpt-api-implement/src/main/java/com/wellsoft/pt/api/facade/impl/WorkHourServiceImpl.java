package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.request.WorkHourRequest;
import com.wellsoft.pt.api.response.WorkHourResponse;
import com.wellsoft.pt.basicdata.workhour.enums.WorkUnit;
import com.wellsoft.pt.basicdata.workhour.support.WorkHourUtils;
import com.wellsoft.pt.basicdata.workhour.support.WorkPeriod;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service(ApiServiceName.WORKHOURSERVICE)
@Transactional
public class WorkHourServiceImpl extends BaseServiceImpl implements WellptService<WorkHourRequest> {

    @Override
    public WellptResponse doService(WorkHourRequest wr) {
        //System.out.println("service -----------");
        WellptResponse resp = null;
        try {
            if (WorkHourRequest.METHOD_WORKTIME.equals(wr.getValue("methodFlag"))) {
                // 获取参数值
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                Date startDate = sdf.parse(wr.getValue("startTime"));
                Date endDate = sdf.parse(wr.getValue("endTime"));
                resp = getWorkTime(startDate, endDate);
            } else if (WorkHourRequest.METHOD_POINTTIME.equals(wr.getValue("methodFlag"))) {
                // 获取参数值
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                Date startDate = sdf.parse(wr.getValue("startTime"));
                double time = Double.valueOf(wr.getValue("time"));

                resp = getPointTime(startDate, time, WorkHourRequest.TYPE_DAY.equals(wr.getValue("type"))
                        ? WorkUnit.WorkingDay : WorkUnit.WorkingHour);
            }
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        }
        return resp;
    }

    /**
     * 根据起始时间、结束时间返回工作时间
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
    public WellptResponse getWorkTime(Date startTime, Date endTime) {
        WorkPeriod wp = WorkHourUtils.getWorkPeriod(startTime, endTime);
        WorkHourResponse wr = new WorkHourResponse();
        wr.setCode(WorkHourResponse.KEY_SUCCESS);
        wr.setValue("type", WorkHourRequest.TYPE_HOUR);
        wr.setValue("result", String.valueOf(wp.getWorkHour()));
        return wr;
    }

    /**
     * 根据起始时间，花费时间返回结束时间
     *
     * @param startTime 开始时间
     * @param time      花费时间，可以为负
     * @param workUnit  花费时间类型
     */
    public WellptResponse getPointTime(Date startTime, Double time, WorkUnit workUnit) {
        Date date = WorkHourUtils.getWorkDate(startTime, time, workUnit);
        WorkHourResponse wr = new WorkHourResponse();
        wr.setCode(WorkHourResponse.KEY_SUCCESS);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

        wr.setValue("result", sdf.format(date));
        return wr;
    }
}
