package com.wellsoft.pt.basicdata.favorite.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014年7月18日.1	zhongzh		2014年7月18日		Create
 * </pre>
 * @date 2014年7月18日
 */
@Entity
@Table(name = "cd_favorite_item")
@DynamicUpdate
@DynamicInsert
public class FavoriteItem extends IdEntity {
    private static final long serialVersionUID = 1L;

    private String entityClass;
    private String entityUuid;
    private String userId;

    /**
     * @return the entityClass
     */
    public String getEntityClass() {
        return entityClass;
    }

    /**
     * @param entityClass 要设置的entityClass
     */
    public void setEntityClass(String entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * @return the entityUuid
     */
    public String getEntityUuid() {
        return entityUuid;
    }

    /**
     * @param entityUuid 要设置的entityUuid
     */
    public void setEntityUuid(String entityUuid) {
        this.entityUuid = entityUuid;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId 要设置的userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
