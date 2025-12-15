package com.wellsoft.pt.basicdata.serialnumber.support;

import com.google.common.collect.Maps;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/1/18
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/1/18    chenq		2019/1/18		Create
 * </pre>
 */
public class SerialNumberBuildParams implements Serializable {
    private static final long serialVersionUID = 9129049703346866436L;

    @NotNull
    private String serialNumberId;//流水号定义ID

    private Boolean occupied = false;//是否占用，如果占用，则保存当前流水

    private Boolean defaultTransaction = false;

    private String formUuid;//表单UUID

    private String tableName;//表名

    private String dataUuid;//数据uuid

    private String formField;//表单字段

    private Boolean automaticNumberSupplement = false; //自动补最小流水号

    private Boolean queryByHeadAndLast = false; //是否按头尾查询 用于不同头尾的流水号维护值计算

    private String headPart; //头部

    private String lastPart; //尾部

    private String uuid;

    private String snValue;

    private boolean isBackEnd = false;

    // 补号记录UUID
    private String recordUuid;

    // 占用的指针
    private Long occupiedPointer;

    private Map<String, Object> renderParams = Maps.newHashMap();//其他可供流水号模板渲染的参数

    public String getSerialNumberId() {
        return serialNumberId;
    }

    public void setSerialNumberId(String serialNumberId) {
        this.serialNumberId = serialNumberId;
    }

    public Boolean getOccupied() {
        return occupied;
    }

    public void setOccupied(Boolean occupied) {
        this.occupied = occupied;
    }

    public String getFormUuid() {
        return formUuid;
    }

    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    public String getFormField() {
        return formField;
    }

    public void setFormField(String formField) {
        this.formField = formField;
    }

    public Map<String, Object> getRenderParams() {
        return renderParams;
    }

    public void setRenderParams(Map<String, Object> renderParams) {
        this.renderParams = renderParams;
    }

    public Boolean getDefaultTransaction() {
        return defaultTransaction;
    }

    public void setDefaultTransaction(Boolean defaultTransaction) {
        this.defaultTransaction = defaultTransaction;
    }

    public Boolean getAutomaticNumberSupplement() {
        return automaticNumberSupplement;
    }

    public void setAutomaticNumberSupplement(Boolean automaticNumberSupplement) {
        this.automaticNumberSupplement = automaticNumberSupplement;
    }

    public String getHeadPart() {
        return headPart;
    }

    public void setHeadPart(String headPart) {
        this.headPart = headPart;
    }

    public String getLastPart() {
        return lastPart;
    }

    public void setLastPart(String lastPart) {
        this.lastPart = lastPart;
    }

    public Boolean getQueryByHeadAndLast() {
        return queryByHeadAndLast;
    }

    public void setQueryByHeadAndLast(Boolean queryByHeadAndLast) {
        this.queryByHeadAndLast = queryByHeadAndLast;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDataUuid() {
        return dataUuid;
    }

    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSnValue() {
        return snValue;
    }

    public void setSnValue(String snValue) {
        this.snValue = snValue;
    }

    public boolean getIsBackEnd() {
        return isBackEnd;
    }

    public void setIsBackEnd(boolean isBackEnd) {
        this.isBackEnd = isBackEnd;
    }

    /**
     * @return the recordUuid
     */
    public String getRecordUuid() {
        return recordUuid;
    }

    /**
     * @param recordUuid 要设置的recordUuid
     */
    public void setRecordUuid(String recordUuid) {
        this.recordUuid = recordUuid;
    }

    /**
     * @return the occupiedPointer
     */
    public Long getOccupiedPointer() {
        return occupiedPointer;
    }

    /**
     * @param occupiedPointer 要设置的occupiedPointer
     */
    public void setOccupiedPointer(Long occupiedPointer) {
        this.occupiedPointer = occupiedPointer;
    }
}
