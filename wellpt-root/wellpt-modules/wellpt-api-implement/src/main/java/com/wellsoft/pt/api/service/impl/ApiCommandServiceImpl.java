package com.wellsoft.pt.api.service.impl;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.api.adapter.WellptApiAdapter;
import com.wellsoft.pt.api.dao.impl.ApiCommandDaoImpl;
import com.wellsoft.pt.api.dto.ApiOutSysServiceConfigDto;
import com.wellsoft.pt.api.entity.ApiCommandDetailEntity;
import com.wellsoft.pt.api.entity.ApiCommandEntity;
import com.wellsoft.pt.api.enums.CommandStatusEnum;
import com.wellsoft.pt.api.facade.ApiOutSystemFacadeService;
import com.wellsoft.pt.api.request.ApiAdapterRequest;
import com.wellsoft.pt.api.response.ApiResponse;
import com.wellsoft.pt.api.service.ApiCommandDetailService;
import com.wellsoft.pt.api.service.ApiCommandService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ObjectInputStream;
import java.util.Date;
import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/8/13
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/8/13    chenq		2018/8/13		Create
 * </pre>
 */
@Service
public class ApiCommandServiceImpl extends
        AbstractJpaServiceImpl<ApiCommandEntity, ApiCommandDaoImpl, String> implements
        ApiCommandService {

    @Autowired
    ApiCommandDetailService detailService;

    @Override
    @Transactional
    public void updateRetryCntAdded(String commandUuid) {
        ApiCommandEntity commandEntity = getOne(commandUuid);
        int num = commandEntity.getRetryNum();
        Date nextRetryTime = null;
        if (num <= 4) {//五次以内的
            nextRetryTime = DateUtils.addMinutes(new Date(), 10);//10分钟后重试
        } else if (num >= 5 && num <= 10) {
            nextRetryTime = DateUtils.addHours(new Date(), 3);//3小时候后重试
        }//其他不用重试了

        commandEntity.setRetryNum(num + 1);
        commandEntity.setNextRetryTime(nextRetryTime);
        this.save(commandEntity);
    }

    @Override
    @Transactional
    public void retryCommand(String commandUuid) throws Exception {
        ApiCommandDetailEntity detailEntity = detailService.getByCommandUuid(commandUuid);
        ApiCommandEntity commandEntity = this.getOne(commandUuid);
        if (StringUtils.isNotBlank(commandEntity.getOutSystemCode()) && StringUtils.isNotBlank(
                commandEntity.getServiceCode())) {
            //获取配置
            ApiOutSysServiceConfigDto serviceConfigDto = ApplicationContextHolder.getBean(
                    ApiOutSystemFacadeService.class).getServiceConfig(
                    commandEntity.getOutSystemCode(), commandEntity.getServiceCode());
            //调用适配器
            WellptApiAdapter adapter = null;
            try {
                adapter = (WellptApiAdapter) ApplicationContextHolder.getBean(
                        Class.forName(serviceConfigDto.getServiceAdapter()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            ObjectInputStream in = new ObjectInputStream(
                    detailEntity.getAdapterRequest().getBinaryStream());
            ApiAdapterRequest adapterRequest = (ApiAdapterRequest) in.readObject();
            ApiResponse response = adapter.invoke(adapterRequest);
            commandEntity.setStatus(
                    response.isSuccess() ? CommandStatusEnum.SUCCESS.getValue() : CommandStatusEnum.FAIL.getValue());
            this.dao.save(commandEntity);
        }

    }

    @Override
    public List<ApiCommandEntity> listByGreaterThanRetryTime(Date date) {
        return this.dao.listByGreaterThanRetryTime(date);
    }
}
