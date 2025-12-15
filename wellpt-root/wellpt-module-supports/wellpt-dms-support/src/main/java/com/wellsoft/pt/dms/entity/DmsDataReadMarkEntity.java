package com.wellsoft.pt.dms.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 数据标记-已读
 *
 * @author chenq
 * @date 2018/6/15
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/6/15    chenq		2018/6/15		Create
 * </pre>
 */
@Table(name = "DMS_DATA_READ_MARK")
@Entity
@DynamicInsert
@DynamicUpdate
public class DmsDataReadMarkEntity extends DataMarkEntity {
}
