package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.api.dto.ApiCommandDetailDto;
import com.wellsoft.pt.api.entity.ApiCommandDetailEntity;
import com.wellsoft.pt.api.entity.ApiCommandEntity;
import com.wellsoft.pt.api.facade.ApiCommandFacadeService;
import com.wellsoft.pt.api.request.ApiAdapterRequest;
import com.wellsoft.pt.api.service.ApiCommandDetailService;
import com.wellsoft.pt.api.service.ApiCommandService;
import com.wellsoft.pt.api.support.CallApiParams;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.rowset.serial.SerialBlob;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
public class ApiCommandFacadeServiceImpl extends AbstractApiFacade implements
        ApiCommandFacadeService {

    @Autowired
    ApiCommandDetailService apiCommandDetailService;

    @Autowired
    ApiCommandService apiCommandService;

    @Override
    public ApiCommandDetailDto getCommandDetailByCommandUuid(String commandUuid) {
        ApiCommandDetailEntity detailEntity = apiCommandDetailService.getByCommandUuid(commandUuid);
        try {
            ApiCommandDetailDto detailDto = new ApiCommandDetailDto();
            if (detailEntity.getRequestBody() != null) {
                detailDto.setRequestBody(
                        IOUtils.toString(detailEntity.getRequestBody().getCharacterStream()));
            }
            if (detailEntity.getResponseBody() != null) {
                detailDto.setResponseBody(
                        IOUtils.toString(detailEntity.getResponseBody().getCharacterStream()));
            }
            detailDto.setCommandUuid(commandUuid);
            return detailDto;
        } catch (Exception e) {
            logger.error("查询报文明细异常：", e);
        }

        return null;
    }

    @Override
    public void resend(List<String> uuids, String requestBody) {
        for (String uuid : uuids) {
            try {
                apiCommandService.retryCommand(uuid);
            } catch (Exception e) {
                logger.error("重试指令{}异常：", uuid, e);
            }

        }


    }

    @Override
    @Transactional
    public String saveAdapterRequestCommand(ApiAdapterRequest request) {
        if (StringUtils.isNotBlank(request.getCommandUuid())) {
            apiCommandService.updateRetryCntAdded(request.getCommandUuid());
            return request.getCommandUuid();
        }
        CallApiParams params = request.getParams();
        ApiCommandEntity apiCommandEntity = new ApiCommandEntity();
        apiCommandEntity.setIdempotentKey(params.getIdempotentKey());
        apiCommandEntity.setBusinessType(params.getBusinessType());
        apiCommandEntity.setBizCorrelationKey(params.getBizCorrelationKey());
        apiCommandEntity.setServiceCode(params.getServiceCode());
        apiCommandEntity.setOutSystemCode(params.getSystemCode());
        apiCommandEntity.setApiMode(request.getApiMode());
        apiCommandEntity.setSerialNumber(generateSerialNumber("RQ" + request.getApiMode()));
        apiCommandService.save(apiCommandEntity);


        ApiCommandDetailEntity detailEntity = new ApiCommandDetailEntity();
        detailEntity.setCommandUuid(apiCommandEntity.getUuid());

        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oo = null;
        try {
            oo = new ObjectOutputStream(bo);
            request.setCommandUuid(apiCommandEntity.getUuid());
            oo.writeObject(request);
            byte[] bytes = bo.toByteArray();
            Blob blob = new SerialBlob(bytes);
            detailEntity.setAdapterRequest(blob);
        } catch (Exception e) {
            logger.error("保存适配器请求字节流异常", e);
        } finally {
            IOUtils.closeQuietly(oo);
            IOUtils.closeQuietly(bo);
        }
        apiCommandDetailService.save(detailEntity);
        return apiCommandEntity.getUuid();
    }


    private String generateSerialNumber(String prefix) {
        return prefix + DateFormatUtils.format(new Date(),
                "yyMMddHHmmssSSS") + UUID.randomUUID().toString().substring(0, 4);
    }
}
