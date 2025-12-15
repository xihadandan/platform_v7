package com.wellsoft.pt.app.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 用户消息不再提示
 *
 * @author chenq
 * @date 2019/10/16
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/10/16    chenq		2019/10/16		Create
 * </pre>
 */
@Entity
@Table(name = "APP_TIP_NO_LONGER_REMIND")
@DynamicUpdate
@DynamicInsert
public class AppTipNoLongerRemindEntity extends IdEntity {

    private String tipCode;//定义的消息编码

    private String userId;//用户ID

    public AppTipNoLongerRemindEntity() {
    }

    public AppTipNoLongerRemindEntity(String tipCode, String userId) {
        this.tipCode = tipCode;
        this.userId = userId;
    }

    public String getTipCode() {
        return tipCode;
    }

    public void setTipCode(String tipCode) {
        this.tipCode = tipCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
