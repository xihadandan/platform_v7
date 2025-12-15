package com.wellsoft.oauth2.service.impl;

import com.wellsoft.oauth2.dto.OAuthClientDetailDto;
import com.wellsoft.oauth2.entity.OAuthClientDetailEntity;
import com.wellsoft.oauth2.repository.OAuthClientDetailRepository;
import com.wellsoft.oauth2.service.OAuthClientDetailService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/9/10
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/9/10    chenq		2019/9/10		Create
 * </pre>
 */
@Service
public class OAuthClientDetailServiceImpl extends
        AbstractJpaServiceImpl<OAuthClientDetailEntity, String, OAuthClientDetailRepository> implements
        OAuthClientDetailService {

    @Override
    @Transactional
    public OAuthClientDetailEntity saveWhenNotExist(OAuthClientDetailDto dto) {
        //查询客户端是否存在，如果存在，则不保存
        if (repository.exists(dto.getClientId())) {
            throw new RuntimeException("已存在的客户端ID=[" + dto.getClientId() + "]");
        }

        OAuthClientDetailEntity entity = new OAuthClientDetailEntity();
        BeanUtils.copyProperties(dto, entity);
        entity.setClientSecret(DigestUtils.md2Hex(dto.getClientId()));
        return save(entity);
    }

    @Override
    public OAuthClientDetailEntity getByClientId(String s) {
        return repository.getByClientId(s);
    }
}
