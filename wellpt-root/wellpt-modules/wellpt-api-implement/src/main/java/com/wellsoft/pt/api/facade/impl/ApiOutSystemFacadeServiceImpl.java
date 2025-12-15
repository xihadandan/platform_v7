package com.wellsoft.pt.api.facade.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.api.adapter.WellptApiAdapter;
import com.wellsoft.pt.api.dto.ApiAuthorizeItemDto;
import com.wellsoft.pt.api.dto.ApiOutSysServiceConfigDto;
import com.wellsoft.pt.api.dto.ApiOutSystemCofigDto;
import com.wellsoft.pt.api.entity.ApiAuthorizeItemEntity;
import com.wellsoft.pt.api.entity.ApiOutSysServiceConfigEntity;
import com.wellsoft.pt.api.entity.ApiOutSystemConfigEntity;
import com.wellsoft.pt.api.facade.ApiOutSystemFacadeService;
import com.wellsoft.pt.api.service.ApiAuthroizeItemService;
import com.wellsoft.pt.api.service.ApiOutSysConfigService;
import com.wellsoft.pt.api.service.ApiOutSysServiceConfigService;
import com.wellsoft.pt.api.support.TokenClaims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class ApiOutSystemFacadeServiceImpl extends AbstractApiFacade implements
        ApiOutSystemFacadeService {


    @Autowired
    ApiOutSysConfigService apiOutSysConfigService;

    @Autowired
    ApiOutSysServiceConfigService apiOutSysServiceConfigService;

    @Autowired
    List<WellptApiAdapter> apiAdapters;

    @Autowired
    ApiAuthroizeItemService apiAuthroizeItemService;


    @Override
    public ApiOutSystemCofigDto getByUuid(String uuid, boolean fetchService) {
        return apiOutSysConfigService.getByUuid(uuid, fetchService);

    }

    @Override
    public void deleteConfig(List<String> uuids) {
        apiOutSysConfigService.deleteConfig(uuids);
    }

    @Override
    public String generateToken(String uuid) {
        ApiOutSystemConfigEntity configEntity = apiOutSysConfigService.getOne(uuid);
        List<ApiAuthorizeItemEntity> itemEntities = apiAuthroizeItemService.listBySystemUuid(
                configEntity.getUuid());
        TokenClaims claims = new TokenClaims();
        if (CollectionUtils.isNotEmpty(itemEntities)) {
            List<String> authrozeApis = Lists.newArrayList();
            List<String> unauthrozeApis = Lists.newArrayList();
            for (ApiAuthorizeItemEntity item : itemEntities) {
                if (item.getIsAuthorized()) {
                    authrozeApis.add(item.getPattern());
                } else {
                    unauthrozeApis.add(item.getPattern());
                }
            }
            claims.setAuthorizeApis(authrozeApis);
            claims.setUnauthorizeApis(unauthrozeApis);
        }
        claims.setSystemCode(configEntity.getSystemCode());
        claims.setSystemName(configEntity.getSystemName());
        claims.setUnit(configEntity.getSystemUnitId());


        String token = Jwts.builder().setSubject(configEntity.getSystemCode()).setAudience(
                configEntity.getSystemName()).setExpiration(
                DateUtils.addYears(new Date(), 1)).addClaims(claims).
                signWith(
                        Keys.hmacShaKeyFor(apiOutSysConfigService.tokenKey())).compact();
        configEntity.setToken(token);
        apiOutSysConfigService.save(configEntity);
        return token;
    }


    @Override
    public void addSystemConfig(ApiOutSystemCofigDto dto) {
        ApiOutSystemConfigEntity configEntity = new ApiOutSystemConfigEntity();
        if (StringUtils.isNotBlank(dto.getUuid())) {
            configEntity = apiOutSysConfigService.getOne(dto.getUuid());
            configEntity.setSystemCode(dto.getSystemCode());
            configEntity.setSystemName(dto.getSystemName());
        } else {
            BeanUtils.copyProperties(dto, configEntity);
        }

        if (StringUtils.isNotBlank(dto.getUuid())) {
            apiOutSysServiceConfigService.deleteBySystemUuids(
                    Lists.<String>newArrayList(dto.getUuid()));
            apiAuthroizeItemService.deleteBySytemUuids(Lists.newArrayList(dto.getUuid()));

        }

        if (CollectionUtils.isNotEmpty(dto.getServiceConfigs())) {
            List<ApiOutSysServiceConfigDto> serviceConfigDtos = dto.getServiceConfigs();
            List<ApiOutSysServiceConfigEntity> serviceConfigEntities = Lists.newArrayList();
            for (ApiOutSysServiceConfigDto serviceConfigDto : serviceConfigDtos) {
                ApiOutSysServiceConfigEntity serviceConfigEntity = new ApiOutSysServiceConfigEntity();
                BeanUtils.copyProperties(serviceConfigDto, serviceConfigEntity);
                serviceConfigEntity.setSystemConfig(configEntity);
                serviceConfigEntities.add(serviceConfigEntity);
            }
            configEntity.setServices(serviceConfigEntities);
        }
        if (CollectionUtils.isNotEmpty(dto.getAuthorizeItems())) {
            List<ApiAuthorizeItemDto> itemDtos = dto.getAuthorizeItems();
            List<ApiAuthorizeItemEntity> itemEntities = Lists.newArrayList();
            for (ApiAuthorizeItemDto itemDto : itemDtos) {
                ApiAuthorizeItemEntity itemEntity = new ApiAuthorizeItemEntity();
                BeanUtils.copyProperties(itemDto, itemEntity);
                itemEntity.setSystemConfig(configEntity);
                itemEntities.add(itemEntity);
            }
            configEntity.setAuthorizeItems(itemEntities);
        }

        apiOutSysConfigService.save(configEntity);

    }

    @Override
    public Select2QueryData queryApiAdapterClass(Select2QueryInfo queryInfo) {
        Select2QueryData data = new Select2QueryData();
        if (CollectionUtils.isNotEmpty(apiAdapters)) {
            for (WellptApiAdapter apiAdapter : apiAdapters) {
                data.addResultData(new Select2DataBean(
                        AopUtils.getTargetClass(apiAdapter).getName(),
                        apiAdapter.name()));
            }
        }
        return data;
    }

    @Override
    public ApiOutSysServiceConfigDto getServiceConfig(String systemCode, String serviceCode) {
        return apiOutSysConfigService.getServiceConfig(systemCode, serviceCode);
    }


}
