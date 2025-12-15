package com.wellsoft.pt.dms.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Clob;

/**
 * Description: 文档交换-操作日志
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
@Table(name = "DMS_DOC_EXCHANGE_LOG")
@DynamicUpdate
@DynamicInsert
@Entity
public class DmsDocExchangeLogEntity extends TenantEntity {
    private static final long serialVersionUID = 8652194329647531585L;

    private String operationName;

    private String operator;

    private Clob target;

    private String content;

    private String fileUuids;

    private String fileNames;

    private String docExchangeRecordUuid;

    public DmsDocExchangeLogEntity() {
    }

    public DmsDocExchangeLogEntity(String docExchangeRecordUuid, String operationName,
                                   String operator, Clob target, String content,
                                   String fileUuids, String fileNames) {
        this.operationName = operationName;
        this.operator = operator;
        this.target = target;
        this.content = content;
        this.fileUuids = fileUuids;
        this.fileNames = fileNames;
        this.docExchangeRecordUuid = docExchangeRecordUuid;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Clob getTarget() {
        return target;
    }

    public void setTarget(Clob target) {
        this.target = target;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileUuids() {
        return fileUuids;
    }

    public void setFileUuids(String fileUuids) {
        this.fileUuids = fileUuids;
    }

    public String getDocExchangeRecordUuid() {
        return docExchangeRecordUuid;
    }

    public void setDocExchangeRecordUuid(String docExchangeRecordUuid) {
        this.docExchangeRecordUuid = docExchangeRecordUuid;
    }

    public String getFileNames() {
        return fileNames;
    }

    public void setFileNames(String fileNames) {
        this.fileNames = fileNames;
    }
}
