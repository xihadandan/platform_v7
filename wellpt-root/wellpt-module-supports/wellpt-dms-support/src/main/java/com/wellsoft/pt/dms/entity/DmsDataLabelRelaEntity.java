package com.wellsoft.pt.dms.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 标签与数据的关联
 *
 * @author chenq
 * @date 2018/6/11
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/6/11    chenq		2018/6/11		Create
 * </pre>
 */
@Table(name = "DMS_DATA_LABEL_RELA")
@Entity
@DynamicInsert
@DynamicUpdate
public class DmsDataLabelRelaEntity extends DataLabelRelaEntity {
}
