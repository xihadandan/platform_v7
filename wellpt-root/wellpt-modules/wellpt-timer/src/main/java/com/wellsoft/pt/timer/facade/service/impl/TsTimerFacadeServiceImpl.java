/*
 * @(#)2021年4月8日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.facade.service.impl;

import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.timer.dto.TsTimerDto;
import com.wellsoft.pt.timer.entity.TsTimerEntity;
import com.wellsoft.pt.timer.facade.service.TsTimerFacadeService;
import com.wellsoft.pt.timer.service.TsTimerService;
import com.wellsoft.pt.timer.support.TimerWorkTime;
import com.wellsoft.pt.timer.support.TsTimerParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年4月8日.1	zhulh		2021年4月8日		Create
 * </pre>
 * @date 2021年4月8日
 */
@Service
public class TsTimerFacadeServiceImpl implements TsTimerFacadeService {

    @Autowired
    private TsTimerService timerService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsTimerFacadeService#startTimer(com.wellsoft.pt.timer.support.TsTimerParam)
     */
    @Override
    public TsTimerDto startTimer(TsTimerParam timerParam) {
        TsTimerEntity timerEntity = timerService.startTimer(timerParam);
        TsTimerDto tsTimerDto = new TsTimerDto();
        BeanUtils.copyProperties(timerEntity, tsTimerDto);
        return tsTimerDto;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsTimerFacadeService#pauseTimer(java.lang.String)
     */
    @Override
    public double pauseTimer(String timerUuid) {
        return timerService.pauseTimer(timerUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsTimerFacadeService#resumeTimer(java.lang.String)
     */
    @Override
    public Date resumeTimer(String timerUuid) {
        return timerService.resumeTimer(timerUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsTimerFacadeService#stopTimer(java.lang.String)
     */
    @Override
    public double stopTimer(String timerUuid) {
        return timerService.stopTimer(timerUuid);
    }

    /**
     * 重新开始计时，返回到期时间
     *
     * @param timerUuid
     * @return
     */
    @Override
    public Date restartTimer(String timerUuid) {
        return timerService.restartTimer(timerUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsTimerFacadeService#getTimer(java.lang.String)
     */
    @Override
    public TsTimerDto getTimer(String timerUuid) {
        TsTimerEntity timerEntity = timerService.getOne(timerUuid);
        TsTimerDto tsTimerDto = new TsTimerDto();
        BeanUtils.copyProperties(timerEntity, tsTimerDto);
        return tsTimerDto;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsTimerFacadeService#getRemainingTimeLimit(java.lang.String)
     */
    @Override
    public double getRemainingTimeLimit(String timerUuid) {
        return timerService.getRemainingTimeLimit(timerUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsTimerFacadeService#getTimeLimitNameInMinute(java.lang.String)
     */
    @Override
    public String getTimeLimitNameInMinute(String timerUuid) {
        return timerService.getTimeLimitNameInMinute(timerUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsTimerFacadeService#changeTimeLimit(java.lang.String, int)
     */
    @Override
    public int changeTimeLimit(String timerUuid, int timeLimit) {
        return timerService.changeTimeLimit(timerUuid, timeLimit);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsTimerFacadeService#changeDueTime(java.lang.String, java.util.Date)
     */
    @Override
    public int changeDueTime(String timerUuid, Date dueTime) {
        return timerService.changeDueTime(timerUuid, dueTime);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsTimerFacadeService#getOverDueTime(java.lang.String)
     */
    @Override
    public Date getOverDueTime(String timerUuid) {
        return timerService.getOverDueTime(timerService.getOne(timerUuid));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsTimerFacadeService#calculateTime(java.lang.String, java.util.Date, int, java.lang.String)
     */
    @Override
    public Date calculateTime(String timerUuid, Date fromTime, double amount, String timingMode) {
        return timerService.calculateTime(timerUuid, fromTime, amount, timingMode);
    }

    @Override
    public TimerWorkTime getTimerWorkTime(String timerUuid) {
        if (StringUtils.isBlank(timerUuid)) {
            Date currentTime = Calendar.getInstance().getTime();
            return new TimerWorkTime(timerUuid, 0d, currentTime, currentTime, 0);
        }
        return timerService.getTimerWorkTime(timerUuid);
    }

}
