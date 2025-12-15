package com.wellsoft.pt.basicdata.workhour.bean;

import com.wellsoft.pt.basicdata.workhour.entity.WorkHour;

import java.util.HashSet;
import java.util.Set;

/**
 * Description: 工作时间VO类
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-18.1	zhouyq		2013-4-18		Create
 * </pre>
 * @date 2013-4-18
 */
public class WorkHourBean extends WorkHour {
    private static final long serialVersionUID = 1L;
    // jqGrid默认传过来的行标识
    private String id;

    private Set<WorkHourBean> changedFixedHolidays = new HashSet<WorkHourBean>();
    private Set<WorkHourBean> changedSpecialHolidays = new HashSet<WorkHourBean>();
    private Set<WorkHourBean> changedMakeups = new HashSet<WorkHourBean>();
    private Set<WorkHourBean> deletedFixedHolidays = new HashSet<WorkHourBean>();
    private Set<WorkHourBean> deletedSpecialHolidays = new HashSet<WorkHourBean>();
    private Set<WorkHourBean> deletedMakeups = new HashSet<WorkHourBean>();

    /**
     * @return the changedFixedHolidays
     */
    public Set<WorkHourBean> getChangedFixedHolidays() {
        return changedFixedHolidays;
    }

    /**
     * @param changedFixedHolidays 要设置的changedFixedHolidays
     */
    public void setChangedFixedHolidays(Set<WorkHourBean> changedFixedHolidays) {
        this.changedFixedHolidays = changedFixedHolidays;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the changedSpecialHolidays
     */
    public Set<WorkHourBean> getChangedSpecialHolidays() {
        return changedSpecialHolidays;
    }

    /**
     * @param changedSpecialHolidays 要设置的changedSpecialHolidays
     */
    public void setChangedSpecialHolidays(Set<WorkHourBean> changedSpecialHolidays) {
        this.changedSpecialHolidays = changedSpecialHolidays;
    }

    /**
     * @return the changedMakeups
     */
    public Set<WorkHourBean> getChangedMakeups() {
        return changedMakeups;
    }

    /**
     * @param changedMakeups 要设置的changedMakeups
     */
    public void setChangedMakeups(Set<WorkHourBean> changedMakeups) {
        this.changedMakeups = changedMakeups;
    }

    /**
     * @return the deletedFixedHolidays
     */
    public Set<WorkHourBean> getDeletedFixedHolidays() {
        return deletedFixedHolidays;
    }

    /**
     * @param deletedFixedHolidays 要设置的deletedFixedHolidays
     */
    public void setDeletedFixedHolidays(Set<WorkHourBean> deletedFixedHolidays) {
        this.deletedFixedHolidays = deletedFixedHolidays;
    }

    /**
     * @return the deletedSpecialHolidays
     */
    public Set<WorkHourBean> getDeletedSpecialHolidays() {
        return deletedSpecialHolidays;
    }

    /**
     * @param deletedSpecialHolidays 要设置的deletedSpecialHolidays
     */
    public void setDeletedSpecialHolidays(Set<WorkHourBean> deletedSpecialHolidays) {
        this.deletedSpecialHolidays = deletedSpecialHolidays;
    }

    /**
     * @return the deletedMakeups
     */
    public Set<WorkHourBean> getDeletedMakeups() {
        return deletedMakeups;
    }

    /**
     * @param deletedMakeups 要设置的deletedMakeups
     */
    public void setDeletedMakeups(Set<WorkHourBean> deletedMakeups) {
        this.deletedMakeups = deletedMakeups;
    }

}
