/*
 * @(#)2018-03-29 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.viewcomponent.facade.support.calendar;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 * @author
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018-03-29.1	raolq		2018-03-29		Create
 * </pre>
 * @date 2018-03-29
 */
@Entity
@Table(name = "MY_AGENDA_EVENT")
@DynamicUpdate
@DynamicInsert
public class MyAgendaEventEntity extends CalendarEventEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1522302769825L;

}
