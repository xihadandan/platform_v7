package com.wellsoft.pt.dms.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 文档交换-催办
 *
 * @author chenq
 * @date 2018/5/18
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/18    chenq		2018/5/18		Create
 * </pre>
 */
@Table(name = "DMS_DOC_EXC_URGE_DETAIL")
@Entity
@DynamicInsert
@DynamicUpdate
public class DmsDocExcUrgeDetailEntity extends TenantEntity {
    private static final long serialVersionUID = 4941304210270391971L;

    private String toUserId;

    private String content;

    private String urgeWay;

    private String docExchangeRecordUuid;

    public DmsDocExcUrgeDetailEntity() {
    }

    public DmsDocExcUrgeDetailEntity(String toUserId, String content, String urgeWay,
                                     String docExchangeRecordUuid) {
        this.toUserId = toUserId;
        this.content = content;
        this.urgeWay = urgeWay;
        this.docExchangeRecordUuid = docExchangeRecordUuid;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrgeWay() {
        return urgeWay;
    }

    public void setUrgeWay(String urgeWay) {
        this.urgeWay = urgeWay;
    }

    public String getDocExchangeRecordUuid() {
        return docExchangeRecordUuid;
    }

    public void setDocExchangeRecordUuid(String docExchangeRecordUuid) {
        this.docExchangeRecordUuid = docExchangeRecordUuid;
    }
}
