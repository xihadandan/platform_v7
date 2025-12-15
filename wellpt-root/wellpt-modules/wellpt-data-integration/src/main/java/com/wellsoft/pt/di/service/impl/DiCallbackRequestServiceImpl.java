/*
 * @(#)2019-07-25 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.di.service.impl;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.di.callback.AbstractDiCallback;
import com.wellsoft.pt.di.dao.DiCallbackRequestDao;
import com.wellsoft.pt.di.entity.DiCallbackRequestEntity;
import com.wellsoft.pt.di.entity.DiCallbackRequestHisEntity;
import com.wellsoft.pt.di.enums.CallbackStatusEnum;
import com.wellsoft.pt.di.response.Response;
import com.wellsoft.pt.di.service.DiCallbackRequestHisService;
import com.wellsoft.pt.di.service.DiCallbackRequestService;
import com.wellsoft.pt.di.util.MessageUtils;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.rowset.serial.SerialBlob;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.util.List;
import java.util.Map;

/**
 * Description: 数据库表DI_CALLBACK_REQUEST的service服务接口实现类
 *
 * @author chenq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019-07-25.1	chenq		2019-07-25		Create
 * </pre>
 * @date 2019-07-25
 */
@Service
public class DiCallbackRequestServiceImpl extends
        AbstractJpaServiceImpl<DiCallbackRequestEntity, DiCallbackRequestDao, String> implements
        DiCallbackRequestService {


    @Override
    public DiCallbackRequestEntity getOneReponsedCallbackRequest() {
        DiCallbackRequestEntity example = new DiCallbackRequestEntity();
        example.setCallbackStatus(CallbackStatusEnum.WAIT_DEAL.ordinal());//已反馈的
        List<DiCallbackRequestEntity> requestEntityList = this.dao.listByEntity(example);

        if (CollectionUtils.isEmpty(requestEntityList)) {
            return null;

        }
        example = requestEntityList.get(0);
        return example;
    }


    @Override
    @Transactional
    public Response executeCallback(String requestId, Object requestXml) {
        DiCallbackRequestEntity requestEntity = new DiCallbackRequestEntity();
        requestEntity.setRequestId(requestId);
        List<DiCallbackRequestEntity> entities = this.dao.listByEntity(requestEntity);
        Response response = new Response();
        if (CollectionUtils.isEmpty(entities)) {
            response.error(-1, "系统无此请求等待");
            return response;
        }
        int status = CallbackStatusEnum.DONE.ordinal();
        requestEntity = entities.get(0);
        requestEntity.setResponseBody(messageBlob(requestXml));
        try {
            IgnoreLoginUtils.loginSuperadmin();
            AbstractDiCallback callback = (AbstractDiCallback) Class.forName(
                    requestEntity.getCallbackClass()).newInstance();
            callback.callback(requestXml,
                    MessageUtils.fromJSON(requestEntity.getRequestBody()));
        } catch (Exception e) {
            logger.error("执行数据交换异步回调异常：{}", Throwables.getStackTraceAsString(e));
            status = CallbackStatusEnum.EXCEPTION_DONE.ordinal();//反馈处理失败
            response.error(-1, "系统异常");
        } finally {
            requestEntity.setCallbackStatus(status);
            this.save(requestEntity);
            if (status == CallbackStatusEnum.DONE.ordinal()) {
                //处理成功 -> 转储到历史表
                DiCallbackRequestHisEntity hisEntity = new DiCallbackRequestHisEntity();
                BeanUtils.copyProperties(requestEntity, hisEntity);
                this.delete(requestEntity);
                hisEntity.setUuid(null);
                ApplicationContextHolder.getBean(
                        DiCallbackRequestHisService.class).save(hisEntity);
            }

            IgnoreLoginUtils.logout();
        }
        return response;
    }

    private Blob messageBlob(Object obj) {
        InputStream in = null;
        OutputStream out = null;
        ObjectOutputStream objOut = null;
        try {
            if (obj != null) {

                if (obj instanceof byte[]) {
                    return new SerialBlob((byte[]) obj);
                }

                //对象流
                out = new ByteArrayOutputStream();
                objOut = new ObjectOutputStream(
                        out);
                objOut.writeObject(obj);
                objOut.flush();
                return new SerialBlob(((ByteArrayOutputStream) out).toByteArray());
            }

        } catch (Exception e) {
            //throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(objOut);
        }
        return null;
    }

    @Override
    @Transactional
    public int updateCallbackWaitDeal(String requestId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("requestId", requestId);
        params.put("status", CallbackStatusEnum.WAIT_DEAL.ordinal());
        return this.dao.updateByHQL(
                "update DiCallbackRequestEntity set callbackStatus=:status where requestId=:requestId",
                params);
    }
}
