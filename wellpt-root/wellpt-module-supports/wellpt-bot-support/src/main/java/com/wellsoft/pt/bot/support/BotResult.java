package com.wellsoft.pt.bot.support;

import java.io.Serializable;

/**
 * Description: 单据转换结果
 *
 * @author chenq
 * @date 2018/9/18
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/9/18    chenq		2018/9/18		Create
 * </pre>
 */
public class BotResult implements Serializable {

    private String dataUuid;

    private Object data;

    private Object dyformData;

    /**
     * 构造方法
     *
     * @param dataUuid   数据UUID
     * @param data       结果数据
     * @param dyformData 表单数据
     */
    public BotResult(String dataUuid, Object data, Object dyformData) {
        this.dataUuid = dataUuid;
        this.data = data;
        this.dyformData = dyformData;
    }

    /**
     * 获取数据UUID
     *
     * @return
     */
    public String getDataUuid() {
        return dataUuid;
    }

    /**
     * 设置数据UUID
     *
     * @param dataUuid
     */
    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

    /**
     * 获取结果数据
     *
     * @return
     */
    public Object getData() {
        return data;
    }

    /**
     * 设置结果数据
     *
     * @param data
     */
    public void setData(Object data) {
        this.data = data;
    }

    /**
     * @return
     */
    public Object getDyformData() {
        return dyformData;
    }

    /**
     * @param dyformData
     */
    public void setDyformData(Object dyformData) {
        this.dyformData = dyformData;
    }

}
