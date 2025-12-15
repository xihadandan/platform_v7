package com.wellsoft.pt.basicdata.viewcomponent.facade.support.calendar;

import com.drew.lang.annotations.NotNull;
import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.MappedSuperclass;
import java.util.Date;

/**
 * Description: 日程事项基础类
 *
 * @author {Svn璐﹀彿}
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年5月9日.1	{Svn璐﹀彿}		2018年5月9日		Create
 * </pre>
 * @date 2018年5月9日
 */
@MappedSuperclass
public class CalendarEventEntity extends TenantEntity {

    public static final String BELONG_OBJ_ID = "belongObjId";

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 3710572780675135592L;

    @NotBlank
    private String title; // 事项标题
    @NotNull
    private Date startTime; // 事项开始时间
    @NotNull
    private Date endTime; // 事项结束时间
    private String eventContent; // 事项内容
    @NotBlank
    private String belongObjId; // 事项归属资源对象ID

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title 要设置的title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the content
     */
    public String getEventContent() {
        return eventContent;
    }

    /**
     * @param content 要设置的content
     */
    public void setEventContent(String content) {
        this.eventContent = content;
    }

    /**
     * @return the belongObjId
     */

    public String getBelongObjId() {
        return belongObjId;
    }

    /**
     * @param belongObjId 要设置的belongObjId
     */
    public void setBelongObjId(String belongObjId) {
        this.belongObjId = belongObjId;
    }

    /**
     * @return the startTime
     */

    public Date getStartTime() {
        return startTime;
    }

    /**
     * @param startTime 要设置的startTime
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the endTime
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @param endTime 要设置的endTime
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

}
