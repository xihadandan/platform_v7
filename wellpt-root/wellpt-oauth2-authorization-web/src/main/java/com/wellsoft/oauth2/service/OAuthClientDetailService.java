package com.wellsoft.oauth2.service;

import com.wellsoft.oauth2.dto.OAuthClientDetailDto;
import com.wellsoft.oauth2.entity.OAuthClientDetailEntity;
import com.wellsoft.oauth2.repository.OAuthClientDetailRepository;

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
public interface OAuthClientDetailService extends
        JpaService<OAuthClientDetailEntity, String, OAuthClientDetailRepository> {
    OAuthClientDetailEntity saveWhenNotExist(OAuthClientDetailDto dto);

    OAuthClientDetailEntity getByClientId(String s);
}
