package com.wellsoft.pt.task;

import org.quartz.Trigger;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/6/15
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/6/15    chenq		2019/6/15		Create
 * </pre>
 */
public enum JobStateEnum {
    RUNNING(0, "运行中"), PAUSED(1, "暂停中"), FINISHED(2, "已完成"), ERROR(3, "错误"), BLOCKING(4,
            "阻塞中"), STOPED(-1, "已停止");


    private Integer code;
    private String name;

    JobStateEnum(Integer code, String name) {
        this.name = name;
        this.code = code;
    }

    /**
     * quartz2.3的状态转换
     *
     * @param triggerKey
     * @return
     */
    public static JobStateEnum fromTriggerState(Trigger.TriggerState state) {

        switch (state) {
            case PAUSED:
                return PAUSED;
            case NORMAL:
                return RUNNING;
            case ERROR:
                return ERROR;
            case BLOCKED:
                return BLOCKING;
            case COMPLETE:
                return FINISHED;
            case NONE:
                return STOPED;
            default:
                return FINISHED;
        }


    }

    public static String name(Integer code) {
        JobStateEnum[] enums = JobStateEnum.values();
        for (JobStateEnum e : enums) {
            if (e.getCode().equals(code)) {
                return e.getName();
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
