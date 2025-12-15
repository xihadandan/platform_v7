package com.wellsoft.pt.api.service.impl;

import com.wellsoft.pt.api.dao.impl.ApiOutSystemConfigDaoImpl;
import com.wellsoft.pt.api.dto.ApiAuthorizeItemDto;
import com.wellsoft.pt.api.dto.ApiOutSysServiceConfigDto;
import com.wellsoft.pt.api.dto.ApiOutSystemCofigDto;
import com.wellsoft.pt.api.entity.ApiAuthorizeItemEntity;
import com.wellsoft.pt.api.entity.ApiOutSysServiceConfigEntity;
import com.wellsoft.pt.api.entity.ApiOutSystemConfigEntity;
import com.wellsoft.pt.api.service.ApiOutSysConfigService;
import com.wellsoft.pt.api.service.ApiOutSysServiceConfigService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
public class ApiOutSysConfigServiceImpl extends
        AbstractJpaServiceImpl<ApiOutSystemConfigEntity, ApiOutSystemConfigDaoImpl, String> implements
        ApiOutSysConfigService {

    @Autowired
    ApiOutSysServiceConfigService apiOutSysServiceConfigService;

    private String tokenSignKey;

    @Override
    @Transactional
    public void deleteConfig(List<String> uuids) {
        apiOutSysServiceConfigService.deleteBySystemUuids(uuids);
        this.dao.deleteByUuids(uuids);
    }

    @Override
    public byte[] tokenKey() {
        byte[] result = null;
        try {
            //根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
            SecretKeySpec signinKey = new SecretKeySpec(
                    tokenSignKey.getBytes(),
                    SignatureAlgorithm.HS256.getJcaName());
            Mac mac = Mac.getInstance(SignatureAlgorithm.HS256.getJcaName());
            mac.init(signinKey);
            byte[] rawHmac = mac.doFinal("wellpt".getBytes());
            result = Base64.encodeBase64(rawHmac);
        } catch (NoSuchAlgorithmException e) {
            logger.error("token秘钥异常：", e);
        } catch (InvalidKeyException e) {
            logger.error("token秘钥异常：", e);
        }
        return result;
    }

    @Override
    public ApiOutSystemCofigDto getByUuid(String uuid, boolean fetchService) {
        ApiOutSystemConfigEntity configEntity = this.getOne(uuid);
        ApiOutSystemCofigDto dto = new ApiOutSystemCofigDto();
        BeanUtils.copyProperties(configEntity, dto, "authorizeItems", "services");
        if (fetchService) {
            List<ApiOutSysServiceConfigEntity> serviceConfigEntities = configEntity.getServices();
            if (CollectionUtils.isNotEmpty(serviceConfigEntities)) {
                for (ApiOutSysServiceConfigEntity service : serviceConfigEntities) {
                    ApiOutSysServiceConfigDto serviceConfigDto = new ApiOutSysServiceConfigDto();
                    BeanUtils.copyProperties(service, serviceConfigDto);
                    dto.getServiceConfigs().add(serviceConfigDto);
                }
            }

            List<ApiAuthorizeItemEntity> authroizeItemEntities = configEntity.getAuthorizeItems();
            if (CollectionUtils.isNotEmpty(authroizeItemEntities)) {
                for (ApiAuthorizeItemEntity item : authroizeItemEntities) {
                    ApiAuthorizeItemDto itemDto = new ApiAuthorizeItemDto();
                    itemDto.setIsAuthorized(item.getIsAuthorized());
                    itemDto.setPattern(item.getPattern());
                    dto.getAuthorizeItems().add(itemDto);

                }
            }


        }
        return dto;
    }

    @Override
    public ApiOutSysServiceConfigDto getServiceConfig(String systemCode, String serviceCode) {
        ApiOutSysServiceConfigEntity configEntity = this.apiOutSysServiceConfigService.getServiceConfig(
                systemCode,
                serviceCode);
        ApiOutSysServiceConfigDto dto = new ApiOutSysServiceConfigDto();
        BeanUtils.copyProperties(configEntity, dto);
        return dto;
    }


    @Override
    public ApiOutSystemConfigEntity getBySystemCode(String systemCode) {
        List<ApiOutSystemConfigEntity> configEntities = this.dao.listByFieldEqValue("systemCode",
                systemCode);
        return CollectionUtils.isNotEmpty(configEntities) ? configEntities.get(0) : null;
    }


    @Value("${api.token.signinKey}")
    public void setTokenSignKey(String tokenSignKey) {
        this.tokenSignKey = tokenSignKey;
    }
}
