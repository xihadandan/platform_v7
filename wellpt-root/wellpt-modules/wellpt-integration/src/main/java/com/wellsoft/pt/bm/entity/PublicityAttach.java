package com.wellsoft.pt.bm.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Blob;

/**
 * Description: 附件信息表
 *
 * @author wangbx
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-6.1	wangbx		2013-12-6		Create
 * </pre>
 * @date 2013-12-6
 */
@Entity
@Table(name = "bm_publicity_attach")
@DynamicUpdate
@DynamicInsert
public class PublicityAttach extends IdEntity {

    private String uuid;
    //主信息表uuid
    private String puuid;
    //附件显示名称
    private String fjmc;
    //附件二进制流
    @UnCloneable
    private Blob body;

    public String getPuuid() {
        return puuid;
    }

    public void setPuuid(String puuid) {
        this.puuid = puuid;
    }

    public String getFjmc() {
        return fjmc;
    }

    public void setFjmc(String fjmc) {
        this.fjmc = fjmc;
    }

    public Blob getBody() {
        return body;
    }

    public void setBody(Blob body) {
        this.body = body;
    }
}
