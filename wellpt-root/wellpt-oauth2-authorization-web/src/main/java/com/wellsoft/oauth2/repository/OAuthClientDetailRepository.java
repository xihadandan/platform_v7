package com.wellsoft.oauth2.repository;

import com.wellsoft.oauth2.entity.OAuthClientDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
public interface OAuthClientDetailRepository extends
        JpaRepository<OAuthClientDetailEntity, String> {
    @Query(value = "from OAuthClientDetailEntity where clientId=:clientId")
    OAuthClientDetailEntity getByClientId(@Param("clientId") String s);
}
