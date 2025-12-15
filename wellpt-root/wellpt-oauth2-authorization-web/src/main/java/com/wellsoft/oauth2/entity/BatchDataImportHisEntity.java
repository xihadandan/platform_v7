package com.wellsoft.oauth2.entity;

import com.wellsoft.oauth2.enums.DataImportTypeEnum;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/9/25
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/9/25    chenq		2019/9/25		Create
 * </pre>
 */
@Entity
@Table(name = "batch_data_import_his")
@DynamicInsert
@DynamicUpdate
public class BatchDataImportHisEntity extends BaseEntity {

    private DataImportTypeEnum dataImportType;


    @Transient
    private List<BatchDataImportDetailsHisEntity> importDetailsHisEntities;

    public DataImportTypeEnum getDataImportType() {
        return dataImportType;
    }

    public void setDataImportType(DataImportTypeEnum dataImportType) {
        this.dataImportType = dataImportType;
    }

    public List<BatchDataImportDetailsHisEntity> getImportDetailsHisEntities() {
        return importDetailsHisEntities;
    }

    public void setImportDetailsHisEntities(
            List<BatchDataImportDetailsHisEntity> importDetailsHisEntities) {
        this.importDetailsHisEntities = importDetailsHisEntities;
    }
}
